package com.pabloisla.mipostulacion.data.repository

import com.pabloisla.mipostulacion.data.local.EtapaDao
import com.pabloisla.mipostulacion.data.local.EtapaProceso
import com.pabloisla.mipostulacion.data.local.Postulacion
import com.pabloisla.mipostulacion.data.local.PostulacionDao
import kotlinx.coroutines.flow.Flow

class PostulacionRepository(
    private val postulacionDao: PostulacionDao,
    private val etapaDao: EtapaDao
) {

    // Postulaciones

    fun obtenerTodas(): Flow<List<Postulacion>> =
        postulacionDao.obtenerTodas()

    fun obtenerPorId(id: Long): Flow<Postulacion?> =
        postulacionDao.obtenerPorId(id)

    suspend fun registrarPostulacion(postulacion: Postulacion): Long =
        postulacionDao.insertar(postulacion)

    suspend fun actualizarPostulacion(postulacion: Postulacion) =
        postulacionDao.actualizar(postulacion)

    suspend fun eliminarPostulacion(postulacion: Postulacion) =
        postulacionDao.eliminar(postulacion)

    // Etapas de proceso

    fun obtenerEtapasPorPostulacion(postulacionId: Long): Flow<List<EtapaProceso>> =
        etapaDao.obtenerPorPostulacion(postulacionId)

    fun obtenerEtapasProximas(): Flow<List<EtapaProceso>> =
        etapaDao.obtenerProximas()

    suspend fun agregarEtapa(etapa: EtapaProceso): Long =
        etapaDao.insertar(etapa)

    suspend fun actualizarEtapa(etapa: EtapaProceso) =
        etapaDao.actualizar(etapa)

    suspend fun eliminarEtapa(etapa: EtapaProceso) =
        etapaDao.eliminar(etapa)
}