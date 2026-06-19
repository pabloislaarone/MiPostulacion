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
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import com.pabloisla.mipostulacion.data.local.Postulacion
import com.pabloisla.mipostulacion.viewmodel.PostulacionListViewModel
import com.pabloisla.mipostulacion.viewmodel.postulacionListViewModelFactory

private val ESTADOS = listOf("Postulado", "En proceso", "Entrevista", "Oferta", "Rechazado")
private val AREAS = listOf("Frontend", "Backend", "Móvil", "Datos", "QA", "Otro")
private const val MINIMO_PARA_MOSTRAR_FILTROS = 5

private fun colorPorEstado(estado: String): Color = when (estado) {
    "Postulado" -> Color(0xFF6890B5)
    "En proceso" -> Color(0xFFB5A04A)
    "Entrevista" -> Color(0xFFB57D4A)
    "Oferta" -> Color(0xFF4AAE7A)
    "Rechazado" -> Color(0xFFB5544A)
    else -> Color.Gray
}

private fun inicialesDe(nombre: String): String {
    val partes = nombre.trim().split(" ").filter { it.isNotBlank() }
    return when {
        partes.size >= 2 -> "${partes[0].first()}${partes[1].first()}".uppercase()
        partes.size == 1 -> partes[0].take(2).uppercase()
        else -> "?"
    }
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
    val nombreUsuario = app.container.nombreUsuario ?: "Usuario"
    var menuAbierto by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                ),
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = inicialesDe(nombreUsuario),
                                color = Color.White,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Column(modifier = Modifier.padding(start = 10.dp)) {
                            Text(
                                text = "Hola, $nombreUsuario",
                                color = Color.White,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "${uiState.postulaciones.size} postulaciones activas",
                                color = Color.White.copy(alpha = 0.8f),
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onEstadisticasClick) {
                        Icon(Icons.Default.BarChart, contentDescription = "Estadísticas", tint = Color.White)
                    }
                    Box {
                        IconButton(onClick = { menuAbierto = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Más opciones", tint = Color.White)
                        }
                        DropdownMenu(
                            expanded = menuAbierto,
                            onDismissRequest = { menuAbierto = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Cerrar sesión") },
                                onClick = { menuAbierto = false }
                            )
                        }
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

            if (uiState.postulaciones.isNotEmpty()) {
                ResumenRapido(postulaciones = uiState.postulaciones)
            }

            if (uiState.postulaciones.size >= MINIMO_PARA_MOSTRAR_FILTROS) {
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
private fun ResumenRapido(postulaciones: List<Postulacion>) {
    val conteoPorEstado = postulaciones.groupingBy { it.estado }.eachCount()

    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Tu panorama",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(modifier = Modifier.padding(top = 8.dp)) {
                conteoPorEstado.entries.take(4).forEach { (estado, cantidad) ->
                    Column(
                        modifier = Modifier.padding(end = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "$cantidad",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Medium,
                            color = colorPorEstado(estado)
                        )
                        Text(
                            text = estado,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
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