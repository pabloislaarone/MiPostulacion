package com.pabloisla.mipostulacion.ui.list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Card
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
@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

@Composable
fun PostulacionListScreen() {
    val context = LocalContext.current
    val app = context.applicationContext as MiPostulacionApp
    val viewModel: PostulacionListViewModel = viewModel(factory = postulacionListViewModelFactory(app))

    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Mis Postulaciones") })
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
                            PostulacionItem(postulacion)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PostulacionItem(postulacion: Postulacion) {
    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Box(modifier = Modifier.padding(16.dp)) {
            Text(text = "${postulacion.empresa} — ${postulacion.puesto}")
        }
    }
}