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
 * ViewModel que gestiona el estado de la UI y los eventos del NoteDashboard.
 * 
 * Implementa Clean Architecture & patrones MVVM modernos utilizando StateFlows reactivos
 * basados en Room Database + Flows, eliminando por completo los acoples de Broadcasts locales.
 */
class NoteViewModel(private val noteRepository: NoteRepository) : ViewModel() {

    // El listado general ahora se observa de forma reactiva desde Room Flow
    val notesList: StateFlow<List<Note>> = noteRepository.allNotesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _showAddDialog = MutableStateFlow(false)
    val showAddDialog: StateFlow<Boolean> = _showAddDialog.asStateFlow()

    /**
     * Lista filtrada reactiva que se recalcula automáticamente cuando
     * cambia el listado en base de datos local o la consulta de búsqueda.
     */
    val filteredNotes: StateFlow<List<Note>> = combine(notesList, _searchQuery) { notes, query ->
        if (query.isBlank()) {
            notes
        } else {
            notes.filter {
                it.title.contains(query, ignoreCase = true) ||
                        it.content.contains(query, ignoreCase = true)
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
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
            noteRepository.add(title, content)
        }
    }

    fun deleteNote(id: String) {
        viewModelScope.launch {
            noteRepository.delete(id)
        }
    }
}
