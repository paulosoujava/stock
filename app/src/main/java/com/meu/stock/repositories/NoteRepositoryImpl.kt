package com.meu.stock.repositories


import com.meu.stock.contracts.INoteRepository
import com.meu.stock.data.note.NoteDao
import com.meu.stock.mappers.toNote
import com.meu.stock.mappers.toNoteEntity
import com.meu.stock.model.Note
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class NoteRepositoryImpl @Inject constructor(
    private val noteDao: NoteDao
) : INoteRepository {

    override fun getAllNotes(): Flow<List<Note>> {
        // Pega as entidades do DAO e as mapeia para o modelo de UI
        return noteDao.getAllNotes().map { entities ->
            entities.map { it.toNote() }
        }
    }

    override suspend fun saveNote(note: Note): Long { // <- MUDANÇA AQUI
        // Mapeia o modelo de UI para a entidade do banco e salva
        return noteDao.saveNote(note.toNoteEntity())
    }

    override suspend fun deleteNoteById(id: Long) {
        noteDao.deleteNoteById(id)
    }
    override suspend fun getNoteById(id: Long): Note? {
        // Busca a entidade pelo DAO. Se não for nula, mapeia para o modelo de UI.
        return noteDao.getNoteById(id)?.toNote()
    }
    override fun getNoteCount(): Flow<Int> {
        return noteDao.getNoteCount()
    }


}
