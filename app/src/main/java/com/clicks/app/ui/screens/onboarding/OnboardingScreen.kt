package com.clicks.app.ui.screens.onboarding

import androidx.compose.animation.*
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.clicks.app.ui.theme.ClicksGreenLight
import com.clicks.app.ui.theme.ClicksSplashBg

data class OnboardingStep(
    val title: String,
    val subtitle: String,
    val imageUrl: String
)

@Composable
fun OnboardingScreen(
    onFinished: () -> Unit
) {
    var currentStep by remember { mutableIntStateOf(0) }

    val steps = listOf(
        OnboardingStep(
            title = "Let's meet our summer\ncoffee drinks",
            subtitle = "Discover refreshing iced beverages and seasonal blends crafted to energize your sunny days.",
            imageUrl = "https://images.unsplash.com/photo-1517256064527-09c73fc73e38?auto=format&fit=crop&w=800&q=80"
        ),
        OnboardingStep(
            title = "Discover amazing\nproducts daily",
            subtitle = "Browse through thousands of curated items tailored specifically for your shopping needs and preferences.",
            imageUrl = "https://images.unsplash.com/photo-1483985988355-763728e1935b?auto=format&fit=crop&w=800&q=80"
        ),
        OnboardingStep(
            title = "Fast delivery to your\ndoorstep",
            subtitle = "Experience lightning-fast checkout and secure tracking for all your purchases right from your mobile device.",
            imageUrl = "https://images.unsplash.com/photo-1526304640581-d334cdbbf45e?auto=format&fit=crop&w=800&q=80"
        )
    )

    val step = steps[currentStep]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Fixed Top Bar with Skip
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = onFinished) {
                Text(
                    text = "Skip",
                    color = Color.Gray,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Static Container with Animated Content for Image & Text (Prevents whole page scrolling/moving)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AnimatedContent(
                targetState = currentStep,
                transitionSpec = {
                    (fadeIn(animationSpec = tween(400)) + slideInHorizontally(
                        initialOffsetX = { it / 3 },
                        animationSpec = tween(400)
                    )).togetherWith(
                        fadeOut(animationSpec = tween(300)) + slideOutHorizontally(
                            targetOffsetX = { -it / 3 },
                            animationSpec = tween(300)
                        )
                    )
                },
                label = "onboardingAnimation"
            ) { targetStep ->
                val currentData = steps[targetStep]
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Illustration Area with Abstract Blobs & Shapes
                    Box(
                        modifier = Modifier
                            .size(280.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Background Abstract Blob / Gradient Circle
                        Box(
                            modifier = Modifier
                                .size(260.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(
                                            ClicksSplashBg.copy(alpha = 0.25f),
                                            ClicksSplashBg.copy(alpha = 0.05f),
                                            Color.Transparent
                                        )
                                    )
                                )
                        )

                        // Decorative Floating Abstract Shapes & Dots
                        Canvas(modifier = Modifier.size(280.dp)) {
                            drawCircle(
                                color = ClicksSplashBg.copy(alpha = 0.5f),
                                radius = 8.dp.toPx(),
                                center = androidx.compose.ui.geometry.Offset(x = size.width * 0.15f, y = size.height * 0.25f)
                            )
                            drawCircle(
                                color = ClicksGreenLight.copy(alpha = 0.4f),
                                radius = 10.dp.toPx(),
                                center = androidx.compose.ui.geometry.Offset(x = size.width * 0.85f, y = size.height * 0.75f)
                            )
                        }

                        // High-Quality Open Source Photo from Unsplash/Pixabay
                        AsyncImage(
                            model = currentData.imageUrl,
                            contentDescription = currentData.title,
                            modifier = Modifier
                                .size(240.dp)
                                .clip(RoundedCornerShape(32.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Title
                    Text(
                        text = currentData.title,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                        lineHeight = 34.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp)
                    )

                    // Subtitle
                    Text(
                        text = currentData.subtitle,
                        fontSize = 14.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp),
                        lineHeight = 20.sp
                    )
                }
            }
        }

        // Fixed Bottom Section (Indicator Dots & Button)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(bottom = 24.dp)
        ) {
            // Silky Smooth Animated Page Indicator Dots
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                repeat(3) { index ->
                    val isSelected = currentStep == index
                    val width by animateDpAsState(
                        targetValue = if (isSelected) 32.dp else 8.dp,
                        animationSpec = tween(durationMillis = 300),
                        label = "dotWidth"
                    )
                    val color = if (isSelected) ClicksSplashBg else Color.LightGray

                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .height(8.dp)
                            .width(width)
                            .clip(RoundedCornerShape(4.dp))
                            .background(color)
                    )
                }
            }

            // Next / Get Started Button
            Button(
                onClick = {
                    if (currentStep < 2) {
                        currentStep++
                    } else {
                        onFinished()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ClicksSplashBg
                ),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text(
                    text = if (currentStep == 2) "GET STARTED" else "NEXT",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}
