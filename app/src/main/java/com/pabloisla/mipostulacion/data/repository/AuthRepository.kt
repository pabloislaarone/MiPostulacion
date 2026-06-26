package com.pabloisla.mipostulacion.data.repository

import com.pabloisla.mipostulacion.data.remote.firebase.FirebaseAuthSource

class AuthRepository(
    private val authSource: FirebaseAuthSource
) {
    fun haySesionActiva(): Boolean = authSource.haySesionActiva()

    fun usuarioActualId(): String? = authSource.usuarioActualId()

    suspend fun registrar(correo: String, contrasena: String): Result<Unit> =
        authSource.registrar(correo, contrasena)

    suspend fun iniciarSesion(correo: String, contrasena: String): Result<Unit> =
        authSource.iniciarSesion(correo, contrasena)

    fun cerrarSesion() {
        authSource.cerrarSesion()
    }
}
