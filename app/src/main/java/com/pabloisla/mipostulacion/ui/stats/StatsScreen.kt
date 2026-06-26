package com.pabloisla.mipostulacion.ui.stats

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.WorkOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pabloisla.mipostulacion.MiPostulacionApp
import com.pabloisla.mipostulacion.data.local.EtapaProceso
import com.pabloisla.mipostulacion.ui.theme.Ambar
import com.pabloisla.mipostulacion.ui.theme.Aqua
import com.pabloisla.mipostulacion.ui.theme.Carbon
import com.pabloisla.mipostulacion.ui.theme.EstadoOferta
import com.pabloisla.mipostulacion.ui.theme.EstadoRechazado
import com.pabloisla.mipostulacion.ui.theme.ExitoContainer
import com.pabloisla.mipostulacion.ui.theme.ErrorContainerSuave
import com.pabloisla.mipostulacion.ui.theme.Indigo
import com.pabloisla.mipostulacion.ui.theme.colorPorEstado
import com.pabloisla.mipostulacion.ui.theme.colorPorResultado
import com.pabloisla.mipostulacion.ui.theme.iconoPorEstado
import com.pabloisla.mipostulacion.util.formatearFechaHora
import com.pabloisla.mipostulacion.viewmodel.ProgresoPostulaciones
import com.pabloisla.mipostulacion.viewmodel.RetoUiState
import com.pabloisla.mipostulacion.viewmodel.StatsViewModel
import com.pabloisla.mipostulacion.viewmodel.statsViewModelFactory

private val ESTADOS = listOf("Postulado", "En proceso", "Entrevista", "Oferta", "Rechazado")
private val ColorOfertas = EstadoOferta
private val ColorRechazadas = EstadoRechazado

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    onBackClick: () -> Unit,
    onCerrarSesion: () -> Unit
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
                },
                actions = {
                    IconButton(onClick = onCerrarSesion) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Cerrar sesión"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            HeroProgreso(uiState.progreso)

            Column(modifier = Modifier.padding(16.dp)) {
                SeccionConIcono(icono = Icons.Default.PieChart, texto = "Resumen por estado", color = Indigo)
                if (uiState.progreso.total > 0) {
                    ResumenEstadosCard(uiState.progreso)
                } else {
                    EstadoVacioCard(
                        icono = Icons.Default.WorkOutline,
                        mensaje = "Aún no tienes postulaciones registradas. Crea una para ver tus estadísticas aquí."
                    )
                }

                SeccionConIcono(
                    icono = Icons.Default.EventAvailable,
                    texto = "Próximas etapas",
                    color = Aqua,
                    modifier = Modifier.padding(top = 28.dp)
                )
                if (uiState.proximasEtapas.isNotEmpty()) {
                    uiState.proximasEtapas.forEach { etapa ->
                        EtapaProximaCard(etapa)
                    }
                } else {
                    EstadoVacioCard(
                        icono = Icons.Default.EventBusy,
                        mensaje = "No tienes etapas programadas próximamente."
                    )
                }

                SeccionConIcono(
                    icono = Icons.Default.Psychology,
                    texto = "Reto técnico del día",
                    color = Ambar,
                    modifier = Modifier.padding(top = 28.dp)
                )
                RetoTecnicoSection(
                    reto = uiState.reto,
                    onReintentar = { viewModel.cargarRetoDelDia() }
                )

                Box(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun HeroProgreso(progreso: ProgresoPostulaciones) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Brush.horizontalGradient(listOf(Indigo, Carbon)))
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(82.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = { 1f },
                    modifier = Modifier.size(82.dp),
                    color = Color.White.copy(alpha = 0.25f),
                    strokeWidth = 8.dp
                )
                CircularProgressIndicator(
                    progress = { progreso.porcentajeOfertas / 100f },
                    modifier = Modifier.size(82.dp),
                    color = Color.White,
                    strokeWidth = 8.dp
                )
                Text(
                    text = "${progreso.porcentajeOfertas}%",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
            Column(modifier = Modifier.padding(start = 18.dp)) {
                Text(
                    text = "${progreso.total} postulaciones en total",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "avanzaron a oferta hasta ahora",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.85f),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun SeccionConIcono(
    icono: ImageVector,
    texto: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.padding(bottom = 12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(15.dp)
            )
        }
        Text(
            text = texto,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(start = 10.dp)
        )
    }
}

@Composable
private fun EstadoVacioCard(icono: ImageVector, mensaje: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icono,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(24.dp)
                )
            }
            Text(
                text = mensaje,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 12.dp)
            )
        }
    }
}

