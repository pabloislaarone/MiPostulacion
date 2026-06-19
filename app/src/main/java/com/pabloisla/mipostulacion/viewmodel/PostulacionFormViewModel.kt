package com.pabloisla.mipostulacion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pabloisla.mipostulacion.data.local.Postulacion
import com.pabloisla.mipostulacion.data.repository.PostulacionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PostulacionFormViewModel(
    private val repository: PostulacionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PostulacionFormUiState())
    val uiState: StateFlow<PostulacionFormUiState> = _uiState.asStateFlow()

    fun onEmpresaChange(value: String) {
        _uiState.value = _uiState.value.copy(empresa = value, errorValidacion = null)
    }

    fun onPuestoChange(value: String) {
        _uiState.value = _uiState.value.copy(puesto = value, errorValidacion = null)
    }

    fun onAreaChange(value: String) {
        _uiState.value = _uiState.value.copy(area = value)
    }

    fun onModalidadChange(value: String) {
        _uiState.value = _uiState.value.copy(modalidad = value)
    }

    fun onEstadoChange(value: String) {
        _uiState.value = _uiState.value.copy(estado = value)
    }

    fun onPrioridadChange(value: Int) {
        _uiState.value = _uiState.value.copy(prioridad = value)
    }

    fun onEnlaceChange(value: String) {
        _uiState.value = _uiState.value.copy(enlace = value)
    }

    fun onNotasChange(value: String) {
        _uiState.value = _uiState.value.copy(notas = value)
    }

    fun guardarPostulacion() {
        val state = _uiState.value

        if (state.empresa.isBlank() || state.puesto.isBlank()) {
            _uiState.value = state.copy(
                errorValidacion = "Empresa y puesto son obligatorios"
            )
            return
        }

        viewModelScope.launch {
            repository.registrarPostulacion(
                Postulacion(
                    empresa = state.empresa,
                    puesto = state.puesto,
                    area = state.area,
                    modalidad = state.modalidad,
                    estado = state.estado,
                    fechaPostulacion = System.currentTimeMillis(),
                    prioridad = state.prioridad,
                    enlace = state.enlace.ifBlank { null },
                    notas = state.notas.ifBlank { null }
                )
            )
            _uiState.value = state.copy(guardadoExitoso = true)
        }
    }
}