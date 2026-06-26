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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Work
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
import com.pabloisla.mipostulacion.ui.theme.Carbon
import com.pabloisla.mipostulacion.util.formatearFecha
import com.pabloisla.mipostulacion.viewmodel.PostulacionFormViewModel
import com.pabloisla.mipostulacion.viewmodel.postulacionFormViewModelFactory

private val AREAS = listOf("Frontend", "Backend", "Móvil", "Datos", "QA", "Otro")
private val MODALIDADES = listOf("Presencial", "Remoto", "Híbrido")
private val ESTADOS = listOf("Postulado", "En proceso", "Entrevista", "Oferta", "Rechazado")
private val PRIORIDADES = listOf(1 to "Baja", 2 to "Media", 3 to "Alta")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostulacionFormScreen(
    postulacionId: Long? = null,
    onGuardadoExitoso: () -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val app = context.applicationContext as MiPostulacionApp
    val viewModel: PostulacionFormViewModel = viewModel(
        factory = postulacionFormViewModelFactory(app, postulacionId)
    )

    val uiState by viewModel.uiState.collectAsState()
    var mostrarSelectorFecha by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.guardadoExitoso) {
        if (uiState.guardadoExitoso) {
            onGuardadoExitoso()
        }
    }

    if (mostrarSelectorFecha) {
        SelectorFecha(
            fechaInicialMillis = uiState.fechaPostulacion,
            onFechaSeleccionada = viewModel::onFechaChange,
            onDismiss = { mostrarSelectorFecha = false }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (uiState.esEdicion) "Editar postulación" else "Nueva postulación") },
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

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            SeccionCard(titulo = "Información básica", icono = Icons.Default.Business) {
                CampoConEtiqueta(label = "Empresa") {
                    CampoConSugerencias(
                        valor = uiState.empresa,
                        placeholder = "Busca o escribe una empresa",
                        icono = Icons.Default.Business,
                        sugerencias = EMPRESAS_SUGERIDAS,
                        onValueChange = viewModel::onEmpresaChange
                    )
                }
                CampoConEtiqueta(label = "Puesto", modifier = Modifier.padding(top = 12.dp)) {
                    CampoConSugerencias(
                        valor = uiState.puesto,
                        placeholder = "Busca o escribe un puesto",
                        icono = Icons.Default.Work,
                        sugerencias = PUESTOS_SUGERIDOS,
                        onValueChange = viewModel::onPuestoChange
                    )
                }
            }

            SeccionCard(titulo = "Detalles del proceso", icono = Icons.Default.Tune, modifier = Modifier.padding(top = 14.dp)) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    CampoConEtiqueta(label = "Área", modifier = Modifier.weight(1f)) {
                        DropdownSelector(
                            opciones = AREAS,
                            seleccion = uiState.area,
                            onSeleccionar = viewModel::onAreaChange
                        )
                    }
                    CampoConEtiqueta(
                        label = "Modalidad",
                        modifier = Modifier.weight(1f).padding(start = 10.dp)
                    ) {
                        DropdownSelector(
                            opciones = MODALIDADES,
                            seleccion = uiState.modalidad,
                            onSeleccionar = viewModel::onModalidadChange
                        )
                    }
                }

                CampoConEtiqueta(label = "Estado", modifier = Modifier.padding(top = 14.dp)) {
                    DropdownSelector(
                        opciones = ESTADOS,
                        seleccion = uiState.estado,
                        onSeleccionar = viewModel::onEstadoChange
                    )
                }

                CampoConEtiqueta(label = "Fecha de postulación", modifier = Modifier.padding(top = 14.dp)) {
                    Box {
                        OutlinedTextField(
                            value = formatearFecha(uiState.fechaPostulacion),
                            onValueChange = {},
                            readOnly = true,
                            leadingIcon = { Icon(Icons.Default.CalendarMonth, contentDescription = null) },
                            shape = RoundedCornerShape(14.dp),
                            colors = campoColors(),
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
                }

                CampoConEtiqueta(label = "Prioridad", modifier = Modifier.padding(top = 14.dp)) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        PRIORIDADES.forEach { (valor, etiqueta) ->
                            PrioridadOpcion(
                                texto = etiqueta,
                                seleccionado = uiState.prioridad == valor,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = if (valor != 3) 8.dp else 0.dp),
                                onClick = { viewModel.onPrioridadChange(valor) }
                            )
                        }
                    }
                }
            }

            SeccionCard(titulo = "Información adicional", icono = Icons.AutoMirrored.Filled.Notes, modifier = Modifier.padding(top = 14.dp)) {
                CampoConEtiqueta(label = "Enlace") {
                    OutlinedTextField(
                        value = uiState.enlace,
                        onValueChange = viewModel::onEnlaceChange,
                        placeholder = { Text("Opcional") },
                        leadingIcon = { Icon(Icons.Default.Link, contentDescription = null) },
                        keyboardOptions = KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Uri),
                        shape = RoundedCornerShape(14.dp),
                        colors = campoColors(),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                CampoConEtiqueta(label = "Notas", modifier = Modifier.padding(top = 12.dp)) {
                    OutlinedTextField(
                        value = uiState.notas,
                        onValueChange = viewModel::onNotasChange,
                        placeholder = { Text("Opcional") },
                        leadingIcon = { Icon(Icons.AutoMirrored.Filled.Notes, contentDescription = null) },
                        shape = RoundedCornerShape(14.dp),
                        colors = campoColors(),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            if (uiState.errorValidacion != null) {
                Text(
                    text = uiState.errorValidacion!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }

            Button(
                onClick = viewModel::guardarPostulacion,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Carbon,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth().height(54.dp).padding(top = 22.dp, bottom = 12.dp)
            ) {
                Icon(Icons.Default.Save, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                Text(
                    "Guardar",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    modifier = Modifier.padding(start = 10.dp)
                )
            }
        }
    }
}

@Composable
private fun campoColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = MaterialTheme.colorScheme.surface,
    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
    focusedBorderColor = MaterialTheme.colorScheme.primary,
    unfocusedBorderColor = MaterialTheme.colorScheme.outline
)

@Composable
private fun SeccionCard(
    titulo: String,
    icono: ImageVector,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 14.dp)
            ) {
                Icon(
                    imageVector = icono,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = titulo.uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
            content()
        }
    }
}

@Composable
private fun CampoConEtiqueta(
    label: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        content()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DropdownSelector(
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
            colors = campoColors(),
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
private fun PrioridadOpcion(
    texto: String,
    seleccionado: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (seleccionado) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Flag,
            contentDescription = null,
            tint = if (seleccionado) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(14.dp)
        )
        Text(
            text = texto,
            style = MaterialTheme.typography.labelMedium,
            color = if (seleccionado) Color.White
            else MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = if (seleccionado) FontWeight.SemiBold else FontWeight.Medium,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
