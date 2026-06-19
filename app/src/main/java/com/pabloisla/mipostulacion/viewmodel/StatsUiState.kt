package com.pabloisla.mipostulacion.viewmodel

import com.pabloisla.mipostulacion.data.local.EtapaProceso
import com.pabloisla.mipostulacion.data.remote.TriviaQuestion

sealed interface RetoUiState {
    object Cargando : RetoUiState
    data class Exito(val pregunta: TriviaQuestion, val opciones: List<String>) : RetoUiState
    data class Error(val mensaje: String) : RetoUiState
}

data class StatsUiState(
    val conteoPorEstado: Map<String, Int> = emptyMap(),
    val proximasEtapas: List<EtapaProceso> = emptyList(),
    val reto: RetoUiState = RetoUiState.Cargando
)