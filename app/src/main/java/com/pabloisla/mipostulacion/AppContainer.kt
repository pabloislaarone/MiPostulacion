package com.pabloisla.mipostulacion

import android.content.Context
import com.pabloisla.mipostulacion.data.local.AppDatabase
import com.pabloisla.mipostulacion.data.remote.RetrofitInstance
import com.pabloisla.mipostulacion.data.remote.firebase.FirebaseAuthSource
import com.pabloisla.mipostulacion.data.remote.firebase.FirestorePostulacionSource
import com.pabloisla.mipostulacion.data.repository.AuthRepository
import com.pabloisla.mipostulacion.data.repository.PostulacionRepository
import com.pabloisla.mipostulacion.notification.AlarmNotificationScheduler

class AppContainer(context: Context) {

    private val database: AppDatabase = AppDatabase.getDatabase(context)

    private val authSource = FirebaseAuthSource()
    private val firestoreSource = FirestorePostulacionSource()
    private val notificationScheduler = AlarmNotificationScheduler(context.applicationContext)

    val authRepository: AuthRepository by lazy {
        AuthRepository(authSource)
    }

    val postulacionRepository: PostulacionRepository by lazy {
        PostulacionRepository(
            postulacionDao = database.postulacionDao(),
            etapaDao = database.etapaDao(),
            triviaApiService = RetrofitInstance.triviaApiService,
            authSource = authSource,
            firestoreSource = firestoreSource,
            notificationScheduler = notificationScheduler
        )
    }
}
