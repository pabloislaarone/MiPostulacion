package com.pabloisla.mipostulacion.ui.form

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pabloisla.mipostulacion.MiPostulacionApp
import com.pabloisla.mipostulacion.viewmodel.EtapaFormViewModel
import com.pabloisla.mipostulacion.viewmodel.etapaFormViewModelFactory

private val TIPOS = listOf("Entrevista RRHH", "Entrevista Técnica", "Prueba o Caso", "Resultado", "Otro")
private val RESULTADOS = listOf("Pendiente", "Aprobado", "Rechazado", "Sin respuesta")

private fun colorPorResultado(resultado: String): Color = when (resultado) {
    "Aprobado" -> Color(0xFF4AAE7A)
    "Rechazado" -> Color(0xFFB5544A)
    "Sin respuesta" -> Color(0xFFB5A04A)
    else -> Color(0xFF6890B5)
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

    LaunchedEffect(uiState.guardadoExitoso) {
        if (uiState.guardadoExitoso) {
            onGuardadoExitoso()
        }
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
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "TIPO DE ETAPA",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )
                    EtapaDropdown(
                        opciones = TIPOS,
                        seleccion = uiState.tipo,
                        onSeleccionar = viewModel::onTipoChange
                    )

                    Text(
                        text = "RESULTADO",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 16.dp, bottom = 10.dp)
                    )
                    Row(modifier = Modifier.fillMaxWidth()) {
                        RESULTADOS.forEach { resultado ->
                            ResultadoOpcion(
                                texto = resultado,
                                seleccionado = uiState.resultado == resultado,
                                color = colorPorResultado(resultado),
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = if (resultado != RESULTADOS.last()) 6.dp else 0.dp),
                                onClick = { viewModel.onResultadoChange(resultado) }
                            )
                        }
                    }

                    Text(
                        text = "NOTAS",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 16.dp, bottom = 10.dp)
                    )
                    OutlinedTextField(
                        value = uiState.notas,
                        onValueChange = viewModel::onNotasChange,
                        placeholder = { Text("Opcional") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Button(
                onClick = viewModel::guardarEtapa,
                modifier = Modifier.fillMaxWidth().padding(top = 20.dp)
            ) {
                Text("Guardar etapa")
            }
        }
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
            .clip(RoundedCornerShape(8.dp))
            .background(if (seleccionado) color else MaterialTheme.colorScheme.background)
            .clickable { onClick() }
            .padding(vertical = 10.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = texto,
            style = MaterialTheme.typography.labelSmall,
            color = if (seleccionado) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = if (seleccionado) FontWeight.Medium else FontWeight.Normal,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}