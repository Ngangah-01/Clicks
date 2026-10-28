package com.clicks.app.feature.menu.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.clicks.app.feature.menu.model.StoreProduct
import com.clicks.app.feature.menu.model.sampleProducts

data class CartItem(
    val product: StoreProduct,
    val quantity: Int = 1
)

data class OrderItem(
    val orderId: String,
    val date: String,
    val total: Double,
    val status: String,
    val itemsSummary: String
)

data class NotificationItem(
    val id: Int,
    val title: String,
    val message: String,
    val time: String,
    var isRead: Boolean = false
)

class StoreViewModel : ViewModel() {
    val favorites = mutableStateListOf<Int>()
    val cartItems = mutableStateListOf<CartItem>()
    val orders = mutableStateListOf(
        OrderItem("ORD-78421", "Oct 24, 2023", 34.50, "Delivered", "Truffle Burger & Fries, Caramel Iced Latte"),
        OrderItem("ORD-78390", "Sep 15, 2023", 26.00, "In Transit", "Wood-Fired Margherita Pizza, Berry Smoothie")
    )

    fun clearSession() {
        favorites.clear()
        cartItems.clear()
        orders.clear()
        orders.addAll(
            listOf(
                OrderItem("ORD-78421", "Oct 24, 2023", 34.50, "Delivered", "Truffle Burger & Fries, Caramel Iced Latte"),
                OrderItem("ORD-78390", "Sep 15, 2023", 26.00, "In Transit", "Wood-Fired Margherita Pizza, Berry Smoothie")
            )
        )
    }

    val notifications = mutableStateListOf(
        NotificationItem(1, "Order Delivered", "Your order #ORD-78421 has been successfully delivered.", "2h ago", false),
        NotificationItem(2, "Special Offer", "Flash sale! Enjoy 40% off on all new summer arrivals.", "1d ago", false),
        NotificationItem(3, "Order Dispatched", "Your order #ORD-78390 is out for delivery.", "2d ago", true)
    )

    val unreadNotificationCount: Int
        get() = notifications.count { !it.isRead }

    fun markAllNotificationsRead() {
        notifications.forEach { it.isRead = true }
    }

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

    fun isInCart(productId: Int): Boolean = cartItems.any { it.product.id == productId }

    fun toggleCart(productId: Int) {
        if (isInCart(productId))
            removeFromCart(productId) else addToCart(productId)
    }

    fun cartQuantityOf(productId: Int): Int = cartItems.firstOrNull { it.product.id == productId }?.quantity ?: 0

    fun addToCart(productId: Int, quantity: Int = 1) {
        val product = sampleProducts.firstOrNull { it.id == productId } ?: return
        val index = cartItems.indexOfFirst { it.product.id == productId }
        if (index >= 0) {
            cartItems[index] = cartItems[index].copy(quantity = cartItems[index].quantity + quantity)
        } else {
            cartItems.add(CartItem(product, quantity))
        }
    }

    fun updateQuantity(productId: Int, delta: Int) {
        val index = cartItems.indexOfFirst { it.product.id == productId }
        if (index < 0) return
        val newQuantity = cartItems[index].quantity + delta
        if (newQuantity <= 0) {
            cartItems.removeAt(index)
        } else {
            cartItems[index] = cartItems[index].copy(quantity = newQuantity)
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
}
