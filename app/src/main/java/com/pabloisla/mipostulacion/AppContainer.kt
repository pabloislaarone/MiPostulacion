package com.pabloisla.mipostulacion

import android.content.Context
import com.pabloisla.mipostulacion.data.local.AppDatabase
import com.pabloisla.mipostulacion.data.remote.RetrofitInstance
import com.pabloisla.mipostulacion.data.repository.PostulacionRepository

class AppContainer(context: Context) {

    private val database: AppDatabase = AppDatabase.getDatabase(context)

    val postulacionRepository: PostulacionRepository by lazy {
        PostulacionRepository(
            postulacionDao = database.postulacionDao(),
            etapaDao = database.etapaDao(),
            triviaApiService = RetrofitInstance.triviaApiService
        )
    }
}