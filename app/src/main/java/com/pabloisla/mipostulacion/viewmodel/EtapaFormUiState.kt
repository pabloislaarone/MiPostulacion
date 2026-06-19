package com.pabloisla.mipostulacion.viewmodel

data class EtapaFormUiState(
    val tipo: String = "Entrevista RRHH",
    val fecha: Long = System.currentTimeMillis(),
    val resultado: String = "Pendiente",
    val notas: String = "",
    val guardadoExitoso: Boolean = false,
    val errorValidacion: String? = null
)