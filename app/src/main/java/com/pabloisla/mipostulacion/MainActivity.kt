package com.pabloisla.mipostulacion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.pabloisla.mipostulacion.navigation.AppNavHost
import com.pabloisla.mipostulacion.ui.theme.MiPostulacionTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MiPostulacionTheme {
                AppNavHost()
            }
        }
    }
}