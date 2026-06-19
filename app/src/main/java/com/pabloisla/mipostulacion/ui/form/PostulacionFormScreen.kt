package com.pabloisla.mipostulacion.ui.form

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pabloisla.mipostulacion.MiPostulacionApp
import com.pabloisla.mipostulacion.viewmodel.PostulacionFormViewModel
import com.pabloisla.mipostulacion.viewmodel.postulacionFormViewModelFactory

private val AREAS = listOf("Frontend", "Backend", "Móvil", "Datos", "QA", "Otro")
private val MODALIDADES = listOf("Presencial", "Remoto", "Híbrido")
private val ESTADOS = listOf("Postulado", "En proceso", "Entrevista", "Oferta", "Rechazado")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostulacionFormScreen(
    onGuardadoExitoso: () -> Unit
) {
    val context = LocalContext.current
    val app = context.applicationContext as MiPostulacionApp
    val viewModel: PostulacionFormViewModel = viewModel(factory = postulacionFormViewModelFactory(app))

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.guardadoExitoso) {
        if (uiState.guardadoExitoso) {
            onGuardadoExitoso()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Nueva Postulación") })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            OutlinedTextField(
                value = uiState.empresa,
                onValueChange = viewModel::onEmpresaChange,
                label = { Text("Empresa") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.puesto,
                onValueChange = viewModel::onPuestoChange,
                label = { Text("Puesto") },
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
            )

            DropdownSelector(
                label = "Área",
                opciones = AREAS,
                seleccion = uiState.area,
                onSeleccionar = viewModel::onAreaChange
            )

            DropdownSelector(
                label = "Modalidad",
                opciones = MODALIDADES,
                seleccion = uiState.modalidad,
                onSeleccionar = viewModel::onModalidadChange
            )

            DropdownSelector(
                label = "Estado",
                opciones = ESTADOS,
                seleccion = uiState.estado,
                onSeleccionar = viewModel::onEstadoChange
            )

            OutlinedTextField(
                value = uiState.enlace,
                onValueChange = viewModel::onEnlaceChange,
                label = { Text("Enlace (opcional)") },
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
            )

            OutlinedTextField(
                value = uiState.notas,
                onValueChange = viewModel::onNotasChange,
                label = { Text("Notas (opcional)") },
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
            )

            if (uiState.errorValidacion != null) {
                Text(
                    text = uiState.errorValidacion!!,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }

            Button(
                onClick = viewModel::guardarPostulacion,
                modifier = Modifier.fillMaxWidth().padding(top = 20.dp)
            ) {
                Text("Guardar")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DropdownSelector(
    label: String,
    opciones: List<String>,
    seleccion: String,
    onSeleccionar: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
    ) {
        OutlinedTextField(
            value = seleccion,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor()
        )
        ExposedDropdownMenu(
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