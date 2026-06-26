package com.pabloisla.mipostulacion.data.repository

import com.pabloisla.mipostulacion.data.local.EtapaDao
import com.pabloisla.mipostulacion.data.local.EtapaProceso
import com.pabloisla.mipostulacion.data.local.Postulacion
import com.pabloisla.mipostulacion.data.local.PostulacionDao
import com.pabloisla.mipostulacion.data.remote.TriviaApiService
import com.pabloisla.mipostulacion.data.remote.TriviaQuestion
import com.pabloisla.mipostulacion.data.remote.firebase.FirebaseAuthSource
import com.pabloisla.mipostulacion.data.remote.firebase.FirestorePostulacionSource
import com.pabloisla.mipostulacion.notification.NotificationScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class PostulacionRepository(
    private val postulacionDao: PostulacionDao,
    private val etapaDao: EtapaDao,
    private val triviaApiService: TriviaApiService,
    private val authSource: FirebaseAuthSource,
    private val firestoreSource: FirestorePostulacionSource,
    private val notificationScheduler: NotificationScheduler
) {

    private val syncScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    // Postulaciones

    fun obtenerTodas(): Flow<List<Postulacion>> =
        postulacionDao.obtenerTodas()

    fun obtenerPorId(id: Long): Flow<Postulacion?> =
        postulacionDao.obtenerPorId(id)

    suspend fun registrarPostulacion(postulacion: Postulacion): Long {
        val id = postulacionDao.insertar(postulacion)
        sincronizarPostulacion(postulacion.copy(id = id))
        return id
    }

    suspend fun actualizarPostulacion(postulacion: Postulacion) {
        postulacionDao.actualizar(postulacion)
        sincronizarPostulacion(postulacion)
    }

    suspend fun eliminarPostulacion(postulacion: Postulacion) {
        val etapas = etapaDao.obtenerPorPostulacion(postulacion.id).first()
        postulacionDao.eliminar(postulacion) // Room elimina en cascada las etapas locales
        etapas.forEach { notificationScheduler.cancelar(it.id) }
        eliminarPostulacionRemota(postulacion.id, etapas.map { it.id })
    }

    // ---- Etapas de proceso ----

    fun obtenerEtapasPorPostulacion(postulacionId: Long): Flow<List<EtapaProceso>> =
        etapaDao.obtenerPorPostulacion(postulacionId)

    fun obtenerEtapasProximas(): Flow<List<EtapaProceso>> =
        etapaDao.obtenerProximas()

    suspend fun agregarEtapa(etapa: EtapaProceso): Long {
        val id = etapaDao.insertar(etapa)
        val etapaGuardada = etapa.copy(id = id)
        programarRecordatorioSiAplica(etapaGuardada)
        sincronizarEtapa(etapaGuardada)
        return id
    }

    suspend fun actualizarEtapa(etapa: EtapaProceso) {
        etapaDao.actualizar(etapa)
        notificationScheduler.cancelar(etapa.id)
        programarRecordatorioSiAplica(etapa)
        sincronizarEtapa(etapa)
    }

    suspend fun eliminarEtapa(etapa: EtapaProceso) {
        etapaDao.eliminar(etapa)
        notificationScheduler.cancelar(etapa.id)
        eliminarEtapaRemota(etapa.id)
    }

    // ---- Sincronización con Cloud Firestore ----

    suspend fun sincronizarDesdeFirestore(uid: String) {
        runCatching {
            val postulaciones = firestoreSource.obtenerPostulaciones(uid)
            val etapas = firestoreSource.obtenerEtapas(uid)
            postulacionDao.insertarTodas(postulaciones)
            etapaDao.insertarTodas(etapas)
        }
    }

    private fun sincronizarPostulacion(postulacion: Postulacion) {
        val uid = authSource.usuarioActualId() ?: return
        syncScope.launch {
            runCatching { firestoreSource.guardarPostulacion(uid, postulacion) }
        }
    }

    private fun sincronizarEtapa(etapa: EtapaProceso) {
        val uid = authSource.usuarioActualId() ?: return
        syncScope.launch {
            runCatching { firestoreSource.guardarEtapa(uid, etapa) }
        }
    }

    private fun eliminarPostulacionRemota(postulacionId: Long, etapaIds: List<Long>) {
        val uid = authSource.usuarioActualId() ?: return
        syncScope.launch {
            runCatching {
                firestoreSource.eliminarPostulacion(uid, postulacionId)
                etapaIds.forEach { firestoreSource.eliminarEtapa(uid, it) }
            }
        }
    }

    private fun eliminarEtapaRemota(etapaId: Long) {
        val uid = authSource.usuarioActualId() ?: return
        syncScope.launch {
            runCatching { firestoreSource.eliminarEtapa(uid, etapaId) }
        }
    }

    // ---- Notificación local de la etapa (RF-21) ----

    private suspend fun programarRecordatorioSiAplica(etapa: EtapaProceso) {
        if (etapa.fecha <= System.currentTimeMillis()) return
        val postulacion = postulacionDao.obtenerPorId(etapa.postulacionId).first() ?: return
        notificationScheduler.programar(postulacion, etapa)
    }

    // Reto técnico para preparación de entrevistas (Open Trivia Database)

    suspend fun obtenerRetoDelDia(): TriviaQuestion {
        val respuesta = triviaApiService.obtenerPregunta()
        return respuesta.results.first()
    }
}
