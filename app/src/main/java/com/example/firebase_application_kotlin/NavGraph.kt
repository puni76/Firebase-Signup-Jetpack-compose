package com.example.firebase_application_kotlin


import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavHostController
import com.example.firebase_application_kotlin.ui.screen.FormScreen
import com.example.firebase_application_kotlin.ui.screen.SignUpScreen

sealed class Screen(val route: String) {
    object SignUpScreen : Screen("signup_screen")
    object FormScreen : Screen("form_screen")
}

@Composable
fun NavGraphSetup(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Screen.SignUpScreen.route
    ) {
        composable(route = Screen.SignUpScreen.route) {
            SignUpScreen(navController = navController)
        }
        composable(route = Screen.FormScreen.route) {
            FormScreen()
        }
    }
}



