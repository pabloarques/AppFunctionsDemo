package com.example.appfunctionsdemo.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.appfunctionsdemo.model.Note
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object para operaciones CRUD reactivas sobre la tabla [notes].
 *
 * Expone un [Flow] reactivo para la lectura continua y funciones [suspend]
 * para las escrituras atómicas, siguiendo el patrón recomendado por Room.
 */
@Dao
interface NoteDao {

    /**
     * Observa todas las notas de forma reactiva, ordenadas por fecha de inserción descendente.
     * Room invalida automáticamente este Flow cuando los datos subyacentes cambian,
     * incluso si la escritura ocurre desde otro proceso (AppFunctionService).
     */
    @Query("SELECT * FROM notes ORDER BY id DESC")
    fun observeAll(): Flow<List<Note>>

    /**
     * Lectura puntual de todas las notas, sin suscripción reactiva.
     * Útil para invocaciones headless donde no necesitamos un stream continuo.
     */
    @Query("SELECT * FROM notes ORDER BY id DESC")
    suspend fun getAll(): List<Note>

    /**
     * Inserta una nota o la reemplaza si ya existe una con el mismo [Note.id].
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: Note)

    /**
     * Elimina una nota por su identificador.
     *
     * @return El número de filas afectadas (0 si no existía, 1 si se eliminó).
     */
    @Query("DELETE FROM notes WHERE id = :id")
    suspend fun deleteById(id: String): Int
}
