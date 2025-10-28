package com.meu.stock.usecases


import com.meu.stock.contracts.INoteRepository
import com.meu.stock.model.Note
import javax.inject.Inject

// GetNoteByIdUseCase
class GetNoteByIdUseCase @Inject constructor(private val noteRepository: INoteRepository) {
    suspend operator fun invoke(id: Long): Note? {
        return noteRepository.getNoteById(id)
    }
}


