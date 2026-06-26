package com.pabloisla.mipostulacion

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.pabloisla.mipostulacion.navigation.AppNavHost
import com.pabloisla.mipostulacion.ui.theme.MiPostulacionTheme

class MainActivity : ComponentActivity() {

    private val solicitarPermisoNotificaciones = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* el usuario decide; si lo rechaza, la app sigue funcionando sin notificaciones */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        solicitarPermisoNotificacionesSiHaceFalta()

        val postulacionIdDesdeNotificacion = intent
            .getLongExtra(EXTRA_POSTULACION_ID, -1L)
            .takeIf { it >= 0L }

        setContent {
            MiPostulacionTheme {
                AppNavHost(postulacionIdDesdeNotificacion = postulacionIdDesdeNotificacion)
            }
        }
    }

    private fun solicitarPermisoNotificacionesSiHaceFalta() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
        val yaTienePermiso = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
        if (!yaTienePermiso) {
            solicitarPermisoNotificaciones.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    companion object {
        const val EXTRA_POSTULACION_ID = "postulacionId"
    }
}
