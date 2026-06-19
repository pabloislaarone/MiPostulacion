package com.pabloisla.mipostulacion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pabloisla.mipostulacion.data.repository.PostulacionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class PostulacionListViewModel(
    private val repository: PostulacionRepository
) : ViewModel() {

    private val _filtroEstado = MutableStateFlow<String?>(null)
    private val _filtroArea = MutableStateFlow<String?>(null)

    val uiState: StateFlow<PostulacionListUiState> = combine(
        repository.obtenerTodas(),
        _filtroEstado,
        _filtroArea
    ) { postulaciones, estado, area ->
        val filtradas = postulaciones.filter { postulacion ->
            (estado == null || postulacion.estado == estado) &&
                    (area == null || postulacion.area == area)
        }
        PostulacionListUiState(
            postulaciones = filtradas,
            filtroEstado = estado,
            filtroArea = area,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PostulacionListUiState(isLoading = true)
    )

    fun aplicarFiltroEstado(estado: String?) {
        _filtroEstado.value = estado
    }

    fun aplicarFiltroArea(area: String?) {
        _filtroArea.value = area
    }
}