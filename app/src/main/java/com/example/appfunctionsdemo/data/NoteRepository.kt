package com.example.appfunctionsdemo.data

import com.example.appfunctionsdemo.model.Note
import kotlinx.coroutines.flow.Flow
import java.util.UUID

/**
 * Repositorio de producción que envuelve las operaciones de acceso a datos de Room (NoteDao).
 * Permite exponer flujos de datos reactivos (Flow) y realizar escrituras asíncronas seguras.
 */
class NoteRepository(private val noteDao: NoteDao) {

    /**
     * Retorna un Flow reactivo con el listado de todas las notas.
     */
    val allNotesFlow: Flow<List<Note>> = noteDao.getAllNotesFlow()

    /**
     * Retorna todas las notas de forma directa.
     */
    suspend fun getAllDirect(): List<Note> {
        return noteDao.getAllNotesDirect()
    }

    /**
     * Añade una nota generando un UUID.
     */
    suspend fun add(title: String, content: String): Note {
        val note = Note(
            id = UUID.randomUUID().toString(),
            title = title,
            content = content
        )
        noteDao.insertNote(note)
        return note
    }

    /**
     * Inserta una nota específica (usada en invocaciones externas/headless).
     */
    suspend fun addNote(note: Note): Note {
        noteDao.insertNote(note)
        return note
    }

    /**
     * Elimina una nota por su identificador único.
     */
    suspend fun delete(id: String) {
        noteDao.deleteNoteById(id)
    }
}
