package com.pabloisla.mipostulacion.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class EtapaAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val etapaId = intent.getLongExtra(EXTRA_ETAPA_ID, -1L)
        val postulacionId = intent.getLongExtra(EXTRA_POSTULACION_ID, -1L)
        val tipoEtapa = intent.getStringExtra(EXTRA_TIPO_ETAPA) ?: "Etapa de proceso"
        val empresa = intent.getStringExtra(EXTRA_EMPRESA) ?: ""

        NotificationHelper.mostrarNotificacionEtapa(
            context = context,
            etapaId = etapaId,
            postulacionId = postulacionId,
            titulo = "Hoy: $tipoEtapa",
            texto = if (empresa.isNotBlank()) "Postulación en $empresa" else "Tienes una etapa programada para hoy"
        )
    }

    companion object {
        const val EXTRA_ETAPA_ID = "etapaId"
        const val EXTRA_POSTULACION_ID = "postulacionId"
        const val EXTRA_TIPO_ETAPA = "tipoEtapa"
        const val EXTRA_EMPRESA = "empresa"
    }
}
