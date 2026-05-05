package com.circle.timer.composeapp

import androidx.compose.runtime.Composable
import com.circle.timer.common.uikit.AppTheme
import com.circle.timer.composeapp.roating.AppRootComponent
import com.circle.timer.composeapp.roating.AppRootMain

@Composable
public fun App(rootComponent: AppRootComponent) {
    AppTheme { AppRootMain(rootComponent = rootComponent) }
}