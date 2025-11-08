package com.meu.stock.views.ui.screens.notes.create

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meu.stock.contracts.IAlarmScheduler
import com.meu.stock.model.Note
import com.meu.stock.usecases.note.DeleteNoteUseCase
import com.meu.stock.usecases.note.GetNoteByIdUseCase
import com.meu.stock.usecases.note.SaveNoteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class NoteFormViewModel @Inject constructor(
    private val getNoteByIdUseCase: GetNoteByIdUseCase,
    private val saveNoteUseCase: SaveNoteUseCase, // **Use case DEVE retornar Long**
    private val deleteNoteUseCase: DeleteNoteUseCase,
    private val alarmScheduler: IAlarmScheduler,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(NoteFormUiState())
    val uiState = _uiState.asStateFlow()

    private val noteId: Long? = savedStateHandle.get<String>("noteId")?.toLongOrNull()

    private val _showDeleteConfirmDialog = MutableStateFlow(false)
    val showDeleteConfirmDialog = _showDeleteConfirmDialog.asStateFlow()

    private val _deleteSuccess = MutableStateFlow(false)
    val deleteSuccess = _deleteSuccess.asStateFlow()

    init {
        if (noteId != null) {
            _uiState.update { it.copy(isEditing = true, isLoading = true) }
            loadNoteForEditing(noteId)
        }
    }

    private fun loadNoteForEditing(id: Long) {
        viewModelScope.launch {
            getNoteByIdUseCase(id)?.let { note ->
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        title = note.title,
                        content = note.content,
                        reminderDate = note.reminderDate ?: "",
                        reminderTime = note.reminderTime ?: ""
                    )
                }
            } ?: _uiState.update { it.copy(isLoading = false, errorMessage = "Nota não encontrada.") }
        }
    }

    fun onTitleChange(newTitle: String) {
        _uiState.update { it.copy(title = newTitle) }
    }

    fun onContentChange(newContent: String) {
        _uiState.update { it.copy(content = newContent) }
    }

    fun onDateSelected(dateInMillis: Long) {
        // BUG CORRIGIDO: Interpreta a data em UTC para evitar problemas de fuso.
        val localDate = Instant.ofEpochMilli(dateInMillis).atZone(ZoneOffset.UTC).toLocalDate()
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault())
        _uiState.update { it.copy(reminderDate = localDate.format(formatter)) }
    }

    fun onTimeSelected(hour: Int, minute: Int) {
        _uiState.update { it.copy(reminderTime = String.format("%02d:%02d", hour, minute)) }
    }

    fun saveNote() {
        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState.title.isBlank()) {
                _uiState.update { it.copy(errorMessage = "O título não pode estar vazio.") }
                return@launch
            }

            _uiState.update { it.copy(isLoading = true) }

            val noteToSave = Note(
                id = noteId,
                title = currentState.title.trim(),
                content = currentState.content.trim(),
                lastUpdated = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM, HH:mm")),
                reminderDate = currentState.reminderDate.ifBlank { null },
                reminderTime = currentState.reminderTime.ifBlank { null }
            )

            try {
                // 1. Salva a nota e obtém o ID de volta
                val savedId = saveNoteUseCase(noteToSave)
                val finalNote = noteToSave.copy(id = noteId ?: savedId)

                // 2. Agenda ou cancela o alarme com a nota (que agora tem o ID correto)
                if (finalNote.reminderDate != null && finalNote.reminderTime != null) {
                    alarmScheduler.schedule(finalNote)
                } else if (noteId != null) {
                    alarmScheduler.cancel(finalNote)
                }

                _uiState.update { it.copy(isLoading = false, saveSuccess = true) }

            } catch (e: Exception) {
                Log.e("NoteFormViewModel", "Erro ao salvar nota  ${e.message}"  )
                _uiState.update { it.copy(isLoading = false, errorMessage = "Erro ao salvar: ${e.message}") }
            }
        }
    }

    fun onSaveSuccessShown() {
        _uiState.update { it.copy(saveSuccess = false) }
    }

    fun onErrorMessageShown() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun onDeleteRequest() {
        _showDeleteConfirmDialog.value = true
    }

    fun onDismissDeleteDialog() {
        _showDeleteConfirmDialog.value = false
    }

    fun confirmDeletion() {
        noteId?.let { id ->
            viewModelScope.launch {
                // Para cancelar o alarme, só precisamos do ID.
                val noteToCancel = Note(id = id, title = "", content = "", lastUpdated = "", reminderDate = "", reminderTime = "")
                alarmScheduler.cancel(noteToCancel)
                deleteNoteUseCase(id)
                _deleteSuccess.value = true
                onDismissDeleteDialog()
            }
        }
    }
}
