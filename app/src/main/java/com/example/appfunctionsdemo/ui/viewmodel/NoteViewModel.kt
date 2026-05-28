package com.example.appfunctionsdemo.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appfunctionsdemo.data.NoteRepository
import com.example.appfunctionsdemo.model.Note
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NoteViewModel(private val repository: NoteRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(NoteDashboardUiState())
    val uiState: StateFlow<NoteDashboardUiState> = _uiState.asStateFlow()

    private val _sideEffect = Channel<NoteSideEffect>(Channel.BUFFERED)
    val sideEffect: Flow<NoteSideEffect> = _sideEffect.receiveAsFlow()

    init {
        observeNotes()
    }

    fun processIntent(intent: NoteIntent) {
        when (intent) {
            is NoteIntent.SearchQueryChanged -> onSearchQueryChanged(intent.query)
            is NoteIntent.ShowAddDialog -> _uiState.update { it.copy(showAddDialog = true) }
            is NoteIntent.HideAddDialog -> _uiState.update { it.copy(showAddDialog = false) }
            is NoteIntent.AddNote -> addNote(intent.title, intent.content)
            is NoteIntent.DeleteNote -> deleteNote(intent.id)
        }
    }

    private fun observeNotes() {
        repository.allNotes
            .onEach { notes ->
                _uiState.update { state ->
                    state.copy(
                        allNotes = notes,
                        filteredNotes = filterNotes(notes, state.searchQuery),
                        isLoading = false
                    )
                }
            }
            .catch { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
            .launchIn(viewModelScope)
    }

    private fun onSearchQueryChanged(query: String) {
        _uiState.update { state ->
            state.copy(
                searchQuery = query,
                filteredNotes = filterNotes(state.allNotes, query)
            )
        }
    }

    private fun addNote(title: String, content: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                repository.create(title, content)
                _uiState.update { it.copy(showAddDialog = false, isLoading = false) }
                _sideEffect.send(NoteSideEffect.ShowSnackbar("Nota creada exitosamente"))
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
                _sideEffect.send(NoteSideEffect.ShowError("Error al crear la nota"))
            }
        }
    }

    private fun deleteNote(id: String) {
        viewModelScope.launch {
            try {
                val deleted = repository.delete(id)
                if (deleted) {
                    _sideEffect.send(NoteSideEffect.ShowSnackbar("Nota eliminada"))
                } else {
                    _sideEffect.send(NoteSideEffect.ShowError("No se encontró la nota"))
                }
            } catch (e: Exception) {
                _sideEffect.send(NoteSideEffect.ShowError("Error al eliminar la nota"))
            }
        }
    }

    private fun filterNotes(notes: List<Note>, query: String): List<Note> =
        if (query.isBlank()) notes
        else notes.filter { note ->
            note.title.contains(query, ignoreCase = true) ||
                    note.content.contains(query, ignoreCase = true)
        }
}
