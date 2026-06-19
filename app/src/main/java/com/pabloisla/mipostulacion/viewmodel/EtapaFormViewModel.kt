package com.pabloisla.mipostulacion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pabloisla.mipostulacion.data.local.EtapaProceso
import com.pabloisla.mipostulacion.data.repository.PostulacionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class EtapaFormViewModel(
    private val repository: PostulacionRepository,
    private val postulacionId: Long,
    etapaId: Long?
) : ViewModel() {

    private val _uiState = MutableStateFlow(EtapaFormUiState(etapaId = etapaId))
    val uiState: StateFlow<EtapaFormUiState> = _uiState.asStateFlow()

    init {
        if (etapaId != null) {
            cargarEtapa(etapaId)
        }
    }

    private fun cargarEtapa(id: Long) {
        _uiState.value = _uiState.value.copy(isLoading = true)
        viewModelScope.launch {
            val etapa = repository.obtenerEtapasPorPostulacion(postulacionId).first()
                .firstOrNull { it.id == id }
            if (etapa != null) {
                _uiState.value = _uiState.value.copy(
                    tipo = etapa.tipo,
                    fecha = etapa.fecha,
                    resultado = etapa.resultado,
                    notas = etapa.notas ?: "",
                    isLoading = false
                )
            }
        }
    }

    fun onTipoChange(value: String) {
        _uiState.value = _uiState.value.copy(tipo = value)
    }

    fun onResultadoChange(value: String) {
        _uiState.value = _uiState.value.copy(resultado = value)
    }

    fun onNotasChange(value: String) {
        _uiState.value = _uiState.value.copy(notas = value)
    }

    fun guardarEtapa() {
        val state = _uiState.value
        viewModelScope.launch {
            if (state.esEdicion) {
                repository.actualizarEtapa(
                    EtapaProceso(
                        id = state.etapaId!!,
                        postulacionId = postulacionId,
                        tipo = state.tipo,
                        fecha = state.fecha,
                        resultado = state.resultado,
                        notas = state.notas.ifBlank { null }
                    )
                )
            } else {
                repository.agregarEtapa(
                    EtapaProceso(
                        postulacionId = postulacionId,
                        tipo = state.tipo,
                        fecha = state.fecha,
                        resultado = state.resultado,
                        notas = state.notas.ifBlank { null }
                    )
                )
            }
            _uiState.value = state.copy(guardadoExitoso = true)
        }
    }
}