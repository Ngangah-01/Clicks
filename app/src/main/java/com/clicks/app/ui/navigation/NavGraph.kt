package com.clicks.app.ui.navigation


import androidx.compose.runtime.Composable

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.clicks.app.ui.screens.login.LoginScreen
import com.clicks.app.ui.screens.onboarding.OnboardingContainerScreen
import com.clicks.app.ui.screens.register.RegisterScreen
import com.clicks.app.ui.screens.home.HomeScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.Onboarding.route
    ) {
        composable(Routes.Onboarding.route) {
            OnboardingContainerScreen(
                onFinished = {
                    navController.navigate(Routes.Login.route) {
                        popUpTo(Routes.Onboarding.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }
        composable(Routes.Login.route) {
            LoginScreen(
                onLoginClick = { _, _ ->
                    navController.navigate(Routes.Home.route) {
                        popUpTo(Routes.Login.route) {
                            inclusive = true
                        }
                    }
                },
                onRegisterClick = {
                    navController.navigate(Routes.Register.route)
                },
                onForgotPasswordClick = {
                    // Handle forgot password
                },
                onBackClick = {
                    // Back handler from login if needed
                }
            )
        }

        composable(Routes.Register.route) {
            RegisterScreen(
                onRegisterSubmit = { _, _, _ ->
                    navController.navigate(Routes.Home.route) {
                        popUpTo(Routes.Login.route) {
                            inclusive = true
                        }
                    }
                },
                onLoginClick = {
                    navController.popBackStack()
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.Home.route) {
            HomeScreen()
        }
    }
}
