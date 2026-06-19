package com.pabloisla.mipostulacion.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.pabloisla.mipostulacion.ui.auth.LoginScreen
import com.pabloisla.mipostulacion.ui.detail.PostulacionDetailScreen
import com.pabloisla.mipostulacion.ui.form.EtapaFormScreen
import com.pabloisla.mipostulacion.ui.form.PostulacionFormScreen
import com.pabloisla.mipostulacion.ui.list.PostulacionListScreen
import com.pabloisla.mipostulacion.ui.stats.StatsScreen

@Composable
fun AppNavHost() {
    val navController: NavHostController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onAutenticadoExitoso = {
                    navController.navigate(Screen.Lista.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Lista.route) {
            PostulacionListScreen(
                onAgregarClick = { navController.navigate(Screen.Formulario.route) },
                onPostulacionClick = { postulacionId ->
                    navController.navigate(Screen.Detalle.crearRuta(postulacionId))
                },
                onEstadisticasClick = {
                    navController.navigate(Screen.Estadisticas.route)
                }
            )
        }
        composable(Screen.Formulario.route) {
            PostulacionFormScreen(
                onGuardadoExitoso = { navController.popBackStack() },
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(
            route = Screen.Formulario.rutaConArgumento,
            arguments = listOf(navArgument("postulacionId") { type = NavType.LongType })
        ) { backStackEntry ->
            val postulacionId = backStackEntry.arguments?.getLong("postulacionId")
            PostulacionFormScreen(
                postulacionId = postulacionId,
                onGuardadoExitoso = { navController.popBackStack() },
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(
            route = Screen.Detalle.route,
            arguments = listOf(navArgument("postulacionId") { type = NavType.LongType })
        ) { backStackEntry ->
            val postulacionId = backStackEntry.arguments?.getLong("postulacionId") ?: 0L
            PostulacionDetailScreen(
                postulacionId = postulacionId,
                onEliminado = { navController.popBackStack() },
                onEditarClick = {
                    navController.navigate(Screen.Formulario.crearRutaEdicion(postulacionId))
                },
                onAgregarEtapaClick = {
                    navController.navigate(Screen.FormularioEtapa.crearRuta(postulacionId))
                },
                onEditarEtapaClick = { etapaId ->
                    navController.navigate(
                        Screen.FormularioEtapaEdicion.crearRuta(postulacionId, etapaId)
                    )
                },
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(
            route = Screen.FormularioEtapa.route,
            arguments = listOf(navArgument("postulacionId") { type = NavType.LongType })
        ) { backStackEntry ->
            val postulacionId = backStackEntry.arguments?.getLong("postulacionId") ?: 0L
            EtapaFormScreen(
                postulacionId = postulacionId,
                onGuardadoExitoso = { navController.popBackStack() },
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(
            route = Screen.FormularioEtapaEdicion.route,
            arguments = listOf(
                navArgument("postulacionId") { type = NavType.LongType },
                navArgument("etapaId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val postulacionId = backStackEntry.arguments?.getLong("postulacionId") ?: 0L
            val etapaId = backStackEntry.arguments?.getLong("etapaId")
            EtapaFormScreen(
                postulacionId = postulacionId,
                etapaId = etapaId,
                onGuardadoExitoso = { navController.popBackStack() },
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(Screen.Estadisticas.route) {
            StatsScreen(onBackClick = { navController.popBackStack() })
        }
    }
}