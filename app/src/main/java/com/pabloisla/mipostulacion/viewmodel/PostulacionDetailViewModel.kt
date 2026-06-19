package com.pabloisla.mipostulacion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pabloisla.mipostulacion.data.local.EtapaProceso
import com.pabloisla.mipostulacion.data.repository.PostulacionRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PostulacionDetailViewModel(
    private val repository: PostulacionRepository,
    private val postulacionId: Long
) : ViewModel() {

    val uiState: StateFlow<PostulacionDetailUiState> = combine(
        repository.obtenerPorId(postulacionId),
        repository.obtenerEtapasPorPostulacion(postulacionId)
    ) { postulacion, etapas ->
        PostulacionDetailUiState(
            postulacion = postulacion,
            etapas = etapas,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PostulacionDetailUiState(isLoading = true)
    )

    fun eliminarPostulacion() {
        val postulacion = uiState.value.postulacion ?: return
        viewModelScope.launch {
            repository.eliminarPostulacion(postulacion)
        }
    }

    fun eliminarEtapa(etapa: EtapaProceso) {
        viewModelScope.launch {
            repository.eliminarEtapa(etapa)
        }
    }
}