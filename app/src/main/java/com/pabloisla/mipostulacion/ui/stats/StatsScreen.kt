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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pabloisla.mipostulacion.MiPostulacionApp
import com.pabloisla.mipostulacion.data.local.EtapaProceso
import com.pabloisla.mipostulacion.viewmodel.ProgresoPostulaciones
import com.pabloisla.mipostulacion.viewmodel.RetoUiState
import com.pabloisla.mipostulacion.viewmodel.StatsViewModel
import com.pabloisla.mipostulacion.viewmodel.statsViewModelFactory

private val ColorActivas = Color(0xFF6890B5)
private val ColorOfertas = Color(0xFF4AAE7A)
private val ColorRechazadas = Color(0xFFB5544A)

private fun colorPorResultado(resultado: String): Color = when (resultado) {
    "Aprobado" -> ColorOfertas
    "Rechazado" -> ColorRechazadas
    else -> ColorActivas
}

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

            if (uiState.progreso.total > 0) {
                ProgresoCard(uiState.progreso)
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Text(
                        text = "Aún no tienes postulaciones registradas",
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            if (uiState.proximasEtapas.isNotEmpty()) {
                Text(
                    text = "Próximas etapas",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 20.dp, bottom = 8.dp)
                )
                uiState.proximasEtapas.forEach { etapa ->
                    EtapaProximaCard(etapa)
                }
            }

            Text(
                text = "Reto técnico del día",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 20.dp, bottom = 8.dp)
            )

            RetoTecnicoSection(
                reto = uiState.reto,
                onReintentar = { viewModel.cargarRetoDelDia() }
            )
        }
    }
}

@Composable
private fun ProgresoCard(progreso: ProgresoPostulaciones) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Tu progreso",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "${progreso.porcentajeOfertas}% de tus postulaciones avanzaron a oferta",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 4.dp, bottom = 14.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp))
            ) {
                if (progreso.activas > 0) {
                    Box(
                        modifier = Modifier
                            .weight(progreso.activas.toFloat())
                            .height(10.dp)
                            .background(ColorActivas)
                    )
                }
                if (progreso.ofertas > 0) {
                    Box(
                        modifier = Modifier
                            .weight(progreso.ofertas.toFloat())
                            .height(10.dp)
                            .background(ColorOfertas)
                    )
                }
                if (progreso.rechazadas > 0) {
                    Box(
                        modifier = Modifier
                            .weight(progreso.rechazadas.toFloat())
                            .height(10.dp)
                            .background(ColorRechazadas)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                LeyendaItem(color = ColorActivas, texto = "${progreso.activas} activas")
                LeyendaItem(color = ColorOfertas, texto = "${progreso.ofertas} ofertas")
                LeyendaItem(color = ColorRechazadas, texto = "${progreso.rechazadas} rechazadas")
            }
        }
    }
}

@Composable
private fun LeyendaItem(color: Color, texto: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .width(8.dp)
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(color)
        )
        Text(
            text = texto,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 4.dp)
        )
    }
}

@Composable
private fun EtapaProximaCard(etapa: EtapaProceso) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
    ) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(48.dp)
                .background(colorPorResultado(etapa.resultado))
        )
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = etapa.tipo,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "Resultado: ${etapa.resultado}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
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
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
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
                        modifier = Modifier.padding(top = 12.dp)
                    ) {
                        Text("Reintentar")
                    }
                }
                is RetoUiState.Exito -> {
                    Text(
                        text = "Practica para tu próxima entrevista técnica",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    Text(
                        text = reto.pregunta.question,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    var seleccionada by remember { mutableStateOf<String?>(null) }

                    Column {
                        reto.opciones.forEach { opcion ->
                            val esEstaSeleccionada = opcion == seleccionada
                            val esCorrecta = opcion == reto.pregunta.correct_answer
                            val mostrarColor = seleccionada != null && esEstaSeleccionada

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (mostrarColor) {
                                            if (esCorrecta) Color(0xFFE3F3EA) else Color(0xFFFAE6E5)
                                        } else MaterialTheme.colorScheme.background
                                    )
                                    .clickable(enabled = seleccionada == null) { seleccionada = opcion }
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = opcion,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.weight(1f)
                                )
                                if (mostrarColor && esCorrecta) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = ColorOfertas
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