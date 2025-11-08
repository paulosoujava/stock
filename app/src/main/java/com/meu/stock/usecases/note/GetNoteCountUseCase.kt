package com.meu.stock.usecases.note

import com.meu.stock.contracts.INoteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetNoteCountUseCase @Inject constructor(
    private val noteRepository: INoteRepository
) {
    operator fun invoke(): Flow<Int> {
        return noteRepository.getNoteCount()
    }
}
