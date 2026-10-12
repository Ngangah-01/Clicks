package com.clicks.app.ui.screens.products

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Sort
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PersonOutline
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
import com.clicks.app.ui.viewmodel.StoreViewModel

private val ClicksGreen = Color(0xFF07865B)
private val ClicksText = Color(0xFF163E32)
private val ClicksMint = Color(0xFFF0F7F3)
private val ClicksImageBackground = Color(0xFFF3F4F0)
private val ClicksMuted = Color(0xFF789087)
private val HeartRed = Color(0xFFEF4444)
private val StarGold = Color(0xFFFFB800)

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

val productCategories = listOf(
    ProductCategory(
        "Clothes",
        "Stay stylish, always",
        listOf("All", "Men", "Women", "Kids", "Accessories")
    ),
    ProductCategory(
        "Shoes",
        "Find your perfect fit",
        listOf("All", "Sneakers", "Casual", "Formal", "Sports")
    ),
    ProductCategory(
        "Accessories",
        "Complete your look",
        listOf("All", "Sunglasses", "Watches", "Jewelry", "Belts")
    ),
    ProductCategory(
        "Bags",
        "Carry it your way",
        listOf("All", "Handbags", "Backpacks", "Totes", "Travel")
    )
)

val sampleProducts = listOf(
    StoreProduct(
        1, "Classic Hoodie", "Clothes", 32.00, 4.8, 124, "New",
        "https://images.unsplash.com/photo-1556821840-3a63f95609a7?w=700"
    ),
    StoreProduct(
        2, "Oversized T-Shirt", "Clothes", 18.00, 4.6, 98, "Bestseller",
        "https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?w=700"
    ),
    StoreProduct(
        3, "Denim Jacket", "Clothes", 45.00, 4.7, 86, null,
        "https://images.unsplash.com/photo-1576995853123-5a10305d93c0?w=700"
    ),
    StoreProduct(
        4, "Straight Fit Jeans", "Clothes", 38.00, 4.5, 72, null,
        "https://images.unsplash.com/photo-1542272604-787c3835535d?w=700"
    ),
    StoreProduct(
        5, "Everyday Sneakers", "Shoes", 55.00, 4.8, 112, "Popular",
        "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=700"
    ),
    StoreProduct(
        6, "Classic White Shoes", "Shoes", 48.00, 4.6, 74, null,
        "https://images.unsplash.com/photo-1549298916-b41d501d3772?w=700"
    ),
    StoreProduct(
        7, "Minimal Sunglasses", "Accessories", 22.00, 4.7, 61, "New",
        "https://images.unsplash.com/photo-1511499767150-a48a237f0083?w=700"
    ),
    StoreProduct(
        8, "Classic Wristwatch", "Accessories", 75.00, 4.8, 91, null,
        "https://images.unsplash.com/photo-1524805444758-089113d48a6d?w=700"
    ),
    StoreProduct(
        9, "Classic Shoulder Bag", "Bags", 42.00, 4.8, 105, "Bestseller",
        "https://images.unsplash.com/photo-1584917865442-de89df76afd3?w=700"
    ),
    StoreProduct(
        10, "Everyday Tote", "Bags", 28.00, 4.5, 49, null,
        "https://images.unsplash.com/photo-1590874103328-eac38a683ce7?w=700"
    )
)

@Composable
fun CategoryProductsScreen(
    initialCategory: String = "Clothes",
    storeViewModel: StoreViewModel,
    onBackClick: () -> Unit = {},
    onProductClick: (Int) -> Unit = {}
) {
    var selectedCategory by remember {
        mutableStateOf(
            productCategories.firstOrNull {
                it.title.equals(initialCategory, ignoreCase = true)
            }?.title ?: "Clothes"
        )
    }

    var selectedSubcategory by remember { mutableStateOf("All") }
    var searchVisible by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var sortMenuExpanded by remember { mutableStateOf(false) }
    var sortOption by remember { mutableStateOf("Recommended") }

    val category = productCategories.firstOrNull { it.title == selectedCategory } ?: productCategories.first()

    val displayedProducts = remember(
        selectedCategory,
        selectedSubcategory,
        searchQuery,
        sortOption
    ) {
        val filtered = sampleProducts.filter { product ->
            product.category == selectedCategory &&
                product.name.contains(searchQuery, ignoreCase = true)
        }

        when (sortOption) {
            "Price: Low to High" -> filtered.sortedBy { it.price }
            "Price: High to Low" -> filtered.sortedByDescending { it.price }
            "Top Rated" -> filtered.sortedByDescending { it.rating }
            else -> filtered
        }
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
            // Screen heading
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, end = 16.dp, top = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = "Go back",
                        tint = ClicksGreen
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = category.title,
                        fontSize = 29.sp,
                        fontWeight = FontWeight.Bold,
                        color = ClicksText
                    )
                    Text(
                        text = category.subtitle,
                        fontSize = 14.sp,
                        color = ClicksMuted
                    )
                }

                IconButton(onClick = { searchVisible = !searchVisible }) {
                    Icon(
                        Icons.Outlined.Search,
                        contentDescription = "Search products",
                        tint = ClicksGreen
                    )
                }
            }

            // Optional search field
            if (searchVisible) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    placeholder = { Text("Search ${category.title.lowercase()}") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp)
                )
            }

            // Category selector
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
                                Color(0xFFDDEAE3)
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
                        val isFavorite = storeViewModel.isFavorite(product.id)
                        ProductGridCard(
                            product = product,
                            isFavorite = isFavorite,
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
                    tint = if (isFavorite) HeartRed else ClicksText,
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

        Text(
            text = "$ " + "%.2f".format(product.price),
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = ClicksGreen,
            modifier = Modifier.padding(horizontal = 5.dp)
        )

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
