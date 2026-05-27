package com.example.appfunctionsdemo.data

import com.example.appfunctionsdemo.model.Note
import kotlinx.coroutines.flow.Flow
import java.util.UUID

/**
 * Repositorio que encapsula el acceso a datos de notas.
 *
 * Actúa como única fuente de verdad para la capa de dominio y presentación,
 * exponiendo un [Flow] reactivo para la UI y operaciones suspend para
 * las invocaciones headless de AppFunctions.
 *
 * @param noteDao DAO de Room inyectado por Koin.
 */
class NoteRepository(private val noteDao: NoteDao) {

    /**
     * Stream reactivo de todas las notas. Room invalida automáticamente
     * este Flow ante cualquier escritura, incluso desde otro proceso.
     */
    val allNotes: Flow<List<Note>> = noteDao.observeAll()

    /**
     * Lectura puntual de todas las notas.
     * Diseñado para invocaciones headless (AppFunctions) donde
     * no se necesita una suscripción continua.
     */
    suspend fun getAll(): List<Note> = noteDao.getAll()

    /**
     * Crea y persiste una nueva nota con un identificador único generado.
     *
     * @return La nota creada con su ID asignado.
     */
    suspend fun create(title: String, content: String): Note {
        val note = Note(
            id = UUID.randomUUID().toString(),
            title = title,
            content = content
        )
        noteDao.insert(note)
        return note
    }

    /**
     * Elimina una nota por su identificador.
     *
     * @return `true` si la nota existía y fue eliminada, `false` si no se encontró.
     */
    suspend fun delete(id: String): Boolean = noteDao.deleteById(id) > 0
}
