package com.clicks.app.ui.navigation

sealed class Routes(val route: String) {

    data object Onboarding : Routes("onboarding")

    data object Login : Routes("login")

    data object Register : Routes("register")

    data object Home : Routes("home")

    data object Products : Routes("products")

    data object ProductDetails : Routes("product_details/{productId}") {
        fun createRoute(productId: Int) = "product_details/$productId"
    }

    data object Cart : Routes("cart")

    data object Favorites : Routes("favorites")

    data object Checkout : Routes("checkout")

    data object Profile : Routes("profile")

    data object Orders : Routes("orders")
}
