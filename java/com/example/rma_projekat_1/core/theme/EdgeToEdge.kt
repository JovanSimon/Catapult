package com.example.rma_projekat_1.core.theme

import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext

@Composable
fun EnableEdgeToEdge(
    statusBarColor: Color = Color.Transparent,
    navigationColor: Color = Color.Transparent,
    isDarkTheme: Boolean = isSystemInDarkTheme()
) {
    val context = LocalContext.current
    if (context is ComponentActivity) {
        context.enableEdgeToEdge(
            statusBarColor = statusBarColor,
            navigationBarColor = navigationColor,
            isDarkTheme = isDarkTheme
        )
    }
}

private fun ComponentActivity.enableEdgeToEdge(
    statusBarColor: Color = Color.Transparent,
    navigationBarColor: Color = Color.Transparent,
    isDarkTheme: Boolean,
) {
    this@enableEdgeToEdge.enableEdgeToEdge(
        statusBarStyle = SystemBarStyle.auto(
            lightScrim = statusBarColor.toArgb(),
            darkScrim = statusBarColor.toArgb(),
            detectDarkMode = { isDarkTheme },
        ),
        navigationBarStyle = SystemBarStyle.auto(
            lightScrim = navigationBarColor.toArgb(),
            darkScrim = navigationBarColor.toArgb(),
            detectDarkMode = { isDarkTheme },
        ),
    )
}