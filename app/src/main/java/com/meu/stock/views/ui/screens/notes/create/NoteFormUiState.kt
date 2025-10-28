package com.meu.stock.views.ui.screens.notes.create

/**
 * Representa o estado da UI para a tela de formulário de notas.
 */
data class NoteFormUiState(
    val isLoading: Boolean = false,
    val isEditing: Boolean = false,
    val saveSuccess: Boolean = false,
    val title: String = "",
    val content: String = "",
    val reminderDate: String = "",
    val reminderTime: String = "", // <- CAMPO ADICIONADO
    val errorMessage: String? = null
)
