package com.example.appfunctionsdemo

import android.app.Application
import com.example.appfunctionsdemo.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

/**
 * Clase Application personalizada para inicializar Koin y configurar
 * el grafo de dependencias al arrancar la app.
 */
class AppFunctionsApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        startKoin {
            androidLogger()
            androidContext(this@AppFunctionsApplication)
            modules(appModule)
        }
    }
}
