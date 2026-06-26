package com.pabloisla.mipostulacion.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Laptop
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pabloisla.mipostulacion.MiPostulacionApp
import com.pabloisla.mipostulacion.data.local.EtapaProceso
import com.pabloisla.mipostulacion.ui.theme.Carbon
import com.pabloisla.mipostulacion.ui.theme.Indigo
import com.pabloisla.mipostulacion.ui.theme.colorPorResultado
import com.pabloisla.mipostulacion.ui.theme.iconoPorEstado
import com.pabloisla.mipostulacion.util.formatearFecha
import com.pabloisla.mipostulacion.util.formatearFechaHora
import com.pabloisla.mipostulacion.viewmodel.PostulacionDetailViewModel
import com.pabloisla.mipostulacion.viewmodel.postulacionDetailViewModelFactory

private fun iconoPorTipoEtapa(tipo: String): ImageVector = when {
    tipo.contains("RRHH", ignoreCase = true) -> Icons.Default.Groups
    tipo.contains("Técnica", ignoreCase = true) -> Icons.Default.Laptop
    tipo.contains("Prueba", ignoreCase = true) || tipo.contains("Caso", ignoreCase = true) -> Icons.AutoMirrored.Filled.Assignment
    tipo.contains("Resultado", ignoreCase = true) -> Icons.Default.Flag
    else -> Icons.Default.Forum
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
fun PostulacionDetailScreen(
    postulacionId: Long,
    onEliminado: () -> Unit,
    onEditarClick: () -> Unit,
    onAgregarEtapaClick: () -> Unit,
    onEditarEtapaClick: (Long) -> Unit,
    onBackClick: () -> Unit
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
            TopAppBar(
                title = { Text("Detalle de postulación") },
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

        val postulacion = uiState.postulacion
        if (postulacion == null) {
            Text(
                text = "Esta postulación ya no existe",
                modifier = Modifier.padding(innerPadding).padding(32.dp)
            )
            return@Scaffold
        }

        LazyColumn(modifier = Modifier.padding(innerPadding).padding(16.dp)) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.horizontalGradient(listOf(Indigo, Carbon)),
                                    shape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp)
                                )
                                .padding(18.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(52.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(Color.White.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = inicialesDe(postulacion.empresa),
                                        color = Color.White,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                }
                                Column(modifier = Modifier.padding(start = 14.dp).weight(1f)) {
                                    Text(
                                        text = postulacion.empresa,
                                        style = MaterialTheme.typography.titleLarge,
                                        color = Color.White
                                    )
                                    Text(
                                        text = postulacion.puesto,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.White.copy(alpha = 0.85f)
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier
                                    .padding(top = 14.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.White.copy(alpha = 0.18f))
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = iconoPorEstado(postulacion.estado),
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = postulacion.estado,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color.White,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(start = 6.dp)
                                )
                            }
                        }

                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(modifier = Modifier.fillMaxWidth()) {
                                DatoSecundario(
                                    icono = Icons.Default.Laptop,
                                    label = "Área",
                                    valor = postulacion.area
                                )
                                DatoSecundario(
                                    icono = Icons.Default.Groups,
                                    label = "Modalidad",
                                    valor = postulacion.modalidad
                                )
                                DatoSecundario(
                                    icono = Icons.Default.PriorityHigh,
                                    label = "Prioridad",
                                    valor = "${postulacion.prioridad}"
                                )
                            }

                            Row(modifier = Modifier.fillMaxWidth().padding(top = 12.dp)) {
                                DatoSecundario(
                                    icono = Icons.Default.CalendarMonth,
                                    label = "Fecha de postulación",
                                    valor = formatearFecha(postulacion.fechaPostulacion)
                                )
                            }

                            if (!postulacion.enlace.isNullOrBlank()) {
                                Row(
                                    modifier = Modifier.padding(top = 14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Link,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = postulacion.enlace,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(start = 6.dp)
                                    )
                                }
                            }
                            if (!postulacion.notas.isNullOrBlank()) {
                                Row(modifier = Modifier.padding(top = 10.dp)) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.Notes,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = postulacion.notas,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(start = 6.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Row(modifier = Modifier.fillMaxWidth().padding(top = 14.dp)) {
                    FilledTonalButton(
                        onClick = onEditarClick,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                        Text("Editar", modifier = Modifier.padding(start = 6.dp))
                    }
                    OutlinedButton(
                        onClick = { viewModel.eliminarPostulacion() },
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(1f).padding(start = 10.dp)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                        Text("Eliminar", modifier = Modifier.padding(start = 6.dp))
                    }
                }

                Text(
                    text = "Etapas del proceso",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 28.dp, bottom = 12.dp)
                )

                if (uiState.etapas.isEmpty()) {
                    Text(
                        text = "Aún no hay etapas registradas",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            itemsIndexed(uiState.etapas) { index, etapa ->
                EtapaTimelineItem(
                    etapa = etapa,
                    esUltima = index == uiState.etapas.lastIndex,
                    onClick = { onEditarEtapaClick(etapa.id) },
                    onEliminarClick = { viewModel.eliminarEtapa(etapa) }
                )
            }

            item {
                OutlinedButton(
                    onClick = onAgregarEtapaClick,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(text = "Agregar etapa", modifier = Modifier.padding(start = 6.dp))
                }
            }
        }
    }
}

@Composable
private fun DatoSecundario(icono: ImageVector, label: String, valor: String) {
    Row(modifier = Modifier.padding(end = 16.dp), verticalAlignment = Alignment.Top) {
        Icon(
            imageVector = icono,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(15.dp)
        )
        Column(modifier = Modifier.padding(start = 6.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = valor,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

@Composable
private fun EtapaTimelineItem(
    etapa: EtapaProceso,
    esUltima: Boolean,
    onClick: () -> Unit,
    onEliminarClick: () -> Unit
) {
    val color = colorPorResultado(etapa.resultado)

    Row(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(end = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(color),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = iconoPorTipoEtapa(etapa.tipo),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
            if (!esUltima) {
                Box(
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .width(2.dp)
                        .height(40.dp)
                        .background(MaterialTheme.colorScheme.outlineVariant)
                )
            }
        }

        Card(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = 14.dp)
                .clickable { onClick() },
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = etapa.tipo,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = formatearFechaHora(etapa.fecha),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = etapa.resultado,
                        style = MaterialTheme.typography.labelSmall,
                        color = color,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
                IconButton(onClick = onEliminarClick) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar etapa",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
