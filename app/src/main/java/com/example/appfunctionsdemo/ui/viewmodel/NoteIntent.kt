package com.example.appfunctionsdemo.ui.viewmodel

/**
 * Intenciones del usuario que representan todas las acciones posibles
 * en la pantalla NoteDashboard.
 *
 * Cada intención es un evento inmutable que el ViewModel procesa
 * para producir un nuevo estado, siguiendo el flujo unidireccional MVI.
 */
sealed interface NoteIntent {
    /** El usuario modificó el texto de búsqueda. */
    data class SearchQueryChanged(val query: String) : NoteIntent

    /** El usuario pulsó el botón para mostrar el diálogo de creación. */
    data object ShowAddDialog : NoteIntent

    /** El usuario cerró el diálogo de creación. */
    data object HideAddDialog : NoteIntent

    /** El usuario confirmó la creación de una nueva nota. */
    data class AddNote(val title: String, val content: String) : NoteIntent

    /** El usuario solicitó eliminar una nota por su ID. */
    data class DeleteNote(val id: String) : NoteIntent
}
