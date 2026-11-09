package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.service.VmsbHealthSyncBackgroundService
import com.example.ui.MainScreen
import com.example.ui.MainViewModel
import com.example.ui.MainViewModelFactory
import com.example.ui.adaptive.ProvideWindowAdaptiveInfo
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Start continuous background health synchronization service
        VmsbHealthSyncBackgroundService.start(this)
        setContent {
            val darkThemeConfig by viewModel.darkThemeConfig.collectAsState()
            val dynamicColor by viewModel.isDynamicColorEnabled.collectAsState()

            MyApplicationTheme(
                darkThemeConfig = darkThemeConfig,
                dynamicColor = dynamicColor,
                onSetDarkThemeConfig = { viewModel.setDarkThemeConfig(it) },
                onToggleDarkTheme = { viewModel.toggleDarkTheme() },
                onSetDynamicColor = { viewModel.setDynamicColor(it) },
                onToggleDynamicColor = { viewModel.toggleDynamicColor() }
            ) {
                ProvideWindowAdaptiveInfo {
                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        MainScreen(
                            viewModel = viewModel
                        )
                    }
                }
            }
        }
    }
}
