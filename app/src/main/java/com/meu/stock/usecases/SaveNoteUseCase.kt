package com.meu.stock.usecases

import com.meu.stock.contracts.INoteRepository
import com.meu.stock.model.Note
import javax.inject.Inject

class SaveNoteUseCase @Inject constructor(private val noteRepository: INoteRepository) {
    suspend operator fun invoke(note: Note): Long { // <- MUDANÇA AQUI
        return noteRepository.saveNote(note)
    }
}
