package com.example.appfunctionsdemo.functions

import androidx.appfunctions.AppFunctionContext
import androidx.appfunctions.service.AppFunction
import com.example.appfunctionsdemo.data.NoteRepository
import com.example.appfunctionsdemo.model.Note
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Punto de entrada headless de la aplicación para agentes de IA.
 *
 * Cada método anotado con [@AppFunction] es descubierto e indexado por el sistema operativo
 * (Android 16+), permitiendo que asistentes como Gemini los invoquen en segundo plano
 * sin necesidad de abrir la interfaz gráfica.
 *
 * El framework instancia esta clase internamente dentro del proceso de servicio
 * (`AppFunctionService`), por lo que las dependencias se resuelven a través de
 * [KoinComponent] en lugar de inyección por constructor.
 *
 * La descripción semántica de cada función (KDoc) es utilizada directamente por
 * la IA para decidir cuándo y cómo invocarla (`isDescribedByKDoc = true`).
 */
class NoteFunctions : KoinComponent {

    private val repository: NoteRepository by inject()

    /**
     * Recupera las notas almacenadas en el dispositivo.
     * Opcionalmente filtra por un término de búsqueda que se compara
     * contra el título y el contenido de cada nota.
     *
     * @param appFunctionContext Contexto de ejecución proporcionado por el sistema.
     * @param query Término de búsqueda opcional. Si es nulo o vacío, devuelve todas las notas.
     * @return Lista de notas que coinciden con los criterios de búsqueda.
     */
    @AppFunction(isDescribedByKDoc = true)
    suspend fun getNotes(
        appFunctionContext: AppFunctionContext,
        query: String?
    ): List<Note> {
        val notes = repository.getAll()

        if (query.isNullOrBlank()) return notes

        val normalizedQuery = query.trim()
        return notes.filter { note ->
            note.title.contains(normalizedQuery, ignoreCase = true) ||
                    note.content.contains(normalizedQuery, ignoreCase = true)
        }
    }

    /**
     * Crea una nueva nota y la persiste en la base de datos local.
     * La UI se actualiza automáticamente de forma reactiva a través de Room Flow,
     * sin necesidad de comunicación explícita entre procesos.
     *
     * @param appFunctionContext Contexto de ejecución proporcionado por el sistema.
     * @param title Título de la nota.
     * @param content Cuerpo o contenido de la nota.
     * @return La nota recién creada con su identificador único asignado.
     */
    @AppFunction(isDescribedByKDoc = true)
    suspend fun createNote(
        appFunctionContext: AppFunctionContext,
        title: String,
        content: String
    ): Note = repository.create(title, content)

    /**
     * Elimina una nota existente por su identificador único.
     *
     * @param appFunctionContext Contexto de ejecución proporcionado por el sistema.
     * @param id Identificador único de la nota a eliminar.
     * @return `true` si la nota existía y fue eliminada, `false` si no se encontró.
     */
    @AppFunction(isDescribedByKDoc = true)
    suspend fun deleteNote(
        appFunctionContext: AppFunctionContext,
        id: String
    ): Boolean = repository.delete(id)
}
