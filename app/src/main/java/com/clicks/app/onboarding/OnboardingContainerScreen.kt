package com.clicks.app.onboarding

import androidx.compose.runtime.Composable

@Composable
fun OnboardingContainerScreen(
    onFinished: () -> Unit
) {
    OnboardingScreen(onFinished = onFinished)
}
