package com.pabloisla.mipostulacion.ui.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pabloisla.mipostulacion.MiPostulacionApp
import com.pabloisla.mipostulacion.data.local.Postulacion
import com.pabloisla.mipostulacion.viewmodel.PostulacionListViewModel
import com.pabloisla.mipostulacion.viewmodel.postulacionListViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostulacionListScreen(
    onAgregarClick: () -> Unit,
    onPostulacionClick: (Long) -> Unit
) {
    val context = LocalContext.current
    val app = context.applicationContext as MiPostulacionApp
    val viewModel: PostulacionListViewModel = viewModel(factory = postulacionListViewModelFactory(app))

    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Mis Postulaciones") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAgregarClick) {
                Icon(Icons.Default.Add, contentDescription = "Agregar postulación")
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(32.dp)
                    )
                }
                uiState.postulaciones.isEmpty() -> {
                    Text(
                        text = "Aún no tienes postulaciones registradas",
                        modifier = Modifier.padding(32.dp)
                    )
                }
                else -> {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(uiState.postulaciones) { postulacion ->
                            PostulacionItem(
                                postulacion = postulacion,
                                onClick = { onPostulacionClick(postulacion.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PostulacionItem(postulacion: Postulacion, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick() }
    ) {
        Box(modifier = Modifier.padding(16.dp)) {
            Text(text = "${postulacion.empresa} — ${postulacion.puesto}")
        }
    }
}