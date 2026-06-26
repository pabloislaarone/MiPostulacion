package com.pabloisla.mipostulacion.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface EtapaDao {

    @Insert
    suspend fun insertar(etapa: EtapaProceso): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTodas(etapas: List<EtapaProceso>)

    @Update
    suspend fun actualizar(etapa: EtapaProceso)

    @Delete
    suspend fun eliminar(etapa: EtapaProceso)

    @Query("SELECT * FROM etapas_proceso WHERE postulacionId = :postulacionId ORDER BY fecha ASC")
    fun obtenerPorPostulacion(postulacionId: Long): Flow<List<EtapaProceso>>

    @Query("SELECT * FROM etapas_proceso ORDER BY fecha ASC")
    fun obtenerProximas(): Flow<List<EtapaProceso>>

    @Query("DELETE FROM etapas_proceso")
    suspend fun eliminarTodas()
}