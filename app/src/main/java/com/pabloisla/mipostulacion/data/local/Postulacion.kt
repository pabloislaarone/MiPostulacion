package com.pabloisla.mipostulacion.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "postulaciones")
data class Postulacion(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val empresa: String,
    val puesto: String,
    val area: String,
    val modalidad: String,
    val estado: String,
    val fechaPostulacion: Long,
    val prioridad: Int,
    val enlace: String? = null,
    val notas: String? = null
)