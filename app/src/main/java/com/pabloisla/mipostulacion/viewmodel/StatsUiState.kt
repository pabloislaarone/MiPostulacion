package com.pabloisla.mipostulacion.viewmodel

import com.pabloisla.mipostulacion.data.local.EtapaProceso
import com.pabloisla.mipostulacion.data.remote.TriviaQuestion

sealed interface RetoUiState {
    object Cargando : RetoUiState
    data class Exito(val pregunta: TriviaQuestion, val opciones: List<String>) : RetoUiState
    data class Error(val mensaje: String) : RetoUiState
}

data class ProgresoPostulaciones(
    val conteoPorEstado: Map<String, Int> = emptyMap()
) {
    val total: Int get() = conteoPorEstado.values.sum()
    val ofertas: Int get() = conteoPorEstado["Oferta"] ?: 0
    val porcentajeOfertas: Int get() = if (total == 0) 0 else (ofertas * 100) / total
}

data class StatsUiState(
    val progreso: ProgresoPostulaciones = ProgresoPostulaciones(),
    val proximasEtapas: List<EtapaProceso> = emptyList(),
    val reto: RetoUiState = RetoUiState.Cargando
)