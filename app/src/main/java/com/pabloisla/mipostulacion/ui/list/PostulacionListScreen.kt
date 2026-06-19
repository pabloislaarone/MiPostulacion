package com.pabloisla.mipostulacion.ui.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pabloisla.mipostulacion.MiPostulacionApp
import com.pabloisla.mipostulacion.data.local.Postulacion
import com.pabloisla.mipostulacion.viewmodel.PostulacionListViewModel
import com.pabloisla.mipostulacion.viewmodel.postulacionListViewModelFactory

private val ESTADOS = listOf("Postulado", "En proceso", "Entrevista", "Oferta", "Rechazado")
private val AREAS = listOf("Frontend", "Backend", "Móvil", "Datos", "QA", "Otro")

private fun colorPorEstado(estado: String): Color = when (estado) {
    "Postulado" -> Color(0xFF6890B5)
    "En proceso" -> Color(0xFFB5A04A)
    "Entrevista" -> Color(0xFFB57D4A)
    "Oferta" -> Color(0xFF4AAE7A)
    "Rechazado" -> Color(0xFFB5544A)
    else -> Color.Gray
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostulacionListScreen(
    onAgregarClick: () -> Unit,
    onPostulacionClick: (Long) -> Unit,
    onEstadisticasClick: () -> Unit
) {
    val context = LocalContext.current
    val app = context.applicationContext as MiPostulacionApp
    val viewModel: PostulacionListViewModel = viewModel(factory = postulacionListViewModelFactory(app))

    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Postulaciones") },
                actions = {
                    IconButton(onClick = onEstadisticasClick) {
                        Icon(Icons.Default.BarChart, contentDescription = "Estadísticas")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAgregarClick) {
                Icon(Icons.Default.Add, contentDescription = "Agregar postulación")
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            LazyRow(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                items(ESTADOS) { estado ->
                    FilterChip(
                        selected = uiState.filtroEstado == estado,
                        onClick = {
                            val nuevoFiltro = if (uiState.filtroEstado == estado) null else estado
                            viewModel.aplicarFiltroEstado(nuevoFiltro)
                        },
                        label = { Text(estado) },
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            }

            LazyRow(modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)) {
                items(AREAS) { area ->
                    FilterChip(
                        selected = uiState.filtroArea == area,
                        onClick = {
                            val nuevoFiltro = if (uiState.filtroArea == area) null else area
                            viewModel.aplicarFiltroArea(nuevoFiltro)
                        },
                        label = { Text(area) },
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    uiState.isLoading -> {
                        CircularProgressIndicator(modifier = Modifier.padding(32.dp))
                    }
                    uiState.postulaciones.isEmpty() -> {
                        Column(modifier = Modifier.padding(32.dp)) {
                            Text(
                                text = "Aún no tienes postulaciones registradas",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Toca el botón + para registrar tu primera postulación",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                    else -> {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(uiState.postulaciones) { postulacion ->
                                PostulacionItem(
                                    postulacion = postulacion,
                                    onClick = { onPostulacionClick(postulacion.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PostulacionItem(postulacion: Postulacion, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .padding(top = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(colorPorEstado(postulacion.estado))
                )
            }

            Column(modifier = Modifier.padding(start = 12.dp)) {
                Text(
                    text = postulacion.empresa,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = postulacion.puesto,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(modifier = Modifier.padding(top = 6.dp)) {
                    Text(
                        text = postulacion.area,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = " · ${postulacion.estado}",
                        style = MaterialTheme.typography.labelSmall,
                        color = colorPorEstado(postulacion.estado)
                    )
                }
            }
        }
    }
}