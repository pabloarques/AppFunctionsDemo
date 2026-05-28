package com.example.appfunctionsdemo.ui.viewmodel

import com.example.appfunctionsdemo.model.Note

/**
 * Estado unificado e inmutable de la pantalla NoteDashboard.
 *
 * Representa toda la información necesaria para renderizar la UI
 * como una única fuente de verdad, siguiendo el patrón MVI.
 */
data class NoteDashboardUiState(
    val allNotes: List<Note> = emptyList(),
    val filteredNotes: List<Note> = emptyList(),
    val searchQuery: String = "",
    val showAddDialog: Boolean = false,
    val isLoading: Boolean = true,
    val error: String? = null
)
