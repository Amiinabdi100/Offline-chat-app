package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.AppOnboardingScreen
import com.example.ui.components.MainAppNavigation
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.ChatViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: ChatViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Setup edge-to-edge system bar layout
        enableEdgeToEdge()

        setContent {
            val profileState by viewModel.userProfile.collectAsStateWithLifecycle()
            val themeMode by viewModel.currentThemeMode.collectAsStateWithLifecycle()

            val isDarkTheme = when (themeMode) {
                "DARK" -> true
                "LIGHT" -> false
                else -> isSystemInDarkTheme()
            }

            MyApplicationTheme(darkTheme = isDarkTheme) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    val profile = profileState
                    when {
                        profile == null -> {
                            // Loading state prior to SQLite sync
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                        profile.isFirstLaunch -> {
                            AppOnboardingScreen(
                                viewModel = viewModel,
                                profile = profile,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        else -> {
                            MainAppNavigation(
                                viewModel = viewModel,
                                profile = profile,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
        }
    }
}
