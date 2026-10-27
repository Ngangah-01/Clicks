package com.clicks.app.feature.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.automirrored.outlined.Logout
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.clicks.app.R
import com.clicks.app.feature.profile.ProfileViewModel
import com.clicks.app.feature.profile.displayName
import com.clicks.app.feature.profile.initials
import com.clicks.app.lib.viewmodel.StoreViewModel
import com.clicks.app.feature.menu.sampleProducts
import com.clicks.app.lib.components.ClicksBottomBar
import com.clicks.app.lib.components.ClicksDrawerSheet
import com.clicks.app.lib.components.ProductCard
import com.clicks.app.lib.components.SearchBar
import com.clicks.app.lib.theme.ClicksCream
import com.clicks.app.lib.theme.ClicksGreen
import com.clicks.app.lib.theme.ClicksGreenInactive
import com.clicks.app.lib.theme.ClicksMint
import com.clicks.app.lib.theme.ClicksMuted
import com.clicks.app.lib.theme.ClicksText
import kotlin.time.Duration.Companion.milliseconds

private data class ShopCategory(
    val name: String,
    val count: String,
    val imageUrl: String
)

private data class PromoBanner(
    val tag: String,
    val title: String,
    val image: String
)

private val categories = listOf(
    ShopCategory("Burgers", "Explore", "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=400"),
    ShopCategory("Pizza", "Explore", "https://images.unsplash.com/photo-1513104890138-7c749659a591?w=400"),
    ShopCategory("Drinks", "Explore", "https://images.unsplash.com/photo-1544145945-f90425340c7e?w=400"),
    ShopCategory("Desserts", "Explore", "https://images.unsplash.com/photo-1551024709-8f23befc6f87?w=400")
)

