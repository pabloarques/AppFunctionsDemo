package com.example.appfunctionsdemo.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.appfunctionsdemo.model.Note

/**
 * Base de datos principal de Room que gestiona la persistencia de las notas de la aplicación.
 * Cuenta con un callback de creación para precargar datos iniciales semánticos de forma directa.
 */
@Database(entities = [Note::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "notes_database"
                )
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        db.execSQL(
                            "INSERT INTO notes (id, title, content) VALUES " +
                            "('1', 'Bienvenido a AppFunctions', 'Esta es tu primera nota guardada en la base de datos local de Room.')"
                        )
                        db.execSQL(
                            "INSERT INTO notes (id, title, content) VALUES " +
                            "('2', 'Idea para LinkedIn', 'Publicar un articulo de ingenieria explicando como AppFunctions permite a la IA controlar apps en local sin vender humo!')"
                        )
                    }
                })
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
