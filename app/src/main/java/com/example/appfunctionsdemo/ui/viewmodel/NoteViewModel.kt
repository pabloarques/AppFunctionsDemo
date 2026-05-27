package com.example.appfunctionsdemo.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appfunctionsdemo.data.NoteRepository
import com.example.appfunctionsdemo.model.Note
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel que gestiona el estado de la pantalla principal (NoteDashboard).
 *
 * Observa reactivamente los cambios en Room a través de [NoteRepository.allNotes]
 * y expone estados derivados (filtrado por búsqueda) como [StateFlow] inmutables
 * para la capa de presentación Compose.
 */
class NoteViewModel(private val repository: NoteRepository) : ViewModel() {

    /**
     * Stream reactivo de todas las notas desde Room.
     * Se actualiza automáticamente ante cualquier escritura, incluso
     * desde el proceso de servicio de AppFunctions.
     */
    val notesList: StateFlow<List<Note>> = repository.allNotes
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _showAddDialog = MutableStateFlow(false)
    val showAddDialog: StateFlow<Boolean> = _showAddDialog.asStateFlow()

    /**
     * Lista filtrada que se recalcula automáticamente cuando cambia
     * el listado en base de datos o la consulta de búsqueda del usuario.
     */
    val filteredNotes: StateFlow<List<Note>> = combine(notesList, _searchQuery) { notes, query ->
        if (query.isBlank()) {
            notes
        } else {
            notes.filter { note ->
                note.title.contains(query, ignoreCase = true) ||
                        note.content.contains(query, ignoreCase = true)
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun setShowAddDialog(show: Boolean) {
        _showAddDialog.value = show
    }

    fun addNote(title: String, content: String) {
        viewModelScope.launch {
            repository.create(title, content)
        }
    }

    fun deleteNote(id: String) {
        viewModelScope.launch {
            repository.delete(id)
        }
    }
}
