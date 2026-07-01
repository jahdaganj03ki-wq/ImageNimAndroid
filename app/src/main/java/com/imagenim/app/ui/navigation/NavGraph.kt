package com.imagenim.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.imagenim.app.ui.screens.EditScreen
import com.imagenim.app.ui.screens.GenerateScreen
import com.imagenim.app.ui.screens.HomeScreen
import com.imagenim.app.ui.screens.SettingsScreen

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Generate : Screen("generate")
    data object Edit : Screen("edit")
    data object Settings : Screen("settings")
}

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            HomeScreen(
                onGenerateClick = { navController.navigate(Screen.Generate.route) },
                onEditClick = { navController.navigate(Screen.Edit.route) },
                onSettingsClick = { navController.navigate(Screen.Settings.route) }
            )
        }
        composable(Screen.Generate.route) {
            GenerateScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.Edit.route) {
            EditScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.Settings.route) {
            SettingsScreen(onBack = { navController.popBackStack() })
        }
    }
}
