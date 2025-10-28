package com.meu.stock.contracts


import com.meu.stock.model.Note
import kotlinx.coroutines.flow.Flow

interface INoteRepository {
    fun getAllNotes(): Flow<List<Note>>

    suspend fun saveNote(note: Note): Long // <- MUDANÇA AQUI
    suspend fun deleteNoteById(id: Long)
    suspend fun getNoteById(id: Long): Note?
    fun getNoteCount(): Flow<Int>

}
