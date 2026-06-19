package com.pabloisla.mipostulacion.viewmodel

data class EtapaFormUiState(
    val etapaId: Long? = null,
    val tipo: String = "Entrevista RRHH",
    val fecha: Long = System.currentTimeMillis(),
    val resultado: String = "Pendiente",
    val notas: String = "",
    val isLoading: Boolean = false,
    val guardadoExitoso: Boolean = false
) {
    val esEdicion: Boolean get() = etapaId != null
}