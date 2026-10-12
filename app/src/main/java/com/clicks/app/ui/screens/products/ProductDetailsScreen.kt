package com.clicks.app.ui.screens.products

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
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
private val ClicksMuted = Color(0xFF789087)

@Composable
fun ProductDetailsScreen(
    productId: Int = 1,
    storeViewModel: StoreViewModel,
    onBackClick: () -> Unit = {},
    onCartClick: () -> Unit = {},
    onAddToCartClick: () -> Unit = {},
    onSimilarProductClick: (Int) -> Unit = {}
) {
    val product = sampleProducts.firstOrNull { it.id == productId } ?: sampleProducts.first()

    var selectedSize by remember { mutableStateOf("M") }
    val sizes = listOf("S", "M", "L", "XL")
    val isFavorite = storeViewModel.isFavorite(productId)
    val cartCount = storeViewModel.cartItemCount

    Scaffold(
        containerColor = Color.White,
        bottomBar = {
            Surface(
                color = Color.White,
                shadowElevation = 16.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Price",
                            fontSize = 12.sp,
                            color = ClicksMuted
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = "$ " + "%.2f".format(product.price),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = ClicksText
                        )
                    }

                    Button(
                        onClick = {
                            storeViewModel.addToCart(productId)
                            onAddToCartClick()
                        },
                        modifier = Modifier
                            .height(52.dp)
                            .width(180.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ClicksText,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ShoppingCart,
                            contentDescription = "Cart",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Add to Cart",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .background(Color.White)
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = "Back",
                        tint = ClicksText
                    )
                }

                Text(
                    text = "Details",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = ClicksText
                )

                // Cart Icon with red badge dot
                Box(
                    modifier = Modifier.clickable { onCartClick() }
                ) {
                    IconButton(onClick = onCartClick) {
                        Icon(
                            imageVector = Icons.Outlined.ShoppingCart,
                            contentDescription = "Cart",
                            tint = ClicksText
                        )
                    }
                    if (cartCount > 0) {
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

            Spacer(Modifier.height(16.dp))

            // Product Image Banner with Favorite Icon
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFFF5F6F2))
            ) {
                AsyncImage(
                    model = product.imageUrl,
                    contentDescription = product.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                IconButton(
                    onClick = { storeViewModel.toggleFavorite(productId) },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .size(42.dp)
                        .background(Color.White, CircleShape)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) ClicksGreen else ClicksText,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // Product Name & Rating
            Text(
                text = product.name,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = ClicksText
            )

            Spacer(Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Star,
                    contentDescription = "Rating",
                    tint = Color(0xFFFFB800),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "${product.rating}/5",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ClicksText
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "(${product.reviewCount} reviews)",
                    fontSize = 14.sp,
                    color = ClicksMuted
                )
            }

            Spacer(Modifier.height(16.dp))

            // Description
            Text(
                text = "The name says it all, the right size slightly shrugs the body leaving enough room for comfort in the sleeves and waist with premium crafted material.",
                fontSize = 14.sp,
                color = ClicksMuted,
                lineHeight = 22.sp
            )

            Spacer(Modifier.height(24.dp))

            // Choose Size
            Text(
                text = "Choose size",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = ClicksText
            )

            Spacer(Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                sizes.forEach { size ->
                    val selected = selectedSize == size
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (selected) ClicksGreen else Color.White)
                            .border(
                                width = if (selected) 0.dp else 1.dp,
                                color = if (selected) Color.Transparent else Color(0xFFE2E8F0),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { selectedSize = size },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = size,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (selected) Color.White else ClicksText
                        )
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            // Similar To Section
            Text(
                text = "Similar To",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = ClicksText
            )

            Spacer(Modifier.height(14.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                sampleProducts.filter { it.id != product.id }.take(4).forEach { similar ->
                    Column(
                        modifier = Modifier
                            .width(140.dp)
                            .clickable { onSimilarProductClick(similar.id) }
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFFF5F6F2))
                        ) {
                            AsyncImage(
                                model = similar.imageUrl,
                                contentDescription = similar.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = similar.name,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = ClicksText,
                            maxLines = 1
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "$ " + "%.2f".format(similar.price),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = ClicksGreen
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}
