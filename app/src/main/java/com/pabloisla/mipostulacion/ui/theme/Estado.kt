package com.pabloisla.mipostulacion.ui.theme

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

fun colorPorEstado(estado: String): Color = when (estado) {
    "Postulado" -> EstadoPostulado
    "En proceso" -> EstadoEnProceso
    "Entrevista" -> EstadoEntrevista
    "Oferta" -> EstadoOferta
    "Rechazado" -> EstadoRechazado
    else -> TextoSecundario
}

fun colorPorResultado(resultado: String): Color = when (resultado) {
    "Aprobado" -> EstadoOferta
    "Rechazado" -> EstadoRechazado
    "Sin respuesta" -> EstadoEnProceso
    else -> EstadoPostulado
}

fun iconoPorEstado(estado: String): ImageVector = when (estado) {
    "Postulado" -> Icons.AutoMirrored.Filled.Send
    "En proceso" -> Icons.Filled.HourglassTop
    "Entrevista" -> Icons.Filled.Forum
    "Oferta" -> Icons.Filled.Celebration
    "Rechazado" -> Icons.Filled.ThumbDown
    else -> Icons.AutoMirrored.Filled.Send
}
