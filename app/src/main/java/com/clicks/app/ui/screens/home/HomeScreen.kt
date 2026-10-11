package com.clicks.app.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
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
import kotlinx.coroutines.delay

private val ClicksGreen = Color(0xFF07865B)
private val ClicksGreenDark = Color(0xFF07533F)
private val ClicksGreenInactive = Color(0xFF3B6E5B)
private val ClicksMint = Color(0xFFF0F7F3)
private val ClicksCream = Color(0xFFF7F5E9)
private val ClicksText = Color(0xFF163E32)
private val ClicksMuted = Color(0xFF789087)

private data class ShopCategory(
    val name: String,
    val count: String,
    val imageUrl: String
)

private data class ShopProduct(
    val name: String,
    val price: String,
    val image: String
)

private data class PromoBanner(
    val tag: String,
    val title: String,
    val image: String
)

private val categories = listOf(
    ShopCategory("Clothes", "Explore", "https://images.unsplash.com/photo-1489987707025-afc232f7ea0f?w=400"),
    ShopCategory("Shoes", "Explore", "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=400"),
    ShopCategory("Accessories", "Explore", "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=400"),
    ShopCategory("Bags", "Explore", "https://images.unsplash.com/photo-1584917865442-de89df76afd3?w=400")
)

private val promoBanners = listOf(
    PromoBanner(
        tag = "NEW COLLECTION",
        title = "Thoughtful pieces\nwith a minimal touch",
        image = "https://images.unsplash.com/photo-1483985988355-763728e1935b?w=1200"
    ),
    PromoBanner(
        tag = "SUMMER SALE",
        title = "Up to 50% off on\ntrending seasonal styles",
        image = "https://images.unsplash.com/photo-1441986300917-64674bd600d8?w=1200"
    ),
    PromoBanner(
        tag = "EXCLUSIVE DROPS",
        title = "Handcrafted leather\n& premium accessories",
        image = "https://images.unsplash.com/photo-1490481651871-ab68de25d43d?w=1200"
    ),
    PromoBanner(
        tag = "ESSENTIALS",
        title = "Comfortable basics\nfor daily wear",
        image = "https://images.unsplash.com/photo-1445205170230-053b83016050?w=1200"
    )
)

private val products = listOf(
    ShopProduct(
        "Classic Leather Bag",
        "$2,190",
        "https://images.unsplash.com/photo-1584917865442-de89df76afd3?w=700"
    ),
    ShopProduct(
        "Everyday Sneakers",
        "$1,450",
        "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=700"
    ),
    ShopProduct(
        "Modern Sunglasses",
        "$890",
        "https://images.unsplash.com/photo-1511499767150-a48a237f0083?w=700"
    ),
    ShopProduct(
        "Minimal Shoulder Bag",
        "$1,650",
        "https://images.unsplash.com/photo-1590874103328-eac38a683ce7?w=700"
    )
)

