package com.example.appfunctionsdemo.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.appfunctionsdemo.model.Note

/**
 * Base de datos principal de Room para la persistencia local de notas.
 *
 * La instancia es gestionada como singleton por Koin ([di.appModule]),
 * por lo que no se utiliza un companion object con double-checked locking manual.
 *
 * Incluye un [Callback] de creación que prepobla la tabla con datos de ejemplo
 * para que la demo sea funcional desde el primer lanzamiento.
 */
@Database(entities = [Note::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun noteDao(): NoteDao

    companion object {

        private const val DATABASE_NAME = "notes_database"

        /**
         * Factory method utilizado por Koin para construir la instancia de [AppDatabase].
         * Koin se encarga del ciclo de vida singleton, por lo que aquí solo
         * definimos la configuración del builder.
         */
        fun build(context: Context): AppDatabase =
            Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                DATABASE_NAME
            )
                .enableMultiInstanceInvalidation()
                .fallbackToDestructiveMigration()
                .addCallback(PrepopulateCallback)
                .build()
    }

    /**
     * Callback que inserta notas de ejemplo la primera vez que se crea la base de datos.
     * Se utiliza SQL directo porque el DAO no está disponible durante [onCreate]
     * (la instancia de [AppDatabase] aún no ha terminado de construirse).
     */
    private object PrepopulateCallback : Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            db.execSQL(
                """
                INSERT INTO notes (id, title, content) VALUES
                    ('1', 'Bienvenido a AppFunctions', 'Esta es tu primera nota guardada en la base de datos local de Room.'),
                    ('2', 'Idea para LinkedIn', 'Publicar un articulo de ingenieria explicando como AppFunctions permite a la IA controlar apps en local.')
                """.trimIndent()
            )
        }
    }
}
