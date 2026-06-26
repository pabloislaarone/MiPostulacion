package com.pabloisla.mipostulacion

import android.app.Application
import com.pabloisla.mipostulacion.notification.NotificationHelper

class MiPostulacionApp : Application() {

    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        NotificationHelper.crearCanal(this)
    }
}
