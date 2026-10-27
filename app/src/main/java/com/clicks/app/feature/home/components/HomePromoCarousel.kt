package com.clicks.app.feature.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.clicks.app.lib.theme.ClicksCream
import com.clicks.app.lib.theme.ClicksText
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

data class PromoBanner(
    val tag: String,
    val title: String,
    val image: String
)

val defaultPromoBanners = listOf(
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

@Composable
fun HomePromoCarousel(
    banners: List<PromoBanner> = defaultPromoBanners,
    onDiscoverClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(pageCount = { banners.size })

    LaunchedEffect(pagerState) {
        while (true) {
            delay(4000L.milliseconds)
            val nextPage = (pagerState.currentPage + 1) % banners.size
            pagerState.animateScrollToPage(nextPage)
        }
    }

    Column(
        modifier = modifier
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
                val banner = banners[page]
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
                repeat(banners.size) { iteration ->
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
}
