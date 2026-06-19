package com.pabloisla.mipostulacion.viewmodel

import com.pabloisla.mipostulacion.data.local.Postulacion

data class PostulacionListUiState(
    val postulaciones: List<Postulacion> = emptyList(),
    val filtroEstado: String? = null,
    val filtroArea: String? = null,
    val isLoading: Boolean = false
)