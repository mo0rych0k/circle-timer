package com.circle.timer.composeapp.di

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.circle.timer.common.core.di.ComponentFactory
import com.circle.timer.composeapp.roating.AppRootComponent
import com.circle.timer.composeapp.roating.DefaultAppRootComponent
import org.koin.core.component.get
import org.koin.core.module.Module
import org.koin.dsl.module

public val appNavigationModule: Module = module {
    factory<StoreFactory> { DefaultStoreFactory() }
}

public fun ComponentFactory.createAppRootComponent(
    componentContext: ComponentContext,
): AppRootComponent {
    return DefaultAppRootComponent(
        componentContext = componentContext,
        componentFactory = get(),
    )
}