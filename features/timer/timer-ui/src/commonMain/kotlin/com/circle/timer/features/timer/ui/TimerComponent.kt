package com.circle.timer.features.timer.ui

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.circle.timer.common.core.di.ComponentFactory
import com.circle.timer.core.navigation.asValue
import com.circle.timer.features.timer.ui.di.createTimerStore
import com.circle.timer.features.timer.ui.store.TimerStore

public interface TimerComponent {
    public val state: Value<TimerStore.State>
    public fun onIntent(intent: TimerStore.Intent)
}

internal class DefaultTimerComponent(
    componentContext: ComponentContext,
    componentFactory: ComponentFactory,
) : ComponentContext by componentContext, TimerComponent {

    private val store: TimerStore = instanceKeeper.getStore { componentFactory.createTimerStore() }

    override val state: Value<TimerStore.State> = store.asValue()

    override fun onIntent(intent: TimerStore.Intent) {
        store.accept(intent)
    }
}
