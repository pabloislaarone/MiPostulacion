package com.pabloisla.mipostulacion.data.remote.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import kotlinx.coroutines.tasks.await

class FirebaseAuthSource {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun usuarioActualId(): String? = auth.currentUser?.uid

    fun haySesionActiva(): Boolean = auth.currentUser != null

    suspend fun registrar(correo: String, contrasena: String): Result<Unit> =
        try {
            auth.createUserWithEmailAndPassword(correo, contrasena).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception(mensajeDeError(e)))
        }

    suspend fun iniciarSesion(correo: String, contrasena: String): Result<Unit> =
        try {
            auth.signInWithEmailAndPassword(correo, contrasena).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception(mensajeDeError(e)))
        }

    fun cerrarSesion() {
        auth.signOut()
    }

    private fun mensajeDeError(error: Throwable): String = when (error) {
        is FirebaseAuthInvalidCredentialsException ->
            "El correo o la contraseña no son válidos."
        is FirebaseAuthUserCollisionException ->
            "Ya existe una cuenta registrada con ese correo."
        is FirebaseAuthInvalidUserException ->
            "No existe una cuenta con ese correo."
        is FirebaseAuthWeakPasswordException ->
            "La contraseña es demasiado débil. Usa al menos 6 caracteres."
        else ->
            error.message ?: "Ocurrió un error inesperado. Intenta de nuevo."
    }
}
