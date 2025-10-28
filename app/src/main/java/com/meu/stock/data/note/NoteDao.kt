package com.meu.stock.data.note


import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes ORDER BY updatedAt DESC")
    fun getAllNotes(): Flow<List<NoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveNote(note: NoteEntity): Long // <- MUDANÇA AQUI

    @Query("DELETE FROM notes WHERE id = :id")
    suspend fun deleteNoteById(id: Long)


    /**
     * Busca uma única entidade de nota pelo seu ID.
     * Retorna a entidade ou nulo se não for encontrada.
     */
    @Query("SELECT * FROM notes WHERE id = :id LIMIT 1")
    suspend fun getNoteById(id: Long): NoteEntity?


    /**
     * Retorna um fluxo com o número total de notas na tabela.
     * O Flow garante que o número será atualizado automaticamente.
     */
    @Query("SELECT COUNT(id) FROM notes")
    fun getNoteCount(): Flow<Int>
}
