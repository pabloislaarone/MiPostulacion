package com.pabloisla.mipostulacion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pabloisla.mipostulacion.data.local.Postulacion
import com.pabloisla.mipostulacion.data.repository.PostulacionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class PostulacionFormViewModel(
    private val repository: PostulacionRepository,
    postulacionId: Long?
) : ViewModel() {

    private val _uiState = MutableStateFlow(PostulacionFormUiState(postulacionId = postulacionId))
    val uiState: StateFlow<PostulacionFormUiState> = _uiState.asStateFlow()

    init {
        if (postulacionId != null) {
            cargarPostulacion(postulacionId)
        }
    }

    private fun cargarPostulacion(id: Long) {
        _uiState.value = _uiState.value.copy(isLoading = true)
        viewModelScope.launch {
            val postulacion = repository.obtenerPorId(id).first()
            if (postulacion != null) {
                _uiState.value = _uiState.value.copy(
                    empresa = postulacion.empresa,
                    puesto = postulacion.puesto,
                    area = postulacion.area,
                    modalidad = postulacion.modalidad,
                    estado = postulacion.estado,
                    fechaPostulacion = postulacion.fechaPostulacion,
                    prioridad = postulacion.prioridad,
                    enlace = postulacion.enlace ?: "",
                    notas = postulacion.notas ?: "",
                    isLoading = false
                )
            }
        }
    }

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

    fun onFechaChange(value: Long) {
        _uiState.value = _uiState.value.copy(fechaPostulacion = value)
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
            if (state.esEdicion) {
                repository.actualizarPostulacion(
                    Postulacion(
                        id = state.postulacionId!!,
                        empresa = state.empresa,
                        puesto = state.puesto,
                        area = state.area,
                        modalidad = state.modalidad,
                        estado = state.estado,
                        fechaPostulacion = state.fechaPostulacion,
                        prioridad = state.prioridad,
                        enlace = state.enlace.ifBlank { null },
                        notas = state.notas.ifBlank { null }
                    )
                )
            } else {
                repository.registrarPostulacion(
                    Postulacion(
                        empresa = state.empresa,
                        puesto = state.puesto,
                        area = state.area,
                        modalidad = state.modalidad,
                        estado = state.estado,
                        fechaPostulacion = state.fechaPostulacion,
                        prioridad = state.prioridad,
                        enlace = state.enlace.ifBlank { null },
                        notas = state.notas.ifBlank { null }
                    )
                )
            }
            _uiState.value = state.copy(guardadoExitoso = true)
        }
    }
}