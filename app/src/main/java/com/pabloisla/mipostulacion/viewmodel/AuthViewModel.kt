package com.pabloisla.mipostulacion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pabloisla.mipostulacion.data.repository.AuthRepository
import com.pabloisla.mipostulacion.data.repository.PostulacionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository,
    private val postulacionRepository: PostulacionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

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

    fun onConfirmarContrasenaChange(value: String) {
        _uiState.value = _uiState.value.copy(confirmarContrasena = value, errorMensaje = null)
    }

    fun continuar() {
        val state = _uiState.value

        if (state.correo.isBlank() || state.contrasena.isBlank()) {
            _uiState.value = state.copy(errorMensaje = "Ingresa tu correo y contraseña")
            return
        }

        if (state.modo == AuthModo.REGISTRO) {
            if (state.nombre.isBlank() || state.apellido.isBlank()) {
                _uiState.value = state.copy(errorMensaje = "Ingresa tu nombre y apellido")
                return
            }
            if (state.contrasena != state.confirmarContrasena) {
                _uiState.value = state.copy(errorMensaje = "Las contraseñas no coinciden")
                return
            }
        }

        _uiState.value = state.copy(isLoading = true, errorMensaje = null)
        viewModelScope.launch {
            val resultado = if (state.modo == AuthModo.REGISTRO) {
                authRepository.registrar(state.nombre, state.apellido, state.correo, state.contrasena)
            } else {
                authRepository.iniciarSesion(state.correo, state.contrasena)
            }

            resultado
                .onSuccess {
                    authRepository.usuarioActualId()?.let { uid ->
                        postulacionRepository.sincronizarDesdeFirestore(uid)
                    }
                    _uiState.value = _uiState.value.copy(isLoading = false, autenticadoExitoso = true)
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMensaje = error.message ?: "Ocurrió un error inesperado."
                    )
                }
        }
    }
}
