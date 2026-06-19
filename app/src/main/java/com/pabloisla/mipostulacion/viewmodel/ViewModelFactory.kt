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

fun postulacionFormViewModelFactory(
    app: MiPostulacionApp,
    postulacionId: Long? = null
): ViewModelProvider.Factory =
    viewModelFactory {
        initializer {
            PostulacionFormViewModel(app.container.postulacionRepository, postulacionId)
        }
    }

fun postulacionDetailViewModelFactory(
    app: MiPostulacionApp,
    postulacionId: Long
): ViewModelProvider.Factory =
    viewModelFactory {
        initializer {
            PostulacionDetailViewModel(app.container.postulacionRepository, postulacionId)
        }
    }

fun etapaFormViewModelFactory(
    app: MiPostulacionApp,
    postulacionId: Long
): ViewModelProvider.Factory =
    viewModelFactory {
        initializer {
            EtapaFormViewModel(app.container.postulacionRepository, postulacionId)
        }
    }

fun statsViewModelFactory(app: MiPostulacionApp): ViewModelProvider.Factory =
    viewModelFactory {
        initializer {
            StatsViewModel(app.container.postulacionRepository)
        }
    }