package com.example.appfunctionsdemo.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            background = ObsidianBg,
            surface = DeepSlate,
            primary = NeonCyan,
            secondary = NeonPurple,
            tertiary = NeonGreen
        ),
        content = content
    )
}
