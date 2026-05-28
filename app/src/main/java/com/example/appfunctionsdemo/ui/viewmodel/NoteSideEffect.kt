package com.example.appfunctionsdemo.ui.viewmodel

/**
 * Efectos secundarios de una sola emisión (one-shot events)
 * que no forman parte del estado persistente de la UI.
 *
 * Se emiten a través de un [Channel] y se consumen una única vez
 * mediante [LaunchedEffect] en la capa de Compose.
 */
sealed interface NoteSideEffect {
    /** Muestra un Snackbar con un mensaje informativo. */
    data class ShowSnackbar(val message: String) : NoteSideEffect

    /** Muestra un Snackbar con un mensaje de error. */
    data class ShowError(val message: String) : NoteSideEffect
}
