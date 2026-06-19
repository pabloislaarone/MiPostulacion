package com.pabloisla.mipostulacion.navigation

sealed class Screen(val route: String) {
    object Lista : Screen("lista")
    object Formulario : Screen("formulario")
}