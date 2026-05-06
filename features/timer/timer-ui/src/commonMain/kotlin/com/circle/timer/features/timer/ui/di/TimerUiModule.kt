package com.circle.timer.features.timer.ui.di

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.circle.timer.common.core.di.ComponentFactory
import com.circle.timer.features.timer.domain.TimerAudioPlayer
import com.circle.timer.features.timer.domain.TimerSettingsRepository
import com.circle.timer.features.timer.domain.TimerWidgetSnapshotRepository
import com.circle.timer.features.timer.ui.DefaultTimerComponent
import com.circle.timer.features.timer.ui.TimerComponent
import com.circle.timer.features.timer.ui.store.TimerStore
import com.circle.timer.features.timer.ui.store.TimerStoreFactory
import org.koin.core.component.get
import org.koin.core.module.Module
import org.koin.dsl.module

public val timerUiModule: Module = module { }

internal fun ComponentFactory.createTimerStore(): TimerStore {
    return TimerStoreFactory(
        storeFactory = get<StoreFactory>(),
        repository = get<TimerSettingsRepository>(),
        snapshotRepository = get<TimerWidgetSnapshotRepository>(),
        audioPlayer = get<TimerAudioPlayer>(),
    ).create()
}

public fun ComponentFactory.createTimerComponent(
    componentContext: ComponentContext,
): TimerComponent {
    return DefaultTimerComponent(
        componentContext = componentContext,
        componentFactory = this,
    )
}
