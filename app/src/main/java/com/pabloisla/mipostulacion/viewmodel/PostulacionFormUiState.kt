package com.pabloisla.mipostulacion.viewmodel

data class PostulacionFormUiState(
    val postulacionId: Long? = null,
    val empresa: String = "",
    val puesto: String = "",
    val area: String = "Frontend",
    val modalidad: String = "Remoto",
    val estado: String = "Postulado",
    val fechaPostulacion: Long = System.currentTimeMillis(),
    val prioridad: Int = 2,
    val enlace: String = "",
    val notas: String = "",
    val isLoading: Boolean = false,
    val guardadoExitoso: Boolean = false,
    val errorValidacion: String? = null
) {
    val esEdicion: Boolean get() = postulacionId != null
}