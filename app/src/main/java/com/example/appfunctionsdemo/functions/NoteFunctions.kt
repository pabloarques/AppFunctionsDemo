package com.example.appfunctionsdemo.functions

import androidx.appfunctions.AppFunctionContext
import androidx.appfunctions.service.AppFunction
import com.example.appfunctionsdemo.data.NoteRepository
import com.example.appfunctionsdemo.model.Note
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Expone las AppFunctions de la aplicación que el sistema operativo y los asistentes
 * de Inteligencia Artificial pueden invocar de manera headless (en segundo plano).
 * 
 * Implementa KoinComponent para resolver de forma limpia y profesional las dependencias
 * (como NoteRepository) gestionadas por el motor de DI.
 */
class NoteFunctions : KoinComponent {

    private val noteRepository: NoteRepository by inject()

    /**
     * Recupera la lista de notas guardadas en la persistencia local de Room.
     * Puede filtrar los resultados mediante un término de búsqueda.
     *
     * @param appFunctionContext El contexto de ejecución de la AppFunction.
     * @param query Término de búsqueda opcional para filtrar las notas por título o contenido.
     * @return Una lista de notas que coinciden con los criterios de búsqueda.
     */
    @AppFunction(isDescribedByKDoc = true)
    suspend fun getNotes(
        appFunctionContext: AppFunctionContext,
        query: String?
    ): List<Note> {
        val notes = noteRepository.getAllDirect()
        return if (query.isNullOrBlank()) {
            notes
        } else {
            notes.filter {
                it.title.contains(query, ignoreCase = true) ||
                        it.content.contains(query, ignoreCase = true)
            }
        }
    }

    /**
     * Crea y guarda una nueva nota en la base de datos Room de forma directa.
     * Al persistirse en Room, cualquier Flow activo de la UI Compose se actualizará
     * de forma reactiva y automática entre los procesos de la app.
     *
     * @param appFunctionContext El contexto de ejecución de la AppFunction.
     * @param title El título que tendrá la nota creada.
     * @param content El texto o cuerpo de la nota creada.
     * @return La nota recién creada con su identificador único asignado.
     */
    @AppFunction(isDescribedByKDoc = true)
    suspend fun createNote(
        appFunctionContext: AppFunctionContext,
        title: String,
        content: String
    ): Note {
        return noteRepository.add(title, content)
    }

    /**
     * Elimina una nota existente de la base de datos local a partir de su identificador único.
     *
     * @param appFunctionContext El contexto de ejecución de la AppFunction.
     * @param id El identificador único de la nota que se desea eliminar.
     * @return Verdadero si la nota existía y fue eliminada exitosamente, falso en caso contrario.
     */
    @AppFunction(isDescribedByKDoc = true)
    suspend fun deleteNote(
        appFunctionContext: AppFunctionContext,
        id: String
    ): Boolean {
        val exists = noteRepository.getAllDirect().any { it.id == id }
        if (exists) {
            noteRepository.delete(id)
        }
        return exists
    }
}
