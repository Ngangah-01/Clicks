package com.clicks.app.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.clicks.app.ui.screens.products.StoreProduct
import com.clicks.app.ui.screens.products.sampleProducts

data class CartItem(
    val product: StoreProduct,
    var quantity: Int = 1
)

data class OrderItem(
    val orderId: String,
    val date: String,
    val total: Double,
    val status: String,
    val itemsSummary: String
)

class StoreViewModel : ViewModel() {
    // User profile persistent state
    var userName by mutableStateOf("William Smith")
    var userEmail by mutableStateOf("williamsmith@example.com")
    var userPhone by mutableStateOf("+1 (555) 234-5678")

    val favorites = mutableStateListOf<Int>()
    val cartItems = mutableStateListOf<CartItem>()
    val orders = mutableStateListOf<OrderItem>(
        OrderItem("ORD-78421", "Oct 24, 2023", 1190.00, "Delivered", "Classic Hoodie, Sneakers"),
        OrderItem("ORD-78390", "Sep 15, 2023", 450.00, "In Transit", "Denim Jacket")
    )

    var hasUnreadNotifications by mutableStateOf(true)

    val cartItemCount: Int
        get() = cartItems.sumOf { it.quantity }

    val cartSubtotal: Double
        get() = cartItems.sumOf { it.product.price * it.quantity }

    fun isFavorite(productId: Int): Boolean = favorites.contains(productId)

    fun toggleFavorite(productId: Int) {
        if (favorites.contains(productId)) {
            favorites.remove(productId)
        } else {
            favorites.add(productId)
        }
    }

    fun addToCart(productId: Int, quantity: Int = 1) {
        val product = sampleProducts.firstOrNull { it.id == productId } ?: return
        val existing = cartItems.find { it.product.id == productId }
        if (existing != null) {
            existing.quantity += quantity
        } else {
            cartItems.add(CartItem(product, quantity))
        }
    }

    fun updateQuantity(productId: Int, delta: Int) {
        val item = cartItems.find { it.product.id == productId } ?: return
        item.quantity += delta
        if (item.quantity <= 0) {
            cartItems.remove(item)
        }
    }

    fun removeFromCart(productId: Int) {
        cartItems.removeAll { it.product.id == productId }
    }

    fun placeOrder() {
        if (cartItems.isEmpty()) return
        val newOrder = OrderItem(
            orderId = "ORD-${(10000..99999).random()}",
            date = "Today",
            total = cartSubtotal + 80.0,
            status = "Processing",
            itemsSummary = cartItems.joinToString(", ") { "${it.quantity}x ${it.product.name}" }
        )
        orders.add(0, newOrder)
        cartItems.clear()
    }

    fun updateProfile(name: String, email: String, phone: String) {
        userName = name
        userEmail = email
        userPhone = phone
    }
}
