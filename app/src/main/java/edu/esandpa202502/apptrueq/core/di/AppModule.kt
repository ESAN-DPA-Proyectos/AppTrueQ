package edu.esandpa202502.apptrueq.core.di

/**
 * Clase que agrupa dependencias o configuraciones generales del módulo "core".
 * Actualmente no se usa inyección de dependencias (Hilt/Dagger).
 */
object AppModule {
    // Para valores compartidos o inicializaciones globales,
    // se puede declararla aquí de manera estática.

    const val APP_NAME = "TrueQ"

    fun provideAppName(): String = APP_NAME
}



