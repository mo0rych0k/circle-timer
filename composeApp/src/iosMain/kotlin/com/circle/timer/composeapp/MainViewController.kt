package com.circle.timer.composeapp

import androidx.compose.ui.window.ComposeUIViewController
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.ApplicationLifecycle
import com.circle.timer.common.core.di.ComponentFactory
import com.circle.timer.common.core.di.IsolatedKoinContext
import com.circle.timer.composeapp.di.createAppRootComponent
import com.circle.timer.composeapp.roating.AppRootComponent
import platform.UIKit.UIViewController

/**
 * iOS shell entry — same wiring as Android [com.circle.timer.sample.MainActivity].
 * Swift: `MainViewControllerKt.MainViewController()` after `KoinHelperKt.bootstrapPlatformKoin()`.
 */
@Suppress("unused")
public fun MainViewController(): UIViewController = ComposeUIViewController {
    App(IosAppRootHolder.appRoot)
}

private object IosAppRootHolder {
    val appRoot: AppRootComponent by lazy {
        val factory: ComponentFactory = IsolatedKoinContext.koin().get()
        factory.createAppRootComponent(
            componentContext = DefaultComponentContext(lifecycle = ApplicationLifecycle()),
        )
    }
}
