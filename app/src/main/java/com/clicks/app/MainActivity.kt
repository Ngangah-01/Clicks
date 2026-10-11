package com.clicks.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.clicks.app.ui.navigation.NavGraph
import com.clicks.app.ui.screens.splash.SplashState
import com.clicks.app.ui.screens.splash.SplashViewModel
import com.clicks.app.ui.theme.ClicksTheme

class MainActivity : ComponentActivity() {

    private val splashViewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        splashScreen.setKeepOnScreenCondition {
            splashViewModel.state.value == SplashState.Loading
        }

        enableEdgeToEdge()
        setContent {
            ClicksTheme {
                Surface(Modifier.fillMaxSize()) {
                    Box(Modifier.windowInsetsPadding(WindowInsets.safeDrawing)) {
                        NavGraph()
                    }
                }
            }
        }
    }
}