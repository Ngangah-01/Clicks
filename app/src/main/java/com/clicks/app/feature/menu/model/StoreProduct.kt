package com.clicks.app.feature.menu.model

data class StoreProduct(
    val id: Int,
    val name: String,
    val category: String,
    val price: Double,
    val rating: Double,
    val reviewCount: Int,
    val tag: String?,
    val imageUrl: String
)

val sampleProducts = listOf(
    StoreProduct(1, "Truffle Burger & Fries", "Burgers", 14.50, 4.9, 180, "Burgers", "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?auto=format&fit=crop&w=800&q=80"),
    StoreProduct(2, "Wood-Fired Margherita Pizza", "Pizza", 18.00, 4.8, 145, "Pizza", "https://images.unsplash.com/photo-1513104890138-7c749659a591?auto=format&fit=crop&w=800&q=80"),
    StoreProduct(3, "Molten Chocolate Lava Cake", "Desserts", 7.50, 5.0, 210, "Desserts", "https://images.unsplash.com/photo-1606313564200-e75d5e30476c?auto=format&fit=crop&w=800&q=80"),
    StoreProduct(4, "Grilled Salmon Bowl", "Mains", 21.00, 4.9, 90, "Salads", "https://images.unsplash.com/photo-1546069901-ba9599a7e63c?auto=format&fit=crop&w=800&q=80"),
    StoreProduct(5, "Caramel Iced Latte", "Drinks", 4.50, 4.8, 120, "Iced Coffees", "https://images.unsplash.com/photo-1517256064527-09c73fc73e38?auto=format&fit=crop&w=800&q=80"),
    StoreProduct(6, "Matcha Green Tea", "Drinks", 5.00, 4.9, 95, "Teas", "https://images.unsplash.com/photo-1536256263959-770b48d82b0a?auto=format&fit=crop&w=800&q=80"),
    StoreProduct(7, "Berry Smoothie", "Drinks", 5.50, 4.7, 80, "Smoothies", "https://images.unsplash.com/photo-1553530666-ba11a7da3888?auto=format&fit=crop&w=800&q=80"),
    StoreProduct(8, "Butter Croissant", "Bakery", 3.00, 4.8, 140, "Croissants", "https://images.unsplash.com/photo-1555507036-ab1f4038808a?auto=format&fit=crop&w=800&q=80"),
    StoreProduct(9, "Molten Chocolate Lava Cake", "Desserts", 7.50, 5.0, 210, "Desserts", "https://images.unsplash.com/photo-1606313564200-e75d5e30476c?auto=format&fit=crop&w=800&q=80"),
    StoreProduct(10, "Avocado Chicken Sandwich", "Burgers", 12.50, 4.6, 130, "Sandwiches", "https://images.unsplash.com/photo-1528735602780-2552fd46c7af?auto=format&fit=crop&w=800&q=80")
)
