package com.pabloisla.mipostulacion.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * El DatePicker de Material3 entrega milisegundos en UTC (medianoche).
 * Formateamos con timeZone UTC para mostrar el día que el usuario eligió,
 * sin que se corra un día por el desfase de la zona horaria local.
 */
fun formatearFecha(millis: Long): String {
    val formato = SimpleDateFormat("d MMM yyyy", Locale("es", "ES"))
    formato.timeZone = TimeZone.getTimeZone("UTC")
    return formato.format(Date(millis))
}

fun formatearFechaHora(millis: Long): String {
    val formato = SimpleDateFormat("d MMM yyyy, h:mm a", Locale("es", "ES"))
    return formato.format(Date(millis))
}

fun formatearHora(hora: Int, minuto: Int): String {
    val calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, hora)
        set(Calendar.MINUTE, minuto)
    }
    val formato = SimpleDateFormat("h:mm a", Locale("es", "ES"))
    return formato.format(calendar.time)
}

/**
 * Combina una fecha (milisegundos UTC de medianoche, tal como la entrega el DatePicker)
 * con una hora y minuto locales, devolviendo un timestamp en hora local.
 */
fun combinarFechaYHora(fechaUtcMedianoche: Long, hora: Int, minuto: Int): Long {
    val fechaUtc = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
        timeInMillis = fechaUtcMedianoche
    }
    val resultado = Calendar.getInstance().apply {
        clear()
        set(
            fechaUtc.get(Calendar.YEAR),
            fechaUtc.get(Calendar.MONTH),
            fechaUtc.get(Calendar.DAY_OF_MONTH),
            hora,
            minuto
        )
    }
    return resultado.timeInMillis
}

fun esFechaPasada(millis: Long): Boolean = millis < System.currentTimeMillis()

/**
 * Convierte un timestamp local (por ejemplo, fecha+hora ya combinadas de una etapa)
 * a la medianoche UTC equivalente, que es el formato que espera rememberDatePickerState
 * para preseleccionar correctamente el día al reabrir el selector.
 */
fun aFechaUtcMedianoche(millisLocal: Long): Long {
    val local = Calendar.getInstance().apply { timeInMillis = millisLocal }
    return Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
        clear()
        set(local.get(Calendar.YEAR), local.get(Calendar.MONTH), local.get(Calendar.DAY_OF_MONTH))
    }.timeInMillis
}
