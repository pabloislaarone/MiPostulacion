package com.pabloisla.mipostulacion.ui.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.FilterAltOff
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.WorkOutline
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pabloisla.mipostulacion.MiPostulacionApp
import com.pabloisla.mipostulacion.data.local.Postulacion
import com.pabloisla.mipostulacion.ui.theme.Carbon
import com.pabloisla.mipostulacion.ui.theme.Indigo
import com.pabloisla.mipostulacion.ui.theme.colorPorEstado
import com.pabloisla.mipostulacion.ui.theme.iconoPorEstado
import com.pabloisla.mipostulacion.util.formatearFechaHora
import com.pabloisla.mipostulacion.viewmodel.PostulacionListViewModel
import com.pabloisla.mipostulacion.viewmodel.postulacionListViewModelFactory

private val ESTADOS = listOf("Postulado", "En proceso", "Entrevista", "Oferta", "Rechazado")
private val AREAS = listOf("Frontend", "Backend", "Móvil", "Datos", "QA", "Otro")
private const val MINIMO_PARA_MOSTRAR_FILTROS = 5

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

    val hayFiltrosActivos = uiState.filtroEstado != null || uiState.filtroArea != null

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAgregarClick,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Nueva postulación") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            CabeceraInicio(
                totalPostulaciones = uiState.totalSinFiltrar.size,
                onEstadisticasClick = onEstadisticasClick
            )

            if (uiState.postulaciones.isNotEmpty() || hayFiltrosActivos) {
                ResumenRapido(postulacionesTotales = uiState.totalSinFiltrar)
            }

            if (uiState.totalSinFiltrar.size >= MINIMO_PARA_MOSTRAR_FILTROS) {
                LazyRow(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                    items(ESTADOS) { estado ->
                        val seleccionado = uiState.filtroEstado == estado
                        FilterChip(
                            selected = seleccionado,
                            onClick = {
                                val nuevoFiltro = if (seleccionado) null else estado
                                viewModel.aplicarFiltroEstado(nuevoFiltro)
                            },
                            label = { Text(estado) },
                            leadingIcon = {
                                Icon(
                                    imageVector = iconoPorEstado(estado),
                                    contentDescription = null,
                                    tint = if (seleccionado) Color.White else colorPorEstado(estado),
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = colorPorEstado(estado),
                                selectedLabelColor = Color.White
                            ),
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

                if (hayFiltrosActivos) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AssistChip(
                            onClick = {
                                viewModel.aplicarFiltroEstado(null)
                                viewModel.aplicarFiltroArea(null)
                            },
                            label = { Text("Quitar filtros") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        )
                    }
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    uiState.isLoading -> {
                        CircularProgressIndicator(modifier = Modifier.padding(32.dp))
                    }
                    uiState.postulaciones.isEmpty() && hayFiltrosActivos -> {
                        EstadoVacio(
                            icono = Icons.Default.SearchOff,
                            titulo = "No hay postulaciones con esos filtros",
                            subtitulo = "Prueba combinando otros filtros o quítalos para ver todas"
                        ) {
                            OutlinedButton(onClick = {
                                viewModel.aplicarFiltroEstado(null)
                                viewModel.aplicarFiltroArea(null)
                            }) {
                                Icon(
                                    Icons.Default.FilterAltOff,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(text = "Quitar filtros", modifier = Modifier.padding(start = 6.dp))
                            }
                        }
                    }
                    uiState.postulaciones.isEmpty() -> {
                        EstadoVacio(
                            icono = Icons.Default.WorkOutline,
                            titulo = "Aún no tienes postulaciones registradas",
                            subtitulo = "Toca \"Nueva postulación\" para registrar tu primera práctica"
                        )
                    }
                    else -> {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(uiState.postulaciones) { postulacion ->
                                PostulacionItem(
                                    postulacion = postulacion,
                                    proximaEtapaFecha = uiState.proximaEtapaPorPostulacion[postulacion.id],
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CabeceraInicio(
    totalPostulaciones: Int,
    onEstadisticasClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.horizontalGradient(listOf(Indigo, Carbon)),
                shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
            )
            .padding(horizontal = 20.dp, vertical = 18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.22f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.WorkOutline,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
            Column(modifier = Modifier.padding(start = 12.dp).weight(1f)) {
                Text(
                    text = "Mis postulaciones",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "$totalPostulaciones postulaciones registradas",
                    color = Color.White.copy(alpha = 0.85f),
                    style = MaterialTheme.typography.labelMedium
                )
            }
            IconButton(onClick = onEstadisticasClick) {
                Icon(Icons.Default.BarChart, contentDescription = "Estadísticas", tint = Color.White)
            }
        }
    }
}

@Composable
private fun EstadoVacio(
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    titulo: String,
    subtitulo: String,
    accion: (@Composable () -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(88.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(40.dp)
            )
        }
        Text(
            text = titulo,
            style = MaterialTheme.typography.titleMedium,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.padding(top = 20.dp)
        )
        Text(
            text = subtitulo,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.padding(top = 6.dp, bottom = if (accion != null) 16.dp else 0.dp)
        )
        accion?.invoke()
    }
}

@Composable
private fun ResumenRapido(postulacionesTotales: List<Postulacion>) {
    if (postulacionesTotales.isEmpty()) return
    val conteoPorEstado = postulacionesTotales.groupingBy { it.estado }.eachCount().entries.take(4)

    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Tu panorama",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                conteoPorEstado.forEach { (estado, cantidad) ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(colorPorEstado(estado)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = iconoPorEstado(estado),
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Text(
                            text = "$cantidad",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(top = 6.dp)
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
fun PostulacionItem(
    postulacion: Postulacion,
    proximaEtapaFecha: Long? = null,
    onClick: () -> Unit
) {
    val colorEstado = colorPorEstado(postulacion.estado)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(colorEstado.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = inicialesDe(postulacion.empresa),
                        style = MaterialTheme.typography.titleSmall,
                        color = colorEstado,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Column(modifier = Modifier.padding(start = 12.dp).weight(1f)) {
                    Text(
                        text = postulacion.empresa,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = postulacion.puesto,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = postulacion.area,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(colorEstado)
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = iconoPorEstado(postulacion.estado),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = postulacion.estado,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }

            if (proximaEtapaFecha != null) {
                Row(
                    modifier = Modifier.padding(top = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.EventAvailable,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = "Próxima etapa: ${formatearFechaHora(proximaEtapaFecha)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(start = 6.dp)
                    )
                }
            }
        }
    }
}
