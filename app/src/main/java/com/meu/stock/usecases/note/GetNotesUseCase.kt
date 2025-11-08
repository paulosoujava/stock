package com.meu.stock.usecases.note



import com.meu.stock.contracts.INoteRepository
import com.meu.stock.model.Note
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetNotesUseCase @Inject constructor(
    private val noteRepository: INoteRepository
) {
    /**
     * Retorna um fluxo contínuo com a lista de todas as notas,
     * ordenadas pela data de atualização mais recente.
     */
    operator fun invoke(): Flow<List<Note>> {
        return noteRepository.getAllNotes()
    }
}
