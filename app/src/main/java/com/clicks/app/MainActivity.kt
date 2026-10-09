package com.clicks.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
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
                NavGraph()
            }
        }
    }
}
