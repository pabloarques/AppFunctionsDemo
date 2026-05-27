package com.example.appfunctionsdemo.di

import com.example.appfunctionsdemo.data.AppDatabase
import com.example.appfunctionsdemo.data.NoteRepository
import com.example.appfunctionsdemo.ui.viewmodel.NoteViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

/**
 * Grafo de dependencias de la aplicación gestionado por Koin.
 *
 * Define singletons para la capa de datos (Room → DAO → Repository)
 * y factoría para el ViewModel de la pantalla principal.
 */
val appModule = module {

    // Capa de datos: Room
    single { AppDatabase.build(get()) }
    single { get<AppDatabase>().noteDao() }

    // Capa de dominio
    single { NoteRepository(get()) }

    // Capa de presentación
    viewModelOf(::NoteViewModel)
}
