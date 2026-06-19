package com.pabloisla.mipostulacion.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [Postulacion::class, EtapaProceso::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun postulacionDao(): PostulacionDao
    abstract fun etapaDao(): EtapaDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: android.content.Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = androidx.room.Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mipostulacion_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}