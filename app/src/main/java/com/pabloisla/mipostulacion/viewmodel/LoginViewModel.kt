package com.pabloisla.mipostulacion.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import com.pabloisla.mipostulacion.AppContainer

class LoginViewModel(
    private val appContainer: AppContainer
) : ViewModel() {

    private var _uiState = androidx.compose.runtime.mutableStateOf(LoginUiState())
    val uiState: androidx.compose.runtime.State<LoginUiState> = _uiState

    fun cambiarModo(modo: AuthModo) {
        _uiState.value = _uiState.value.copy(modo = modo, errorMensaje = null)
    }

    fun onNombreChange(value: String) {
        _uiState.value = _uiState.value.copy(nombre = value, errorMensaje = null)
    }

    fun onApellidoChange(value: String) {
        _uiState.value = _uiState.value.copy(apellido = value, errorMensaje = null)
    }

    fun onCorreoChange(value: String) {
        _uiState.value = _uiState.value.copy(correo = value, errorMensaje = null)
    }

    fun onContrasenaChange(value: String) {
        _uiState.value = _uiState.value.copy(contrasena = value, errorMensaje = null)
    }

    fun continuar() {
        val state = _uiState.value

        if (state.modo == AuthModo.REGISTRO && (state.nombre.isBlank() || state.apellido.isBlank())) {
            _uiState.value = state.copy(errorMensaje = "Ingresa tu nombre y apellido")
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(state.correo).matches()) {
            _uiState.value = state.copy(errorMensaje = "Ingresa un correo válido")
            return
        }

        if (state.contrasena.length < 6) {
            _uiState.value = state.copy(errorMensaje = "La contraseña debe tener al menos 6 caracteres")
            return
        }

        // Nota: en la Parte 2 aquí se conectará Firebase Authentication.
        // Por ahora, se guarda el nombre localmente para personalizar la sesión.
        val nombreParaGuardar = if (state.modo == AuthModo.REGISTRO) {
            state.nombre to state.apellido
        } else {
            "Usuario" to ""
        }
        appContainer.guardarSesion(nombreParaGuardar.first, nombreParaGuardar.second)

        _uiState.value = state.copy(autenticadoExitoso = true)
    }
}