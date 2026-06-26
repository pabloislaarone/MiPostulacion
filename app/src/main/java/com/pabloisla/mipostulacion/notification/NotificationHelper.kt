package com.pabloisla.mipostulacion.notification

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.pabloisla.mipostulacion.MainActivity
import com.pabloisla.mipostulacion.R

object NotificationHelper {

    const val CANAL_ETAPAS = "etapas_proceso"
    private const val ID_NOTIFICACION_PUSH = 9000

    fun crearCanal(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val canal = NotificationChannel(
            CANAL_ETAPAS,
            "Recordatorios de postulaciones",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Avisos sobre entrevistas y etapas de tus postulaciones"
        }
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(canal)
    }

    fun mostrarNotificacionEtapa(
        context: Context,
        etapaId: Long,
        postulacionId: Long,
        titulo: String,
        texto: String
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(MainActivity.EXTRA_POSTULACION_ID, postulacionId)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            etapaId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificacion = NotificationCompat.Builder(context, CANAL_ETAPAS)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(titulo)
            .setContentText(texto)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificarSiHayPermiso(context, etapaId.toInt(), notificacion)
    }

    fun mostrarNotificacionPush(context: Context, titulo: String, cuerpo: String) {
        val notificacion = NotificationCompat.Builder(context, CANAL_ETAPAS)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(titulo)
            .setContentText(cuerpo)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        notificarSiHayPermiso(context, ID_NOTIFICACION_PUSH, notificacion)
    }

    private fun notificarSiHayPermiso(context: Context, id: Int, notificacion: Notification) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val tienePermiso = ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!tienePermiso) return
        }
        NotificationManagerCompat.from(context).notify(id, notificacion)
    }
}
