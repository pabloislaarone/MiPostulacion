package com.pabloisla.mipostulacion.notification

import com.pabloisla.mipostulacion.data.local.EtapaProceso
import com.pabloisla.mipostulacion.data.local.Postulacion

interface NotificationScheduler {
    fun programar(postulacion: Postulacion, etapa: EtapaProceso)
    fun cancelar(etapaId: Long)
}