@Composable
fun HomeScreen(
    onFavoritesClick: () -> Unit = {},
    onCartClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onProductClick: (String) -> Unit = {},
    onDiscoverClick: () -> Unit = {}
) {
    var searchText by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf("Home") }
    val favorites = remember { mutableStateListOf<String>() }

    Scaffold(
        containerColor = Color.White,
        bottomBar = {
            ClicksBottomBar(
                selectedTab = selectedTab,
                onTabSelected = { tab ->
                    selectedTab = tab
                    when (tab) {
                        "Favorites" -> onFavoritesClick()
                        "Cart" -> onCartClick()
                        "Profile" -> onProfileClick()
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .background(Color.White)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 22.dp, end = 22.dp, top = 18.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onCartClick) {
                        Icon(
                            imageVector = Icons.Outlined.Menu,
                            contentDescription = "Menu",
                            tint = ClicksGreen
                        )
                    }
                    Text(
                        text = "Clicks",
                        fontSize = 29.sp,
                        fontWeight = FontWeight.Bold,
                        color = ClicksGreen
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {

                    IconButton(onClick = onNotificationClick) {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = "Notifications",
                            tint = ClicksGreen
                        )
                    }
                }
            }

            // Search field
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp, vertical = 18.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(ClicksMint)
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = "Search",
                    tint = ClicksGreen
                )
                Spacer(Modifier.width(12.dp))
                Box(Modifier.weight(1f)) {
                    if (searchText.isEmpty()) {
                        Text(
                            "Search products",
                            color = ClicksMuted,
                            fontSize = 14.sp
                        )
                    }
                    BasicTextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        singleLine = true,
                        textStyle = androidx.compose.ui.text.TextStyle(
                            color = ClicksText,
                            fontSize = 14.sp
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Promotional Hero Carousel
            val pagerState = rememberPagerState(pageCount = { promoBanners.size })

            // Auto-advance carousel every 4 seconds
            LaunchedEffect(pagerState) {
                while (true) {
                    delay(4000L)
                    val nextPage = (pagerState.currentPage + 1) % promoBanners.size
                    pagerState.animateScrollToPage(nextPage)
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .clip(RoundedCornerShape(22.dp))
                ) {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize()
                    ) { page ->
                        val banner = promoBanners[page]
                        Box(modifier = Modifier.fillMaxSize()) {
                            AsyncImage(
                                model = banner.image,
                                contentDescription = banner.tag,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )

                            // Soft overlay
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color(0x55333D32))
                            )

                            Column(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(22.dp)
                            ) {
                                Text(
                                    text = banner.tag,
                                    fontSize = 10.sp,
                                    letterSpacing = 3.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(Modifier.height(12.dp))
                                Text(
                                    text = banner.title,
                                    fontSize = 25.sp,
                                    lineHeight = 31.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White
                                )
                                Spacer(Modifier.height(18.dp))
                                Button(
                                    onClick = onDiscoverClick,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = ClicksCream,
                                        contentColor = ClicksText
                                    ),
                                    shape = RoundedCornerShape(4.dp),
                                    contentPadding = PaddingValues(
                                        horizontal = 24.dp,
                                        vertical = 12.dp
                                    )
                                ) {
                                    Text(
                                        "Discover",
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }

                    // Pager Indicators
                    Row(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(promoBanners.size) { iteration ->
                            val indicatorColor = if (pagerState.currentPage == iteration) Color.White else Color.White.copy(alpha = 0.5f)
                            Box(
                                modifier = Modifier
                                    .size(if (pagerState.currentPage == iteration) 8.dp else 6.dp)
                                    .clip(CircleShape)
                                    .background(indicatorColor)
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(28.dp))

            // Categories heading
            SectionHeading(
                title = "Categories",
                action = "View all",
                modifier = Modifier.padding(horizontal = 22.dp)
            )

            Spacer(Modifier.height(14.dp))

            // Horizontally scrollable categories with high quality images
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 22.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                categories.forEach { category ->
                    Column(
                        modifier = Modifier.width(88.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(76.dp)
                                .clip(CircleShape)
                                .background(ClicksMint),
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                model = category.imageUrl,
                                contentDescription = category.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(70.dp)
                                    .clip(CircleShape)
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = category.name,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = ClicksText,
                            maxLines = 1
                        )
                    }
                }
            }

            Spacer(Modifier.height(28.dp))

            // Popular heading
            SectionHeading(
                title = "Popular",
                action = "View all",
                modifier = Modifier.padding(horizontal = 22.dp)
            )

            Spacer(Modifier.height(14.dp))

            // Two-column product grid
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                products.chunked(2).forEach { rowProducts ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowProducts.forEach { product ->
                            ProductCard(
                                product = product,
                                isFavorite = product.name in favorites,
                                onFavoriteClick = {
                                    if (product.name in favorites) {
                                        favorites.remove(product.name)
                                    } else {
                                        favorites.add(product.name)
                                    }
                                },
                                onClick = { onProductClick(product.name) },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        if (rowProducts.size == 1) {
                            Spacer(Modifier.weight(1f))
                        }
                    }
                }
            }

            Spacer(Modifier.height(22.dp))
        }
    }
}

@Composable
private fun SectionHeading(
    title: String,
    action: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 22.sp,
            fontWeight = FontWeight.Medium,
            color = ClicksText
        )
        Text(
            text = "$action  ›",
            fontSize = 13.sp,
            color = ClicksGreen,
            modifier = Modifier.clickable { }
        )
    }
}

@Composable
private fun ProductCard(
    product: ShopProduct,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF8F8F1))
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
        ) {
            AsyncImage(
                model = product.image,
                contentDescription = product.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            IconButton(
                onClick = onFavoriteClick,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
                    .size(36.dp)
                    .background(
                        Color.White.copy(alpha = 0.88f),
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
                    tint = if (isFavorite) ClicksGreen else ClicksText,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Column(
            modifier = Modifier.padding(
                horizontal = 12.dp,
                vertical = 10.dp
            )
        ) {
            Text(
                text = product.name,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = ClicksText,
                maxLines = 2,
                minLines = 2
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = product.price,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = ClicksGreen
            )
        }
    }
}

@Composable
private fun ClicksBottomBar(
    selectedTab: String,
    onTabSelected: (String) -> Unit
) {
    val tabs = listOf(
        Triple("Home", Icons.Filled.Home, Icons.Outlined.Home),
        Triple("Favorites", Icons.Filled.Favorite, Icons.Outlined.FavoriteBorder),
        Triple("Cart", Icons.Filled.ShoppingCart, Icons.Outlined.ShoppingCart),
        Triple("Profile", Icons.Outlined.Person, Icons.Outlined.PersonOutline)
    )

    Surface(
        color = Color.White,
        shadowElevation = 12.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            tabs.forEach { (label, activeIcon, inactiveIcon) ->
                val selected = selectedTab == label

                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(18.dp))
                        .clickable { onTabSelected(label) }
                        .padding(horizontal = 13.dp, vertical = 5.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(
                                if (selected) ClicksGreen else Color.Transparent
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (selected) activeIcon else inactiveIcon,
                            contentDescription = label,
                            tint = if (selected) Color.White else ClicksGreenInactive,
                            modifier = Modifier.size(23.dp)
                        )
                    }

                    Text(
                        text = label,
                        fontSize = 11.sp,
                        color = if (selected) ClicksGreen else ClicksGreenInactive,
                        fontWeight = if (selected) {
                            FontWeight.SemiBold
                        } else {
                            FontWeight.Medium
                        }
                    )
                }
            }
        }
    }
}
