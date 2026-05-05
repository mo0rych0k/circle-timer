package com.circle.timer.composeapp

import androidx.compose.ui.window.ComposeUIViewController
import com.circle.timer.composeapp.roating.AppRootComponent
import platform.UIKit.UIViewController

public fun rootViewController(root: AppRootComponent): UIViewController =
    ComposeUIViewController {
        App(root)
    }