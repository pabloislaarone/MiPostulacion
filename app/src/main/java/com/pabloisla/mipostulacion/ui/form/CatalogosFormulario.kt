package com.pabloisla.mipostulacion.ui.form

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

val EMPRESAS_SUGERIDAS = listOf(
    "Google", "Microsoft", "Amazon", "Meta", "Apple",
    "IBM", "Oracle", "SAP", "Accenture", "Deloitte",
    "Globant", "EPAM Systems", "NTT Data", "Indra",
    "Tata Consultancy Services (TCS)", "Softtek", "Encora",
    "Mercado Libre", "Rappi", "Rimac Seguros", "BCP",
    "Interbank", "Scotiabank Perú", "BBVA Perú", "Sura Perú",
    "Telefónica del Perú (Movistar)", "Claro Perú", "Entel Perú",
    "Alicorp", "Falabella", "Backus"
)

val PUESTOS_SUGERIDOS = listOf(
    "Practicante de Desarrollo Frontend",
    "Practicante de Desarrollo Backend",
    "Practicante Full Stack",
    "Practicante de Desarrollo Móvil",
    "Practicante de QA / Testing",
    "Practicante de Ciencia de Datos",
    "Practicante de DevOps",
    "Practicante de Soporte TI",
    "Desarrollador Junior",
    "Analista Programador Junior",
    "Trainee de Tecnología"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampoConSugerencias(
    valor: String,
    placeholder: String,
    icono: ImageVector,
    sugerencias: List<String>,
    onValueChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val opcionesFiltradas = remember(valor, sugerencias) {
        if (valor.isBlank()) sugerencias
        else sugerencias.filter { it.contains(valor, ignoreCase = true) }
    }
    val coincideExacto = opcionesFiltradas.any { it.equals(valor, ignoreCase = true) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = valor,
            onValueChange = {
                onValueChange(it)
                expanded = true
            },
            placeholder = { Text(placeholder) },
            leadingIcon = { Icon(icono, contentDescription = null) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            ),
            modifier = Modifier.fillMaxWidth().menuAnchor()
        )

        if (opcionesFiltradas.isNotEmpty() || (valor.isNotBlank() && !coincideExacto)) {
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                opcionesFiltradas.forEach { opcion ->
                    DropdownMenuItem(
                        text = { Text(opcion) },
                        onClick = {
                            onValueChange(opcion)
                            expanded = false
                        }
                    )
                }
                if (valor.isNotBlank() && !coincideExacto) {
                    DropdownMenuItem(
                        text = { Text("Agregar \"$valor\" como nueva opción") },
                        leadingIcon = { Icon(Icons.Default.Add, contentDescription = null) },
                        onClick = { expanded = false }
                    )
                }
            }
        }
    }
}
