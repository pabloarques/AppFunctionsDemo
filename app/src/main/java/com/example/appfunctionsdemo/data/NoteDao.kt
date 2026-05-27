package com.example.appfunctionsdemo.data

import androidx.room.*
import com.example.appfunctionsdemo.model.Note
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) para realizar operaciones CRUD reactivas en la tabla de notas de Room.
 */
@Dao
interface NoteDao {
    @Query("SELECT * FROM notes ORDER BY id DESC")
    fun getAllNotesFlow(): Flow<List<Note>>

    @Query("SELECT * FROM notes")
    suspend fun getAllNotesDirect(): List<Note>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note)

    @Query("DELETE FROM notes WHERE id = :id")
    suspend fun deleteNoteById(id: String)
}
