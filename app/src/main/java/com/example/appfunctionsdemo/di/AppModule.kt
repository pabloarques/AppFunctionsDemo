package com.example.appfunctionsdemo.di

import com.example.appfunctionsdemo.data.AppDatabase
import com.example.appfunctionsdemo.data.NoteRepository
import com.example.appfunctionsdemo.ui.viewmodel.NoteViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

/**
 * Módulo de Koin para inyección de dependencias de la aplicación.
 * Registra la base de datos local de Room, el acceso a datos (DAO),
 * el repositorio y el ViewModel de forma limpia y moderna.
 */
val appModule = module {
    // Room Database
    single { AppDatabase.getDatabase(get()) }
    
    // DAO
    single { get<AppDatabase>().noteDao() }
    
    // Repositorio
    single { NoteRepository(get()) }
    
    // ViewModel
    viewModelOf(::NoteViewModel)
}
