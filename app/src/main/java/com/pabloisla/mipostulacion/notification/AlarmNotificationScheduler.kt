package com.pabloisla.mipostulacion.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.pabloisla.mipostulacion.data.local.EtapaProceso
import com.pabloisla.mipostulacion.data.local.Postulacion

/**
 * Usa setAndAllowWhileIdle (no exacta) para no requerir el permiso especial
 * de alarmas exactas en Android 12+; es una holgura razonable para un recordatorio.
 */
class AlarmNotificationScheduler(
    private val context: Context
) : NotificationScheduler {

    private val alarmManager: AlarmManager
        get() = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    override fun programar(postulacion: Postulacion, etapa: EtapaProceso) {
        val pendingIntent = crearPendingIntent(
            etapaId = etapa.id,
            postulacionId = etapa.postulacionId,
            tipoEtapa = etapa.tipo,
            empresa = postulacion.empresa
        )
        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, etapa.fecha, pendingIntent)
    }

    override fun cancelar(etapaId: Long) {
        val pendingIntent = crearPendingIntent(
            etapaId = etapaId,
            postulacionId = -1L,
            tipoEtapa = "",
            empresa = ""
        )
        alarmManager.cancel(pendingIntent)
    }

    private fun crearPendingIntent(
        etapaId: Long,
        postulacionId: Long,
        tipoEtapa: String,
        empresa: String
    ): PendingIntent {
        val intent = Intent(context, EtapaAlarmReceiver::class.java).apply {
            putExtra(EtapaAlarmReceiver.EXTRA_ETAPA_ID, etapaId)
            putExtra(EtapaAlarmReceiver.EXTRA_POSTULACION_ID, postulacionId)
            putExtra(EtapaAlarmReceiver.EXTRA_TIPO_ETAPA, tipoEtapa)
            putExtra(EtapaAlarmReceiver.EXTRA_EMPRESA, empresa)
        }
        return PendingIntent.getBroadcast(
            context,
            etapaId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
