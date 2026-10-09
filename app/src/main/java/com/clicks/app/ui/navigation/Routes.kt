package com.clicks.app.ui.navigation

sealed class Routes(val route: String) {

    data object Onboarding : Routes("onboarding")

    data object Login : Routes("login")

    data object Register : Routes("register")

    data object Home : Routes("home")
}
