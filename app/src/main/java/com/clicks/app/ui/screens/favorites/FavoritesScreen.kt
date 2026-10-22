package com.clicks.app.ui.screens.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
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
import com.clicks.app.ui.screens.products.sampleProducts
import com.clicks.app.ui.theme.BorderColor
import com.clicks.app.ui.theme.ClicksGreen
import com.clicks.app.ui.theme.ClicksImageBackground
import com.clicks.app.ui.theme.ClicksMuted
import com.clicks.app.ui.theme.ClicksText
import com.clicks.app.ui.theme.Error
import com.clicks.app.ui.theme.StarGold
import com.clicks.app.ui.viewmodel.StoreViewModel

@Composable
fun FavoritesScreen(
    storeViewModel: StoreViewModel,
    onBackClick: () -> Unit = {},
    onProductClick: (Int) -> Unit = {}
) {
    val favoriteIds = storeViewModel.favorites
    val favoriteProducts = sampleProducts.filter { it.id in favoriteIds }

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
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
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
                Text(
                    text = "My Favorites (${favoriteProducts.size})",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = ClicksText
                )
            }

            HorizontalDivider(color = BorderColor, thickness = 1.dp)

            if (favoriteProducts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Outlined.FavoriteBorder,
                            contentDescription = "Empty",
                            tint = ClicksMuted,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = "No favorite items yet",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = ClicksText
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "Tap the heart icon on any product to save it here",
                            fontSize = 14.sp,
                            color = ClicksMuted
                        )
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(favoriteProducts, key = { it.id }) { product ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(17.dp))
                                .background(Color.White)
                                .clickable { onProductClick(product.id) }
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
                                    onClick = { storeViewModel.toggleFavorite(product.id) },
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(5.dp)
                                        .size(35.dp)
                                        .background(Color.White.copy(alpha = 0.94f), CircleShape)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Favorite,
                                        contentDescription = "Remove favorite",
                                        tint = Error,
                                        modifier = Modifier.size(21.dp)
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
                                    imageVector = Icons.Outlined.Star,
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
                }
            }
        }
    }
}
