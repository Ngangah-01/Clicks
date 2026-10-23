package com.clicks.app.ui.screens.products

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Sort
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.RemoveShoppingCart
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.clicks.app.ui.theme.ClicksGreen
import com.clicks.app.ui.theme.ClicksImageBackground
import com.clicks.app.ui.theme.ClicksMint
import com.clicks.app.ui.theme.ClicksMuted
import com.clicks.app.ui.theme.ClicksText
import com.clicks.app.ui.theme.Error
import com.clicks.app.ui.theme.ProductCategoryBg
import com.clicks.app.ui.theme.StarGold
import com.clicks.app.ui.viewmodel.StoreViewModel

data class ProductCategory(
    val title: String,
    val subtitle: String,
    val subcategories: List<String>
)

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
    StoreProduct(1, "Caramel Iced Latte", "Coffee", 4.50, 4.8, 120, "Iced Coffees", "https://images.unsplash.com/photo-1517256064527-09c73fc73e38?auto=format&fit=crop&w=800&q=80"),
    StoreProduct(2, "Matcha Green Tea", "Teas", 5.00, 4.9, 95, "Teas", "https://images.unsplash.com/photo-1536256263959-770b48d82b0a?auto=format&fit=crop&w=800&q=80"),
    StoreProduct(3, "Strawberry Smoothie", "Smoothies", 5.50, 4.7, 80, "Smoothies", "https://images.unsplash.com/photo-1553530666-ba11a7da3888?auto=format&fit=crop&w=800&q=80"),
    StoreProduct(4, "Fresh Orange Juice", "Beverages", 4.00, 4.6, 60, "Fresh Juice", "https://images.unsplash.com/photo-1613478223719-2ab802602423?auto=format&fit=crop&w=800&q=80"),
    StoreProduct(5, "Butter Croissant", "Bakery", 3.00, 4.8, 140, "Croissants", "https://images.unsplash.com/photo-1555507036-ab1f4038808a?auto=format&fit=crop&w=800&q=80"),
    StoreProduct(6, "Blueberry Muffin", "Bakery", 3.50, 4.7, 110, "Muffins", "https://images.unsplash.com/photo-1607958996333-41aef7caefcc?auto=format&fit=crop&w=800&q=80"),
    StoreProduct(7, "Chocolate Chip Cookie", "Snacks", 2.50, 4.9, 200, "Cookies", "https://images.unsplash.com/photo-1499636136210-6f4ee915583e?auto=format&fit=crop&w=800&q=80"),
    StoreProduct(8, "Chicken Avocado Sandwich", "Snacks", 7.50, 4.8, 155, "Sandwiches", "https://images.unsplash.com/photo-1528735602780-2552fd46c7af?auto=format&fit=crop&w=800&q=80")
)

