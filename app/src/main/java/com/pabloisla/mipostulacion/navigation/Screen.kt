package com.pabloisla.mipostulacion.navigation

sealed class Screen(val route: String) {
    object Lista : Screen("lista")
    object Formulario : Screen("formulario") {
        fun crearRutaEdicion(postulacionId: Long): String = "formulario/$postulacionId"
        const val rutaConArgumento = "formulario/{postulacionId}"
    }
    object Detalle : Screen("detalle/{postulacionId}") {
        fun crearRuta(postulacionId: Long): String = "detalle/$postulacionId"
    }
}