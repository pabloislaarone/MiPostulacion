package com.pabloisla.mipostulacion.viewmodel

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.viewmodel.initializer
import com.pabloisla.mipostulacion.MiPostulacionApp

fun postulacionListViewModelFactory(app: MiPostulacionApp): ViewModelProvider.Factory =
    viewModelFactory {
        initializer {
            PostulacionListViewModel(app.container.postulacionRepository)
        }
    }

fun postulacionFormViewModelFactory(app: MiPostulacionApp): ViewModelProvider.Factory =
    viewModelFactory {
        initializer {
            PostulacionFormViewModel(app.container.postulacionRepository)
        }
    }