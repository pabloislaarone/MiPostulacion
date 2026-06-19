package com.pabloisla.mipostulacion.viewmodel

data class PostulacionFormUiState(
    val empresa: String = "",
    val puesto: String = "",
    val area: String = "Frontend",
    val modalidad: String = "Remoto",
    val estado: String = "Postulado",
    val prioridad: Int = 2,
    val enlace: String = "",
    val notas: String = "",
    val guardadoExitoso: Boolean = false,
    val errorValidacion: String? = null
)