package com.pabloisla.mipostulacion.ui.form

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pabloisla.mipostulacion.MiPostulacionApp
import com.pabloisla.mipostulacion.ui.components.SelectorFecha
import com.pabloisla.mipostulacion.ui.components.SelectorHora
import com.pabloisla.mipostulacion.ui.theme.Carbon
import com.pabloisla.mipostulacion.ui.theme.colorPorResultado
import com.pabloisla.mipostulacion.util.aFechaUtcMedianoche
import com.pabloisla.mipostulacion.util.combinarFechaYHora
import com.pabloisla.mipostulacion.util.esFechaPasada
import com.pabloisla.mipostulacion.util.formatearFecha
import com.pabloisla.mipostulacion.util.formatearHora
import com.pabloisla.mipostulacion.viewmodel.EtapaFormViewModel
import com.pabloisla.mipostulacion.viewmodel.etapaFormViewModelFactory
import java.util.Calendar

private val TIPOS = listOf("Entrevista RRHH", "Entrevista Técnica", "Prueba o Caso", "Resultado", "Otro")
private val RESULTADOS = listOf("Pendiente", "Aprobado", "Rechazado", "Sin respuesta")

private fun iconoPorResultado(resultado: String): ImageVector = when (resultado) {
    "Aprobado" -> Icons.Default.CheckCircle
    "Rechazado" -> Icons.Default.Cancel
    "Sin respuesta" -> Icons.Default.QuestionMark
    else -> Icons.Default.HourglassEmpty
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EtapaFormScreen(
    postulacionId: Long,
    etapaId: Long? = null,
    onGuardadoExitoso: () -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val app = context.applicationContext as MiPostulacionApp
    val viewModel: EtapaFormViewModel = viewModel(
        factory = etapaFormViewModelFactory(app, postulacionId, etapaId)
    )

    val uiState by viewModel.uiState.collectAsState()
    var mostrarSelectorFecha by remember { mutableStateOf(false) }
    var mostrarSelectorHora by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.guardadoExitoso) {
        if (uiState.guardadoExitoso) {
            onGuardadoExitoso()
        }
    }

    if (mostrarSelectorFecha) {
        SelectorFecha(
            fechaInicialMillis = aFechaUtcMedianoche(uiState.fecha),
            onFechaSeleccionada = { nuevaFechaUtc ->
                val actual = Calendar.getInstance().apply { timeInMillis = uiState.fecha }
                viewModel.onFechaChange(
                    combinarFechaYHora(
                        nuevaFechaUtc,
                        actual.get(Calendar.HOUR_OF_DAY),
                        actual.get(Calendar.MINUTE)
                    )
                )
            },
            onDismiss = { mostrarSelectorFecha = false }
        )
    }

    if (mostrarSelectorHora) {
        val actual = Calendar.getInstance().apply { timeInMillis = uiState.fecha }
        SelectorHora(
            horaInicial = actual.get(Calendar.HOUR_OF_DAY),
            minutoInicial = actual.get(Calendar.MINUTE),
            onHoraSeleccionada = { hora, minuto ->
                val calendarFecha = Calendar.getInstance().apply { timeInMillis = uiState.fecha }
                calendarFecha.set(Calendar.HOUR_OF_DAY, hora)
                calendarFecha.set(Calendar.MINUTE, minuto)
                viewModel.onFechaChange(calendarFecha.timeInMillis)
            },
            onDismiss = { mostrarSelectorHora = false }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (uiState.esEdicion) "Editar etapa" else "Nueva etapa") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { innerPadding ->
        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(innerPadding).padding(32.dp))
            return@Scaffold
        }

        Column(modifier = Modifier.padding(innerPadding).padding(16.dp)) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    SeccionTitulo(icono = Icons.Default.Event, texto = "Tipo de etapa")
                    EtapaDropdown(
                        opciones = TIPOS,
                        seleccion = uiState.tipo,
                        onSeleccionar = viewModel::onTipoChange
                    )

                    SeccionTitulo(
                        icono = Icons.Default.CalendarMonth,
                        texto = "Fecha y hora",
                        modifier = Modifier.padding(top = 18.dp)
                    )
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Box(modifier = Modifier.weight(1.2f)) {
                            OutlinedTextField(
                                value = formatearFecha(aFechaUtcMedianoche(uiState.fecha)),
                                onValueChange = {},
                                readOnly = true,
                                leadingIcon = { Icon(Icons.Default.CalendarMonth, contentDescription = null) },
                                shape = RoundedCornerShape(14.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                            // El propio OutlinedTextField intercepta el toque para enfocarse
                            // antes de que llegue un clickable adjunto a él; este overlay
                            // transparente del mismo tamaño es el que realmente recibe el toque.
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .clickable { mostrarSelectorFecha = true }
                            )
                        }
                        Box(modifier = Modifier.weight(1f).padding(start = 10.dp)) {
                            OutlinedTextField(
                                value = formatearHora(
                                    Calendar.getInstance().apply { timeInMillis = uiState.fecha }.get(Calendar.HOUR_OF_DAY),
                                    Calendar.getInstance().apply { timeInMillis = uiState.fecha }.get(Calendar.MINUTE)
                                ),
                                onValueChange = {},
                                readOnly = true,
                                leadingIcon = { Icon(Icons.Default.Schedule, contentDescription = null) },
                                shape = RoundedCornerShape(14.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .clickable { mostrarSelectorHora = true }
                            )
                        }
                    }
                    if (esFechaPasada(uiState.fecha)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.WarningAmber,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "Esta fecha ya pasó: no se programará un recordatorio.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(start = 6.dp)
                            )
                        }
                    }

                    SeccionTitulo(
                        icono = Icons.Default.CheckCircle,
                        texto = "Resultado",
                        modifier = Modifier.padding(top = 18.dp)
                    )
                    Row(modifier = Modifier.fillMaxWidth()) {
                        RESULTADOS.forEach { resultado ->
                            ResultadoOpcion(
                                texto = resultado,
                                seleccionado = uiState.resultado == resultado,
                                color = colorPorResultado(resultado),
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = if (resultado != RESULTADOS.last()) 8.dp else 0.dp),
                                onClick = { viewModel.onResultadoChange(resultado) }
                            )
                        }
                    }

                    SeccionTitulo(
                        icono = Icons.AutoMirrored.Filled.Notes,
                        texto = "Notas",
                        modifier = Modifier.padding(top = 18.dp)
                    )
                    OutlinedTextField(
                        value = uiState.notas,
                        onValueChange = viewModel::onNotasChange,
                        placeholder = { Text("Opcional") },
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Button(
                onClick = viewModel::guardarEtapa,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Carbon,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth().height(54.dp).padding(top = 20.dp)
            ) {
                Icon(Icons.Default.Save, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                Text(
                    "Guardar etapa",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    modifier = Modifier.padding(start = 10.dp)
                )
            }
        }
    }
}

@Composable
private fun SeccionTitulo(icono: ImageVector, texto: String, modifier: Modifier = Modifier) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier.padding(bottom = 10.dp)) {
        Icon(
            imageVector = icono,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = texto.uppercase(),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EtapaDropdown(
    opciones: List<String>,
    seleccion: String,
    onSeleccionar: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = seleccion,
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            ),
            modifier = Modifier.fillMaxWidth().menuAnchor()
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            opciones.forEach { opcion ->
                DropdownMenuItem(
                    text = { Text(opcion) },
                    onClick = {
                        onSeleccionar(opcion)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun ResultadoOpcion(
    texto: String,
    seleccionado: Boolean,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (seleccionado) color else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = iconoPorResultado(texto),
            contentDescription = null,
            tint = if (seleccionado) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(15.dp)
        )
        Text(
            text = texto,
            style = MaterialTheme.typography.labelSmall,
            color = if (seleccionado) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = if (seleccionado) FontWeight.SemiBold else FontWeight.Medium,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
