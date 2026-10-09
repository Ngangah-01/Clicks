package com.clicks.app.ui.screens.onboarding

import androidx.compose.runtime.Composable

@Composable
fun OnboardingContainerScreen(
    onFinished: () -> Unit
) {
    OnboardingScreen(onFinished = onFinished)
}
