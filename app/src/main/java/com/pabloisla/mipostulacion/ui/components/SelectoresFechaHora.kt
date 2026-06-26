package com.pabloisla.mipostulacion.ui.components

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TextButton
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectorFecha(
    fechaInicialMillis: Long,
    onFechaSeleccionada: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    val estado = rememberDatePickerState(initialSelectedDateMillis = fechaInicialMillis)

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                estado.selectedDateMillis?.let(onFechaSeleccionada)
                onDismiss()
            }) {
                Text("Aceptar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    ) {
        DatePicker(state = estado)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectorHora(
    horaInicial: Int,
    minutoInicial: Int,
    onHoraSeleccionada: (Int, Int) -> Unit,
    onDismiss: () -> Unit
) {
    val estado = rememberTimePickerState(
        initialHour = horaInicial,
        initialMinute = minutoInicial,
        is24Hour = false
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onHoraSeleccionada(estado.hour, estado.minute)
                onDismiss()
            }) {
                Text("Aceptar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        },
        text = {
            TimePicker(state = estado)
        }
    )
}
