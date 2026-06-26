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

private val ESTADOS = listOf("Postulado", "En proceso", "Entrevista", "Oferta", "Rechazado")

class StatsViewModel(
    private val repository: PostulacionRepository
) : ViewModel() {

    private val _reto = MutableStateFlow<RetoUiState>(RetoUiState.Cargando)

    val uiState: StateFlow<StatsUiState> = combine(
        repository.obtenerTodas(),
        repository.obtenerEtapasProximas(),
        _reto
    ) { postulaciones, etapas, reto ->
        val conteoPorEstado = ESTADOS.associateWith { estado ->
            postulaciones.count { it.estado == estado }
        }

        val ahora = System.currentTimeMillis()
        StatsUiState(
            progreso = ProgresoPostulaciones(conteoPorEstado = conteoPorEstado),
            proximasEtapas = etapas.filter { it.fecha >= ahora }.take(3),
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