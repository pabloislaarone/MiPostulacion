package com.pabloisla.mipostulacion

import android.app.Application

class MiPostulacionApp : Application() {

    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}