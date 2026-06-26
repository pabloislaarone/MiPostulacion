package com.pabloisla.mipostulacion.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PostulacionDao {

    @Insert
    suspend fun insertar(postulacion: Postulacion): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTodas(postulaciones: List<Postulacion>)

    @Update
    suspend fun actualizar(postulacion: Postulacion)

    @Delete
    suspend fun eliminar(postulacion: Postulacion)

    @Query("SELECT * FROM postulaciones ORDER BY fechaPostulacion DESC")
    fun obtenerTodas(): Flow<List<Postulacion>>

    @Query("SELECT * FROM postulaciones WHERE id = :id")
    fun obtenerPorId(id: Long): Flow<Postulacion?>
}