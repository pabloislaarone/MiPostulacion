package com.pabloisla.mipostulacion.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.WorkOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pabloisla.mipostulacion.MiPostulacionApp
import com.pabloisla.mipostulacion.ui.theme.Carbon
import com.pabloisla.mipostulacion.ui.theme.Indigo
import com.pabloisla.mipostulacion.viewmodel.AuthModo
import com.pabloisla.mipostulacion.viewmodel.AuthViewModel
import com.pabloisla.mipostulacion.viewmodel.authViewModelFactory
import androidx.compose.runtime.collectAsState

@Composable
fun LoginScreen(
    onAutenticadoExitoso: () -> Unit
) {
    val context = LocalContext.current
    val app = context.applicationContext as MiPostulacionApp
    val viewModel: AuthViewModel = viewModel(factory = authViewModelFactory(app))
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.autenticadoExitoso) {
        if (uiState.autenticadoExitoso) {
            onAutenticadoExitoso()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(Indigo, Carbon)))
                .padding(top = 72.dp, bottom = 64.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(Color.White.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.WorkOutline,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(34.dp)
                )
            }
            Text(
                text = "MiPostulación",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                modifier = Modifier.padding(top = 18.dp)
            )
            Text(
                text = "Organiza tu búsqueda de prácticas",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.85f),
                modifier = Modifier.padding(top = 6.dp)
            )
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = (-32).dp),
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
                ModoSelector(
                    modoActual = uiState.modo,
                    onModoChange = viewModel::cambiarModo
                )

                Text(
                    text = "Correo electrónico",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 22.dp, bottom = 6.dp)
                )
                OutlinedTextField(
                    value = uiState.correo,
                    onValueChange = viewModel::onCorreoChange,
                    placeholder = { Text("tu@correo.com") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    shape = RoundedCornerShape(14.dp),
                    colors = campoColors(),
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Contraseña",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 16.dp, bottom = 6.dp)
                )
                OutlinedTextField(
                    value = uiState.contrasena,
                    onValueChange = viewModel::onContrasenaChange,
                    placeholder = { Text("••••••••") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    shape = RoundedCornerShape(14.dp),
                    colors = campoColors(),
                    modifier = Modifier.fillMaxWidth()
                )

                if (uiState.errorMensaje != null) {
                    Text(
                        text = uiState.errorMensaje!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 10.dp)
                    )
                }

                Button(
                    onClick = viewModel::continuar,
                    enabled = !uiState.isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Carbon,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().height(52.dp).padding(top = 26.dp)
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            if (uiState.modo == AuthModo.LOGIN) "Ingresar" else "Registrarme",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun campoColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = MaterialTheme.colorScheme.surface,
    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
    focusedBorderColor = MaterialTheme.colorScheme.primary,
    unfocusedBorderColor = MaterialTheme.colorScheme.outline
)

@Composable
private fun ModoSelector(
    modoActual: AuthModo,
    onModoChange: (AuthModo) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
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
            .clip(RoundedCornerShape(11.dp))
            .background(if (seleccionado) MaterialTheme.colorScheme.primary else Color.Transparent)
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = texto,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = if (seleccionado) FontWeight.SemiBold else FontWeight.Medium,
            color = if (seleccionado) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
