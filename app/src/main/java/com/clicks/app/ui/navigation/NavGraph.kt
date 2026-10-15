package com.clicks.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.clicks.app.ui.screens.login.LoginScreen
import com.clicks.app.ui.screens.onboarding.OnboardingContainerScreen
import com.clicks.app.ui.screens.register.RegisterScreen
import com.clicks.app.ui.screens.home.HomeScreen
import com.clicks.app.ui.screens.products.CategoryProductsScreen
import com.clicks.app.ui.screens.products.ProductDetailsScreen
import com.clicks.app.ui.screens.cart.CartScreen
import com.clicks.app.ui.screens.favorites.FavoritesScreen
import com.clicks.app.ui.screens.checkout.CheckoutScreen
import com.clicks.app.ui.screens.profile.ProfileScreen
import com.clicks.app.ui.screens.orders.OrdersScreen
import com.clicks.app.ui.viewmodel.StoreViewModel
import com.clicks.app.ui.screens.register.RegisterViewModel

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val storeViewModel: StoreViewModel = viewModel()

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
                onBackClick = {6
                    // Back handler from login if needed
                }
            )
        }

        composable(Routes.Register.route) {

            val registerViewModel: RegisterViewModel = viewModel()

            RegisterScreen(
                viewModel = registerViewModel,
                onLoginClick = {
                    navController.navigate(Routes.Login.route)
                }
            )
        }

        composable(Routes.Home.route) {
            HomeScreen(
                storeViewModel = storeViewModel,
                onCategoryClick = { category ->
                    navController.navigate(Routes.Products.route)
                },
                onViewAllClick = {
                    navController.navigate(Routes.Products.route)
                },
                onProductClick = { productId ->
                    navController.navigate(Routes.ProductDetails.createRoute(productId))
                },
                onFavoritesClick = {
                    navController.navigate(Routes.Favorites.route)
                },
                onCartClick = {
                    navController.navigate(Routes.Cart.route)
                },
                onProfileClick = {
                    navController.navigate(Routes.Profile.route)
                },
                onNotificationClick = {
                    // Notification action
                }
            )
        }

        composable(Routes.Products.route) {
            CategoryProductsScreen(
                storeViewModel = storeViewModel,
                onBackClick = { navController.popBackStack() },
                onProductClick = { productId ->
                    navController.navigate(Routes.ProductDetails.createRoute(productId))
                }
            )
        }

        composable(Routes.ProductDetails.route) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")?.toIntOrNull() ?: 1
            ProductDetailsScreen(
                productId = productId,
                storeViewModel = storeViewModel,
                onBackClick = { navController.popBackStack() },
                onCartClick = {
                    navController.navigate(Routes.Cart.route)
                },
                onSimilarProductClick = { simId ->
                    navController.navigate(Routes.ProductDetails.createRoute(simId))
                }
            )
        }

        composable(Routes.Cart.route) {
            CartScreen(
                storeViewModel = storeViewModel,
                onBackClick = { navController.popBackStack() },
                onProceedToBuy = {
                    navController.navigate(Routes.Checkout.route)
                }
            )
        }

        composable(Routes.Checkout.route) {
            CheckoutScreen(
                storeViewModel = storeViewModel,
                onBackClick = { navController.popBackStack() },
                onOrderPlaced = {
                    navController.navigate(Routes.Home.route) {
                        popUpTo(Routes.Home.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.Favorites.route) {
            FavoritesScreen(
                storeViewModel = storeViewModel,
                onBackClick = { navController.popBackStack() },
                onProductClick = { productId ->
                    navController.navigate(Routes.ProductDetails.createRoute(productId))
                }
            )
        }

        composable(Routes.Profile.route) {
            ProfileScreen(
                storeViewModel = storeViewModel,
                onBackClick = { navController.popBackStack() },
                onOrdersClick = {
                    navController.navigate(Routes.Orders.route)
                },
                onLogoutClick = {
                    navController.navigate(Routes.Login.route) {
                        popUpTo(Routes.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.Orders.route) {
            OrdersScreen(
                storeViewModel = storeViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