private val promoBanners = listOf(
    PromoBanner(
        tag = "CHEF'S SPECIALS",
        title = "Fresh gourmet meals\ndelivered hot & fast",
        image = "https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=1200"
    ),
    PromoBanner(
        tag = "COMBO DEALS",
        title = "Save big on our\npopular family meal packs",
        image = "https://images.unsplash.com/photo-1555396273-367ea4eb4db5?w=1200"
    ),
    PromoBanner(
        tag = "ARTISAN BAKERY",
        title = "Freshly baked pastries\n& warm crusty breads",
        image = "https://images.unsplash.com/photo-1509440159596-0249088772ff?w=1200"
    ),
    PromoBanner(
        tag = "HAPPY HOUR",
        title = "Refreshing smoothies\n& iced craft coffees",
        image = "https://images.unsplash.com/photo-1551024709-8f23befc6f87?w=1200"
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    storeViewModel: StoreViewModel,
    profileViewModel: ProfileViewModel,
    onFavoritesClick: () -> Unit = {},
    onCartClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onOrdersClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    onProductClick: (Int) -> Unit = {},
    onCategoryClick: (String) -> Unit = {},
    onViewAllClick: () -> Unit = {},
    onDiscoverClick: () -> Unit = {}
) {
    var searchText by remember { mutableStateOf("") }
    var isSearchOpen by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf("Home") }

    LaunchedEffect(profileViewModel.uiState.isSessionExpired) {
        if (profileViewModel.uiState.isSessionExpired) {
            onLogoutClick()
        }
    }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var showNotificationsSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val unreadNotifications = storeViewModel.unreadNotificationCount

    if (showNotificationsSheet) {
        ModalBottomSheet(
            onDismissRequest = { showNotificationsSheet = false },
            sheetState = sheetState,
            containerColor = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Notifications",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = ClicksText
                    )
                    TextButton(
                        onClick = { storeViewModel.markAllNotificationsRead() }
                    ) {
                        Text(
                            text = "Mark all as read",
                            fontSize = 13.sp,
                            color = ClicksGreen,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                storeViewModel.notifications.forEach { notification ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (notification.isRead) Color(0xFFF9FAFB) else ClicksMint
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(if (notification.isRead) Color(0xFFE5E7EB) else ClicksGreen),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Notifications,
                                    contentDescription = null,
                                    tint = if (notification.isRead) ClicksMuted else Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = notification.title,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ClicksText
                                    )
                                    Text(
                                        text = notification.time,
                                        fontSize = 11.sp,
                                        color = ClicksMuted
                                    )
                                }
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = notification.message,
                                    fontSize = 12.sp,
                                    color = ClicksMuted,
                                    lineHeight = 16.sp
                                )
                            }

                            if (!notification.isRead) {
                                Spacer(Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFEF4444))
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(36.dp))
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ClicksDrawerSheet(
                profileViewModel = profileViewModel,
                onProfileClick = onProfileClick,
                onOrdersClick = onOrdersClick,
                onFavoritesClick = onFavoritesClick,
                onCartClick = onCartClick,
                onLogoutClick = onLogoutClick,
                onCloseDrawer = { scope.launch { drawerState.close() } }
            )
        }
    ) {
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
                    .statusBarsPadding()
                    .verticalScroll(rememberScrollState())
                    .background(Color.White)
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 22.dp, end = 22.dp, top = 18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                imageVector = Icons.Outlined.Menu,
                                contentDescription = "Menu",
                                tint = ClicksGreen
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.image2),
                            contentDescription = "Clicks Logo",
                            modifier = Modifier
                                .size(35.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "Clicks",
                            fontSize = 35.sp,
                            fontWeight = FontWeight.W800,
                            color = ClicksGreen,
                            letterSpacing = 0.5.sp
                        )
                    }

                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Search Toggle Icon Button
                        IconButton(onClick = {
                            isSearchOpen = !isSearchOpen
                            if (!isSearchOpen) searchText = ""
                        }) {
                            Icon(
                                imageVector = Icons.Outlined.Search,
                                contentDescription = "Search",
                                tint = ClicksGreen
                            )
                        }

                        // Notification bell icon with unread badge in header
                        Box(
                            modifier = Modifier.clickable { showNotificationsSheet = true }
                        ) {
                            IconButton(onClick = { showNotificationsSheet = true }) {
                                Icon(
                                    imageVector = Icons.Outlined.Notifications,
                                    contentDescription = "Notifications",
                                    tint = ClicksGreen
                                )
                            }
                            if (unreadNotifications > 0) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .offset(x = (-4).dp, y = 4.dp)
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFEF4444))
                                )
                            }
                        }
                    }
                }

                AnimatedVisibility(visible = isSearchOpen) {
                    SearchBar(
                        query = searchText,
                        onQueryChange = { searchText = it },
                        placeholder = "Search products..."
                    )
                }

                Spacer(Modifier.height(14.dp))

                SectionHeading(
                    title = "Categories",
                    action = "View all",
                    onActionClick = onViewAllClick,
                    modifier = Modifier.padding(horizontal = 22.dp)
                )

                Spacer(Modifier.height(14.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 22.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    categories.forEach { category ->
                        Column(
                            modifier = Modifier
                                .width(88.dp)
                                .clickable { onCategoryClick(category.name) },
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

                Spacer(Modifier.height(14.dp))

                SectionHeading(
                    title = "Combo Deals",
                    action = "View all",
                    onActionClick = onViewAllClick,
                    modifier = Modifier.padding(horizontal = 22.dp)
                )

                Spacer(Modifier.height(14.dp))

                val pagerState = rememberPagerState(pageCount = { promoBanners.size })

                LaunchedEffect(pagerState) {
                    while (true) {
                        delay(4000L.milliseconds)
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

                SectionHeading(
                    title = "Popular",
                    action = "View all",
                    onActionClick = onViewAllClick,
                    modifier = Modifier.padding(horizontal = 22.dp)
                )

                Spacer(Modifier.height(14.dp))

                Column(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    val featuredProducts = sampleProducts.take(4)
                    featuredProducts.chunked(2).forEach { rowProducts ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            rowProducts.forEach { product ->
                                ProductCard(
                                    product = product,
                                    isFavorite = storeViewModel.isFavorite(product.id),
                                    isInCart = storeViewModel.isInCart(product.id),
                                    onCartClick = { storeViewModel.toggleCart(product.id) },
                                    onFavoriteClick = { storeViewModel.toggleFavorite(product.id) },
                                    onClick = { onProductClick(product.id) },
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
}

@Composable
private fun SectionHeading(
    title: String,
    action: String,
    onActionClick: () -> Unit = {},
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
            fontWeight = FontWeight.Bold,
            color = ClicksText
        )
        Text(
            text = "$action  ›",
            fontSize = 13.sp,
            color = ClicksGreen,
            modifier = Modifier.clickable { onActionClick() }
        )
    }
}
