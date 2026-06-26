package com.pabloisla.mipostulacion.data.remote.firebase

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.pabloisla.mipostulacion.data.local.EtapaProceso
import com.pabloisla.mipostulacion.data.local.Postulacion
import kotlinx.coroutines.tasks.await

private const val COLECCION_USUARIOS = "usuarios"
private const val COLECCION_POSTULACIONES = "postulaciones"
private const val COLECCION_ETAPAS = "etapas"

class FirestorePostulacionSource {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private fun postulacionesRef(uid: String) =
        db.collection(COLECCION_USUARIOS).document(uid).collection(COLECCION_POSTULACIONES)

    private fun etapasRef(uid: String) =
        db.collection(COLECCION_USUARIOS).document(uid).collection(COLECCION_ETAPAS)

    suspend fun guardarPostulacion(uid: String, postulacion: Postulacion) {
        postulacionesRef(uid).document(postulacion.id.toString()).set(postulacion.aMapa()).await()
    }

    suspend fun eliminarPostulacion(uid: String, postulacionId: Long) {
        postulacionesRef(uid).document(postulacionId.toString()).delete().await()
    }

    suspend fun guardarEtapa(uid: String, etapa: EtapaProceso) {
        etapasRef(uid).document(etapa.id.toString()).set(etapa.aMapa()).await()
    }

    suspend fun eliminarEtapa(uid: String, etapaId: Long) {
        etapasRef(uid).document(etapaId.toString()).delete().await()
    }

    suspend fun obtenerPostulaciones(uid: String): List<Postulacion> =
        postulacionesRef(uid).get().await().documents.mapNotNull { it.aPostulacion() }

    suspend fun obtenerEtapas(uid: String): List<EtapaProceso> =
        etapasRef(uid).get().await().documents.mapNotNull { it.aEtapaProceso() }
}

private fun Postulacion.aMapa(): Map<String, Any?> = mapOf(
    "empresa" to empresa,
    "puesto" to puesto,
    "area" to area,
    "modalidad" to modalidad,
    "estado" to estado,
    "fechaPostulacion" to fechaPostulacion,
    "prioridad" to prioridad,
    "enlace" to enlace,
    "notas" to notas
)

private fun DocumentSnapshot.aPostulacion(): Postulacion? {
    val idDocumento = id.toLongOrNull() ?: return null
    return Postulacion(
        id = idDocumento,
        empresa = getString("empresa") ?: "",
        puesto = getString("puesto") ?: "",
        area = getString("area") ?: "",
        modalidad = getString("modalidad") ?: "",
        estado = getString("estado") ?: "",
        fechaPostulacion = getLong("fechaPostulacion") ?: 0L,
        prioridad = (getLong("prioridad") ?: 2L).toInt(),
        enlace = getString("enlace"),
        notas = getString("notas")
    )
}

private fun EtapaProceso.aMapa(): Map<String, Any?> = mapOf(
    "postulacionId" to postulacionId,
    "tipo" to tipo,
    "fecha" to fecha,
    "resultado" to resultado,
    "notas" to notas
)

private fun DocumentSnapshot.aEtapaProceso(): EtapaProceso? {
    val idDocumento = id.toLongOrNull() ?: return null
    return EtapaProceso(
        id = idDocumento,
        postulacionId = getLong("postulacionId") ?: 0L,
        tipo = getString("tipo") ?: "",
        fecha = getLong("fecha") ?: 0L,
        resultado = getString("resultado") ?: "",
        notas = getString("notas")
    )
}
