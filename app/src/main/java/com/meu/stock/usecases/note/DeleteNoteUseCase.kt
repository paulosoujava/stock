package com.meu.stock.usecases.note

import com.meu.stock.contracts.INoteRepository
import javax.inject.Inject

class DeleteNoteUseCase @Inject constructor(
    private val noteRepository: INoteRepository) {
    suspend operator fun invoke(id: Long) {
        noteRepository.deleteNoteById(id)
    }
}
