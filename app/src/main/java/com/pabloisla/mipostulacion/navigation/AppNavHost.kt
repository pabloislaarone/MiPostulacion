package com.pabloisla.mipostulacion.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pabloisla.mipostulacion.ui.form.PostulacionFormScreen
import com.pabloisla.mipostulacion.ui.list.PostulacionListScreen

@Composable
fun AppNavHost() {
    val navController: NavHostController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Lista.route
    ) {
        composable(Screen.Lista.route) {
            PostulacionListScreen(
                onAgregarClick = {
                    navController.navigate(Screen.Formulario.route)
                }
            )
        }
        composable(Screen.Formulario.route) {
            PostulacionFormScreen(
                onGuardadoExitoso = {
                    navController.popBackStack()
                }
            )
        }
    }
}