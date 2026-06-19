package com.pabloisla.mipostulacion.ui.stats

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
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
import com.pabloisla.mipostulacion.data.local.EtapaProceso
import com.pabloisla.mipostulacion.viewmodel.RetoUiState
import com.pabloisla.mipostulacion.viewmodel.StatsViewModel
import com.pabloisla.mipostulacion.viewmodel.statsViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val app = context.applicationContext as MiPostulacionApp
    val viewModel: StatsViewModel = viewModel(factory = statsViewModelFactory(app))

    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Estadísticas") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).padding(16.dp)) {

            Text(text = "Resumen de postulaciones")
            Card(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    if (uiState.conteoPorEstado.isEmpty()) {
                        Text(text = "Aún no tienes postulaciones registradas")
                    } else {
                        uiState.conteoPorEstado.forEach { (estado, cantidad) ->
                            Text(text = "$estado: $cantidad")
                        }
                    }
                }
            }

            if (uiState.proximasEtapas.isNotEmpty()) {
                Text(text = "Próximas etapas", modifier = Modifier.padding(top = 16.dp))
                Card(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        uiState.proximasEtapas.forEach { etapa ->
                            EtapaResumenItem(etapa)
                        }
                    }
                }
            }

            Text(
                text = "Prepárate para tu próxima entrevista técnica",
                modifier = Modifier.padding(top = 24.dp)
            )
            Text(
                text = "Practica con una pregunta de Computer Science, el tipo de preguntas que podrías encontrar en una entrevista técnica",
                modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
            )

            RetoTecnicoSection(
                reto = uiState.reto,
                onReintentar = { viewModel.cargarRetoDelDia() }
            )
        }
    }
}

@Composable
private fun EtapaResumenItem(etapa: EtapaProceso) {
    Text(text = "${etapa.tipo} — ${etapa.resultado}")
}

@Composable
private fun RetoTecnicoSection(
    reto: RetoUiState,
    onReintentar: () -> Unit
) {
    when (reto) {
        is RetoUiState.Cargando -> {
            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
        }
        is RetoUiState.Error -> {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = reto.mensaje)
                    Button(
                        onClick = onReintentar,
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text("Reintentar")
                    }
                }
            }
        }
        is RetoUiState.Exito -> {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = reto.pregunta.question)
                    var seleccionada by remember { mutableStateOf<String?>(null) }

                    reto.opciones.forEach { opcion ->
                        val esCorrecta = opcion == reto.pregunta.correct_answer
                        val mostrarResultado = seleccionada != null

                        Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                            Button(onClick = { seleccionada = opcion }) {
                                Text(opcion)
                            }
                            if (mostrarResultado && opcion == seleccionada) {
                                Text(
                                    text = if (esCorrecta) "¡Correcto!" else "Incorrecto",
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}