package com.clicks.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.clicks.app.lib.session.TokenStorage
import com.clicks.app.lib.session.SessionRepository
import com.clicks.app.feature.login.LoginScreen
import com.clicks.app.feature.login.LoginViewModel
import com.clicks.app.feature.onboarding.OnboardingContainerScreen
import com.clicks.app.feature.register.ui.RegisterScreen
import com.clicks.app.feature.register.ui.RegisterViewModel
import com.clicks.app.feature.home.HomeScreen
import com.clicks.app.feature.menu.CategoryProductsScreen
import com.clicks.app.feature.menu.ProductDetailsScreen
import com.clicks.app.feature.cart.CartScreen
import com.clicks.app.feature.favorites.FavoritesScreen
import com.clicks.app.feature.checkout.CheckoutScreen
import com.clicks.app.feature.location.ui.DeliveryLocationViewModel
import com.clicks.app.feature.location.ui.LocationPickerScreen
import com.clicks.app.feature.location.ui.LocationPickerViewModel
import com.clicks.app.feature.profile.ProfileScreen
import com.clicks.app.feature.profile.ProfileViewModel
import com.clicks.app.feature.orders.OrdersScreen
import com.clicks.app.lib.viewmodel.StoreViewModel

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val sessionRepository = remember { SessionRepository(TokenStorage(context)) }
    val storeViewModel: StoreViewModel = viewModel()
    val deliveryLocationViewModel: DeliveryLocationViewModel = viewModel()

    val startDestination = when {
        !sessionRepository.isOnboardingCompleted() -> Routes.Onboarding.route
        sessionRepository.isLoggedIn() -> Routes.Home.route
        else -> Routes.Login.route
    }

    val goToLogin = {
        navController.navigate(Routes.Login.route) {
            popUpTo(Routes.Onboarding.route) { inclusive = true }
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Routes.Onboarding.route) {
            OnboardingContainerScreen(
                onFinished = {
                    sessionRepository.setOnboardingCompleted(true)
                    val nextRoute = if (sessionRepository.isLoggedIn()) Routes.Home.route else Routes.Login.route
                    navController.navigate(nextRoute) {
                        popUpTo(Routes.Onboarding.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }
        composable(Routes.Login.route) {
            val loginViewModel: LoginViewModel = viewModel()
            LoginScreen(
                viewModel = loginViewModel,
                onLoginSuccess = {
                    navController.navigate(Routes.Home.route) {
                        popUpTo(Routes.Login.route) {
                            inclusive = true
                        }
                    }
                },
                onRegisterClick = {
                    navController.navigate(Routes.Register.route)
                }
            ) {
                // Handle forgot password
            }
        }

        composable(Routes.Register.route) {
            val registerViewModel: RegisterViewModel = viewModel()
            RegisterScreen(
                viewModel = registerViewModel,
                onLoginClick = {
                    navController.popBackStack()
                },
                onRegisterSuccess = {
                    navController.navigate(Routes.Home.route) {
                        popUpTo(Routes.Login.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(Routes.Home.route) {
            val profileViewModel: ProfileViewModel = viewModel()
            HomeScreen(
                storeViewModel = storeViewModel,
                profileViewModel = profileViewModel,
                onCategoryClick = { _ ->
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
                onOrdersClick = {
                    navController.navigate(Routes.Orders.route)
                },
                onLogoutClick = {
                    sessionRepository.clearSession()
                    goToLogin()
                },
            )
        }

        composable(Routes.Products.route) {
            CategoryProductsScreen(
                storeViewModel = storeViewModel,
                onBackClick = { navController.popBackStack() },
                onCartClick = {
                    navController.navigate(Routes.Cart.route)
                },
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
                },
                onStartShopping = {
                    navController.navigate(Routes.Home.route) {
                        popUpTo(Routes.Home.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.Checkout.route) {
            CheckoutScreen(
                storeViewModel = storeViewModel,
                deliveryLocationViewModel = deliveryLocationViewModel,
                onBackClick = { navController.popBackStack() },
                onPickLocation = { navController.navigate(Routes.LocationPicker.route) },
                onOrderPlaced = {
                    navController.navigate(Routes.Home.route) {
                        popUpTo(Routes.Home.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.LocationPicker.route) {
            val pickerViewModel: LocationPickerViewModel = viewModel()
            LocationPickerScreen(
                viewModel = pickerViewModel,
                onConfirm = { deliveryLocationViewModel.confirm(it); navController.popBackStack() },
                onBackClick = { navController.popBackStack() }
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
            val profileViewModel: ProfileViewModel = viewModel()
            ProfileScreen(
                viewModel = profileViewModel,
                onBackClick = { navController.popBackStack() },
                onOrdersClick = { navController.navigate(Routes.Orders.route) },
                onLogoutClick = { profileViewModel.logout(); goToLogin() },
                onSessionExpired = { goToLogin() }
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
