package com.pabloisla.mipostulacion.ui.form

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pabloisla.mipostulacion.MiPostulacionApp
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

    LaunchedEffect(uiState.guardadoExitoso) {
        if (uiState.guardadoExitoso) {
            onGuardadoExitoso()
        }
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
            SeccionCard(titulo = "Información básica") {
                CampoConEtiqueta(label = "Empresa") {
                    OutlinedTextField(
                        value = uiState.empresa,
                        onValueChange = viewModel::onEmpresaChange,
                        placeholder = { Text("Ej. Globant") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                CampoConEtiqueta(label = "Puesto", modifier = Modifier.padding(top = 12.dp)) {
                    OutlinedTextField(
                        value = uiState.puesto,
                        onValueChange = viewModel::onPuestoChange,
                        placeholder = { Text("Ej. Practicante Backend") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            SeccionCard(titulo = "Detalles del proceso", modifier = Modifier.padding(top = 12.dp)) {
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
                        modifier = Modifier.weight(1f).padding(start = 8.dp)
                    ) {
                        DropdownSelector(
                            opciones = MODALIDADES,
                            seleccion = uiState.modalidad,
                            onSeleccionar = viewModel::onModalidadChange
                        )
                    }
                }

                CampoConEtiqueta(label = "Estado", modifier = Modifier.padding(top = 12.dp)) {
                    DropdownSelector(
                        opciones = ESTADOS,
                        seleccion = uiState.estado,
                        onSeleccionar = viewModel::onEstadoChange
                    )
                }

                CampoConEtiqueta(label = "Prioridad", modifier = Modifier.padding(top = 12.dp)) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        PRIORIDADES.forEach { (valor, etiqueta) ->
                            PrioridadOpcion(
                                texto = etiqueta,
                                seleccionado = uiState.prioridad == valor,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = if (valor != 3) 6.dp else 0.dp),
                                onClick = { viewModel.onPrioridadChange(valor) }
                            )
                        }
                    }
                }
            }

            SeccionCard(titulo = "Información adicional", modifier = Modifier.padding(top = 12.dp)) {
                CampoConEtiqueta(label = "Enlace") {
                    OutlinedTextField(
                        value = uiState.enlace,
                        onValueChange = viewModel::onEnlaceChange,
                        placeholder = { Text("Opcional") },
                        keyboardOptions = KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Uri),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                CampoConEtiqueta(label = "Notas", modifier = Modifier.padding(top = 12.dp)) {
                    OutlinedTextField(
                        value = uiState.notas,
                        onValueChange = viewModel::onNotasChange,
                        placeholder = { Text("Opcional") },
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
                modifier = Modifier.fillMaxWidth().padding(top = 20.dp, bottom = 12.dp)
            ) {
                Text("Guardar")
            }
        }
    }
}

@Composable
private fun SeccionCard(
    titulo: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = titulo.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 10.dp)
            )
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
            modifier = Modifier.padding(bottom = 4.dp)
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
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (seleccionado) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.background
            )
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = texto,
            style = MaterialTheme.typography.labelMedium,
            color = if (seleccionado) androidx.compose.ui.graphics.Color.White
            else MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = if (seleccionado) FontWeight.Medium else FontWeight.Normal
        )
    }
}