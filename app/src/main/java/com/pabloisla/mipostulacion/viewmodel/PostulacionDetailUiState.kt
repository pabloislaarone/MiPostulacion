package com.pabloisla.mipostulacion.viewmodel

import com.pabloisla.mipostulacion.data.local.EtapaProceso
import com.pabloisla.mipostulacion.data.local.Postulacion

data class PostulacionDetailUiState(
    val postulacion: Postulacion? = null,
    val etapas: List<EtapaProceso> = emptyList(),
    val isLoading: Boolean = true,
    val eliminado: Boolean = false
)