@Composable
private fun ResumenEstadosCard(progreso: ProgresoPostulaciones) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp))
            ) {
                ESTADOS.forEach { estado ->
                    val cantidad = progreso.conteoPorEstado[estado] ?: 0
                    if (cantidad > 0) {
                        Box(
                            modifier = Modifier
                                .weight(cantidad.toFloat())
                                .height(10.dp)
                                .background(colorPorEstado(estado))
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 18.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ESTADOS.forEach { estado ->
                    EstadoConteoItem(estado = estado, cantidad = progreso.conteoPorEstado[estado] ?: 0)
                }
            }
        }
    }
}

@Composable
private fun EstadoConteoItem(estado: String, cantidad: Int) {
    val color = colorPorEstado(estado)
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(color),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = iconoPorEstado(estado),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(15.dp)
            )
        }
        Text(
            text = "$cantidad",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = 6.dp)
        )
        Text(
            text = estado,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun EtapaProximaCard(etapa: EtapaProceso) {
    val color = colorPorResultado(etapa.resultado)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .padding(12.dp)
                .size(40.dp)
                .clip(CircleShape)
                .background(color),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.EventAvailable,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
        }
        Column(modifier = Modifier.padding(end = 12.dp, top = 12.dp, bottom = 12.dp)) {
            Text(
                text = etapa.tipo,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = formatearFechaHora(etapa.fecha),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Resultado: ${etapa.resultado}",
                style = MaterialTheme.typography.bodySmall,
                color = color,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

@Composable
private fun RetoTecnicoSection(
    reto: RetoUiState,
    onReintentar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            when (reto) {
                is RetoUiState.Cargando -> {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(modifier = Modifier.padding(24.dp))
                    }
                }
                is RetoUiState.Error -> {
                    Text(text = reto.mensaje, style = MaterialTheme.typography.bodyMedium)
                    OutlinedButton(
                        onClick = onReintentar,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.padding(top = 12.dp)
                    ) {
                        Text("Reintentar")
                    }
                }
                is RetoUiState.Exito -> {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Ambar.copy(alpha = 0.15f))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = reto.pregunta.category,
                            style = MaterialTheme.typography.labelSmall,
                            color = Ambar,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Text(
                        text = reto.pregunta.question,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 12.dp, bottom = 14.dp)
                    )

                    var seleccionada by remember { mutableStateOf<String?>(null) }
                    val letras = listOf("A", "B", "C", "D")

                    Column {
                        reto.opciones.forEachIndexed { index, opcion ->
                            val esEstaSeleccionada = opcion == seleccionada
                            val esCorrecta = opcion == reto.pregunta.correct_answer
                            val mostrarColor = seleccionada != null && esEstaSeleccionada

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (mostrarColor) {
                                            if (esCorrecta) ExitoContainer else ErrorContainerSuave
                                        } else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                    )
                                    .clickable(enabled = seleccionada == null) { seleccionada = opcion }
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(26.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.surface),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = letras.getOrElse(index) { "" },
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                Text(
                                    text = opcion,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(start = 10.dp).weight(1f)
                                )
                                if (mostrarColor) {
                                    Icon(
                                        imageVector = if (esCorrecta) Icons.Default.Check else Icons.Default.Cancel,
                                        contentDescription = null,
                                        tint = if (esCorrecta) ColorOfertas else ColorRechazadas
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