@Composable
fun CategoryProductsScreen(
    categoryTitle: String = "All Beverages",
    storeViewModel: StoreViewModel,
    onBackClick: () -> Unit = {},
    onProductClick: (Int) -> Unit = {}
) {
    val categories = listOf(
        ProductCategory(
            title = "All Beverages",
            subtitle = "Fresh, iced, and hot drinks",
            subcategories = listOf("All", "Iced Coffees", "Teas", "Smoothies", "Fresh Juice")
        ),
        ProductCategory(
            title = "Snacks & Pastries",
            subtitle = "Baked fresh every morning",
            subcategories = listOf("All", "Croissants", "Muffins", "Cookies", "Sandwiches")
        ),
        ProductCategory(
            title = "Combo Meals",
            subtitle = "Great value meal deals",
            subcategories = listOf("All", "Breakfast Combo", "Lunch Special", "Family Pack")
        )
    )

    val category = categories.find { it.title.equals(categoryTitle, ignoreCase = true) }
        ?: categories.first()

    var selectedSubcategory by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }
    var sortOption by remember { mutableStateOf("Recommended") }
    var sortMenuExpanded by remember { mutableStateOf(false) }

    val allProducts = sampleProducts.filter {
        when (category.title) {
            "All Beverages" -> it.category == "Coffee" || it.category == "Smoothies" || it.category == "Beverages"
            "Snacks & Pastries" -> it.category == "Bakery" || it.category == "Snacks"
            else -> true
        }
    }

    val filteredProducts = allProducts.filter { product ->
        val matchesSubcategory = selectedSubcategory == "All" || product.tag.equals(selectedSubcategory, ignoreCase = true) || product.category.equals(selectedSubcategory, ignoreCase = true)
        val matchesSearch = searchQuery.isBlank() || product.name.contains(searchQuery, ignoreCase = true)
        matchesSubcategory && matchesSearch
    }

    val displayedProducts = when (sortOption) {
        "Price: Low to High" -> filteredProducts.sortedBy { it.price }
        "Price: High to Low" -> filteredProducts.sortedByDescending { it.price }
        "Top Rated" -> filteredProducts.sortedByDescending { it.rating }
        else -> filteredProducts
    }

    Scaffold(
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .statusBarsPadding()
                .background(Color.White)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = "Back",
                        tint = ClicksText
                    )
                }

                Spacer(Modifier.width(8.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = category.title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = ClicksText
                    )
                    Text(
                        text = category.subtitle,
                        fontSize = 12.sp,
                        color = ClicksMuted
                    )
                }

                IconButton(onClick = { /* Cart or profile action */ }) {
                    Icon(
                        imageVector = Icons.Outlined.ShoppingCart,
                        contentDescription = "Cart",
                        tint = ClicksText
                    )
                }
            }

            // Search bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search in ${category.title}...", fontSize = 14.sp) },
                leadingIcon = {
                    Icon(Icons.Outlined.Search, contentDescription = null, tint = ClicksMuted)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 4.dp),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ClicksGreen,
                    unfocusedBorderColor = Color(0xFFE5E7EB)
                )
            )

            // Subcategories horizontal pills
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp, bottom = 20.dp)
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                category.subcategories.forEach { subcategory ->
                    val selected = selectedSubcategory == subcategory

                    Surface(
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable {
                                selectedSubcategory = subcategory
                            },
                        shape = CircleShape,
                        color = if (selected) ClicksGreen else Color.White,
                        border = if (selected) null else
                            androidx.compose.foundation.BorderStroke(
                                1.dp,
                                ProductCategoryBg
                            )
                    ) {
                        Text(
                            text = subcategory,
                            modifier = Modifier.padding(
                                horizontal = 23.dp,
                                vertical = 13.dp
                            ),
                            fontSize = 13.sp,
                            color = if (selected) Color.White else ClicksText,
                            fontWeight = if (selected) {
                                FontWeight.SemiBold
                            } else {
                                FontWeight.Normal
                            }
                        )
                    }
                }
            }

            // Count and sort
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${displayedProducts.size} products",
                    fontSize = 14.sp,
                    color = ClicksMuted
                )

                Box {
                    Row(
                        modifier = Modifier.clickable {
                            sortMenuExpanded = true
                        },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Sort",
                            fontSize = 14.sp,
                            color = ClicksGreen,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(Modifier.width(6.dp))
                        Icon(
                            Icons.AutoMirrored.Outlined.Sort,
                            contentDescription = "Sort products",
                            tint = ClicksGreen,
                            modifier = Modifier.size(19.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = sortMenuExpanded,
                        onDismissRequest = { sortMenuExpanded = false }
                    ) {
                        listOf(
                            "Recommended",
                            "Price: Low to High",
                            "Price: High to Low",
                            "Top Rated"
                        ).forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    sortOption = option
                                    sortMenuExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // Scrollable two-column product grid
            if (displayedProducts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No products found",
                        color = ClicksMuted
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 4.dp,
                        bottom = 20.dp
                    ),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(
                        items = displayedProducts,
                        key = { it.id }
                    ) { product ->
                        ProductGridCard(
                            product = product,
                            isFavorite = storeViewModel.isFavorite(product.id),
                            isInCart = storeViewModel.isInCart(product.id),
                            onCartClick = {
                                storeViewModel.toggleCart(product.id)
                            },
                            onFavoriteClick = {
                                storeViewModel.toggleFavorite(product.id)
                            },
                            onClick = { onProductClick(product.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductGridCard(
    product: StoreProduct,
    isFavorite: Boolean,
    isInCart: Boolean,
    onCartClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(17.dp))
            .background(Color.White)
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(176.dp)
                .clip(RoundedCornerShape(17.dp))
                .background(ClicksImageBackground)
        ) {
            AsyncImage(
                model = product.imageUrl,
                contentDescription = product.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            IconButton(
                onClick = onFavoriteClick,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(5.dp)
                    .size(35.dp)
                    .background(
                        Color.White.copy(alpha = 0.94f),
                        CircleShape
                    )
            ) {
                Icon(
                    imageVector = if (isFavorite) {
                        Icons.Filled.Favorite
                    } else {
                        Icons.Outlined.FavoriteBorder
                    },
                    contentDescription = if (isFavorite) {
                        "Remove from favorites"
                    } else {
                        "Add to favorites"
                    },
                    tint = if (isFavorite) Error else ClicksText,
                    modifier = Modifier.size(21.dp)
                )
            }

            if (product.tag != null) {
                Text(
                    text = product.tag,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                        .clip(CircleShape)
                        .background(ClicksMint)
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                    color = ClicksGreen,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        Text(
            text = product.name,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = ClicksText,
            maxLines = 1,
            modifier = Modifier.padding(horizontal = 5.dp)
        )

        Spacer(Modifier.height(7.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "$ " + "%.2f".format(product.price),
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = ClicksGreen,
                modifier = Modifier.padding(horizontal = 5.dp)
            )
            IconButton(
                onClick = onCartClick,
                modifier = Modifier
                    .size(35.dp)
            ){
                val isInCart = false
                Icon(
                    imageVector = if (isInCart)
                        Icons.Filled.Done
                    else
                        Icons.Filled.Add,
                    contentDescription = if (isInCart)
                        "Remove from cart"
                    else
                        "Add to cart",
                    tint = if (isInCart) Error else Color.White,
                    modifier = Modifier
                        .size(21.dp)
                        .background(
                            ClicksGreen,
                            CircleShape
                        )


                    )
            }
        }

        Spacer(Modifier.height(9.dp))

        Row(
            modifier = Modifier.padding(start = 5.dp, bottom = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Outlined.Star,
                contentDescription = "Rating",
                tint = StarGold,
                modifier = Modifier.size(16.dp)
            )
            Spacer(Modifier.width(5.dp))
            Text(
                text = "${product.rating} (${product.reviewCount})",
                fontSize = 11.sp,
                color = ClicksMuted
            )
        }
    }
}
