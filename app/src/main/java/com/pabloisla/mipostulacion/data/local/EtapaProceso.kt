package com.pabloisla.mipostulacion.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "etapas_proceso",
    foreignKeys = [
        ForeignKey(
            entity = Postulacion::class,
            parentColumns = ["id"],
            childColumns = ["postulacionId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class EtapaProceso(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val postulacionId: Long,
    val tipo: String,
    val fecha: Long,
    val resultado: String,
    val notas: String? = null
)