package com.circle.timer.features.timer.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

@Composable
internal actual fun ScreenResumeEffect(onResume: () -> Unit) {
    LaunchedEffect(Unit) {
        onResume()
    }
}
