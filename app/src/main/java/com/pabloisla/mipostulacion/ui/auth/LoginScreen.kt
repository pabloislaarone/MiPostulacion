package com.pabloisla.mipostulacion.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pabloisla.mipostulacion.MiPostulacionApp
import com.pabloisla.mipostulacion.viewmodel.AuthModo
import com.pabloisla.mipostulacion.viewmodel.LoginViewModel
import com.pabloisla.mipostulacion.viewmodel.loginViewModelFactory

@Composable
fun LoginScreen(
    onAutenticadoExitoso: () -> Unit
) {
    val context = LocalContext.current
    val app = context.applicationContext as MiPostulacionApp
    val viewModel: LoginViewModel = viewModel(factory = loginViewModelFactory(app))
    val uiState by viewModel.uiState

    LaunchedEffect(uiState.autenticadoExitoso) {
        if (uiState.autenticadoExitoso) {
            onAutenticadoExitoso()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .padding(top = 64.dp, bottom = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Work,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(30.dp)
                )
            }
            Text(
                text = "MiPostulación",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Medium,
                color = Color.White,
                modifier = Modifier.padding(top = 16.dp)
            )
            Text(
                text = "Organiza tu búsqueda de prácticas",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.8f),
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
            ModoSelector(
                modoActual = uiState.modo,
                onModoChange = viewModel::cambiarModo
            )

            if (uiState.modo == AuthModo.REGISTRO) {
                Row(modifier = Modifier.fillMaxWidth().padding(top = 20.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Nombre",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        OutlinedTextField(
                            value = uiState.nombre,
                            onValueChange = viewModel::onNombreChange,
                            placeholder = { Text("Pablo") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    Column(modifier = Modifier.weight(1f).padding(start = 8.dp)) {
                        Text(
                            text = "Apellido",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        OutlinedTextField(
                            value = uiState.apellido,
                            onValueChange = viewModel::onApellidoChange,
                            placeholder = { Text("Isla") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            Text(
                text = "Correo electrónico",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(
                    top = if (uiState.modo == AuthModo.REGISTRO) 14.dp else 20.dp,
                    bottom = 4.dp
                )
            )
            OutlinedTextField(
                value = uiState.correo,
                onValueChange = viewModel::onCorreoChange,
                placeholder = { Text("tu@correo.com") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "Contraseña",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 14.dp, bottom = 4.dp)
            )
            OutlinedTextField(
                value = uiState.contrasena,
                onValueChange = viewModel::onContrasenaChange,
                placeholder = { Text("••••••••") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )

            if (uiState.modo == AuthModo.LOGIN) {
                Text(
                    text = "¿Olvidaste tu contraseña?",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            if (uiState.errorMensaje != null) {
                Text(
                    text = uiState.errorMensaje!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Button(
                onClick = viewModel::continuar,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.fillMaxWidth().padding(top = 24.dp)
            ) {
                Text(if (uiState.modo == AuthModo.LOGIN) "Ingresar" else "Registrarme")
            }

            if (uiState.modo == AuthModo.REGISTRO) {
                Text(
                    text = "Al registrarte, aceptas organizar tus postulaciones de forma responsable",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }
}

@Composable
private fun ModoSelector(
    modoActual: AuthModo,
    onModoChange: (AuthModo) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(4.dp)
    ) {
        ModoTab(
            texto = "Iniciar sesión",
            seleccionado = modoActual == AuthModo.LOGIN,
            modifier = Modifier.weight(1f),
            onClick = { onModoChange(AuthModo.LOGIN) }
        )
        ModoTab(
            texto = "Registrarme",
            seleccionado = modoActual == AuthModo.REGISTRO,
            modifier = Modifier.weight(1f),
            onClick = { onModoChange(AuthModo.REGISTRO) }
        )
    }
}

@Composable
private fun ModoTab(
    texto: String,
    seleccionado: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (seleccionado) MaterialTheme.colorScheme.surface else Color.Transparent)
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = texto,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = if (seleccionado) FontWeight.Medium else FontWeight.Normal,
            color = if (seleccionado) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}