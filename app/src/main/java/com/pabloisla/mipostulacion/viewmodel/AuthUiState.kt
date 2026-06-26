package com.pabloisla.mipostulacion.viewmodel

data class AuthUiState(
    val modo: AuthModo = AuthModo.LOGIN,
    val nombre: String = "",
    val apellido: String = "",
    val correo: String = "",
    val contrasena: String = "",
    val confirmarContrasena: String = "",
    val isLoading: Boolean = false,
    val errorMensaje: String? = null,
    val autenticadoExitoso: Boolean = false
)

enum class AuthModo {
    LOGIN, REGISTRO
}
