package com.pabloisla.mipostulacion.notification

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

/**
 * Recibe los mensajes de Firebase Cloud Messaging. Con la app en segundo plano,
 * el sistema muestra la notificación automáticamente (comportamiento estándar de FCM);
 * aquí solo se cubre el caso de la app en primer plano.
 */
class MiPostulacionMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val titulo = message.notification?.title ?: "MiPostulación"
        val cuerpo = message.notification?.body ?: "Tienes una notificación nueva"
        NotificationHelper.mostrarNotificacionPush(applicationContext, titulo, cuerpo)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Para la sustentación: copiar este token de Logcat y usarlo en
        // "Enviar mensaje de prueba a un dispositivo" desde la consola de Firebase.
        Log.d("MiPostulacionFCM", "Token de FCM: $token")
    }
}
