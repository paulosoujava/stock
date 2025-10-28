package com.meu.stock.views.ui.screens.notes.list

import com.meu.stock.model.Note

data  class NoteListUiState(
    val isLoading: Boolean = true,
    val notes: List<Note> = emptyList(),
    val errorMessage: String? = null
)

