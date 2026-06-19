package com.pabloisla.mipostulacion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pabloisla.mipostulacion.data.repository.PostulacionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.IOException

class StatsViewModel(
    private val repository: PostulacionRepository
) : ViewModel() {

    private val _reto = MutableStateFlow<RetoUiState>(RetoUiState.Cargando)

    val uiState: StateFlow<StatsUiState> = combine(
        repository.obtenerTodas(),
        repository.obtenerEtapasProximas(),
        _reto
    ) { postulaciones, etapas, reto ->
        val activas = postulaciones.count { it.estado in listOf("Postulado", "En proceso", "Entrevista") }
        val ofertas = postulaciones.count { it.estado == "Oferta" }
        val rechazadas = postulaciones.count { it.estado == "Rechazado" }

        StatsUiState(
            progreso = ProgresoPostulaciones(activas = activas, ofertas = ofertas, rechazadas = rechazadas),
            proximasEtapas = etapas.take(3),
            reto = reto
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = StatsUiState()
    )

    init {
        cargarRetoDelDia()
    }

    fun cargarRetoDelDia() {
        _reto.value = RetoUiState.Cargando
        viewModelScope.launch {
            try {
                val pregunta = repository.obtenerRetoDelDia()
                val opciones = (pregunta.incorrect_answers + pregunta.correct_answer).shuffled()
                _reto.value = RetoUiState.Exito(pregunta, opciones)
            } catch (e: IOException) {
                _reto.value = RetoUiState.Error("No hay conexión a internet. Verifica tu red e intenta de nuevo.")
            } catch (e: Exception) {
                _reto.value = RetoUiState.Error("No se pudo cargar el reto técnico. Intenta de nuevo.")
            }
        }
    }
}