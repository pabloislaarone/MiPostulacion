package com.pabloisla.mipostulacion.viewmodel

import com.pabloisla.mipostulacion.data.local.Postulacion

data class PostulacionListUiState(
    val postulaciones: List<Postulacion> = emptyList(),
    val totalSinFiltrar: List<Postulacion> = emptyList(),
    val proximaEtapaPorPostulacion: Map<Long, Long> = emptyMap(),
    val filtroEstado: String? = null,
    val filtroArea: String? = null,
    val isLoading: Boolean = false
)