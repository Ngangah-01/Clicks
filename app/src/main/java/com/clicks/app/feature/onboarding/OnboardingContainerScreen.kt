package com.clicks.app.feature.onboarding

import androidx.compose.runtime.Composable

@Composable
fun OnboardingContainerScreen(
    onFinished: () -> Unit
) {
    OnboardingScreen(onFinished = onFinished)
}
