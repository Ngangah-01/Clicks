package com.clicks.app.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.clicks.app.ui.screens.login.LoginScreen
import com.clicks.app.ui.screens.onboarding.OnboardingContainerScreen
import com.clicks.app.ui.screens.register.RegisterScreen

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
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Home Screen")
            }
        }
    }
}
