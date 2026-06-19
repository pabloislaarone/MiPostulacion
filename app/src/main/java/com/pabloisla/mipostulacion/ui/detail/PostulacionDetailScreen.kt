package com.pabloisla.mipostulacion.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pabloisla.mipostulacion.MiPostulacionApp
import com.pabloisla.mipostulacion.data.local.EtapaProceso
import com.pabloisla.mipostulacion.viewmodel.PostulacionDetailViewModel
import com.pabloisla.mipostulacion.viewmodel.postulacionDetailViewModelFactory


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

        Column(modifier = Modifier.padding(innerPadding).padding(16.dp)) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.padding(end = 12.dp)) {
                            Text(
                                text = postulacion.empresa,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = postulacion.puesto,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = postulacion.estado,
                                style = MaterialTheme.typography.labelMedium,
                                color = colorPorEstado(postulacion.estado),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 14.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        DatoSecundario(label = "Área", valor = postulacion.area)
                        DatoSecundario(label = "Modalidad", valor = postulacion.modalidad)
                        DatoSecundario(label = "Prioridad", valor = "${postulacion.prioridad}")
                    }

                    if (!postulacion.enlace.isNullOrBlank()) {
                        Text(
                            text = "Enlace: ${postulacion.enlace}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 12.dp)
                        )
                    }
                    if (!postulacion.notas.isNullOrBlank()) {
                        Text(
                            text = postulacion.notas,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }

            Row(modifier = Modifier.fillMaxWidth().padding(top = 12.dp)) {
                OutlinedButton(
                    onClick = onEditarClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Editar")
                }
                OutlinedButton(
                        onClick = { viewModel.eliminarPostulacion() },
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                ),
                modifier = Modifier.weight(1f).padding(start = 8.dp)
                ) {
                Text("Eliminar")
            }
            }

            Text(
                text = "Etapas del proceso",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 24.dp, bottom = 8.dp)
            )

            if (uiState.etapas.isEmpty()) {
                Text(
                    text = "Aún no hay etapas registradas",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                LazyColumn {
                    items(uiState.etapas) { etapa ->
                        EtapaItem(
                            etapa = etapa,
                            onClick = { onEditarEtapaClick(etapa.id) },
                            onEliminarClick = { viewModel.eliminarEtapa(etapa) }
                        )
                    }
                }
            }

            OutlinedButton(
                onClick = onAgregarEtapaClick,
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
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

@Composable
private fun DatoSecundario(label: String, valor: String) {
    Column(modifier = Modifier.padding(end = 20.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = valor,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}

@Composable
private fun EtapaItem(
    etapa: EtapaProceso,
    onClick: () -> Unit,
    onEliminarClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = etapa.tipo,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = etapa.resultado,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
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