package com.pabloisla.mipostulacion.ui.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pabloisla.mipostulacion.MiPostulacionApp
import com.pabloisla.mipostulacion.data.local.EtapaProceso
import com.pabloisla.mipostulacion.viewmodel.PostulacionDetailViewModel
import com.pabloisla.mipostulacion.viewmodel.postulacionDetailViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostulacionDetailScreen(
    postulacionId: Long,
    onEliminado: () -> Unit,
    onEditarClick: () -> Unit
) {
    val context = LocalContext.current
    val app = context.applicationContext as MiPostulacionApp
    val viewModel: PostulacionDetailViewModel = viewModel(
        factory = postulacionDetailViewModelFactory(app, postulacionId)
    )

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.eliminado) {
        if (uiState.eliminado) {
            onEliminado()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Detalle de Postulación") })
        }
    ) { innerPadding ->
        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(innerPadding).padding(32.dp))
            return@Scaffold
        }

        val postulacion = uiState.postulacion
        if (postulacion == null) {
            Text(
                text = "Esta postulación ya no existe",
                modifier = Modifier.padding(innerPadding).padding(32.dp)
            )
            return@Scaffold
        }

        Column(modifier = Modifier.padding(innerPadding).padding(16.dp)) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = postulacion.empresa)
                    Text(text = postulacion.puesto)
                    Text(text = "Área: ${postulacion.area}")
                    Text(text = "Modalidad: ${postulacion.modalidad}")
                    Text(text = "Estado: ${postulacion.estado}")
                    Text(text = "Prioridad: ${postulacion.prioridad}")
                    if (!postulacion.enlace.isNullOrBlank()) {
                        Text(text = "Enlace: ${postulacion.enlace}")
                    }
                    if (!postulacion.notas.isNullOrBlank()) {
                        Text(text = "Notas: ${postulacion.notas}")
                    }
                }
            }

            Row(modifier = Modifier.fillMaxWidth().padding(top = 12.dp)) {
                Button(onClick = onEditarClick) {
                    Text("Editar")
                }
                Button(
                    onClick = { viewModel.eliminarPostulacion() },
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text("Eliminar")
                }
            }

            Text(
                text = "Etapas del proceso",
                modifier = Modifier.padding(top = 24.dp, bottom = 8.dp)
            )

            if (uiState.etapas.isEmpty()) {
                Text(text = "Aún no hay etapas registradas")
            } else {
                LazyColumn {
                    items(uiState.etapas) { etapa ->
                        EtapaItem(etapa)
                    }
                }
            }
        }
    }
}

@Composable
private fun EtapaItem(etapa: EtapaProceso) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = etapa.tipo)
            Text(text = "Resultado: ${etapa.resultado}")
        }
    }
}