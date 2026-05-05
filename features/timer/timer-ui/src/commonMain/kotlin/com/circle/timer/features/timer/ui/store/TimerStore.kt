package com.circle.timer.features.timer.ui.store

import com.arkivanov.mvikotlin.core.store.Store
import com.circle.timer.features.timer.domain.TimerSettings

public interface TimerStore : Store<TimerStore.Intent, TimerStore.State, Nothing> {
    public enum class TimerPhase {
        Active,
        Break,
    }

    public sealed interface Intent {
        public data object ToggleRun : Intent
        public data object Stop : Intent
        public data object OpenEditor : Intent
        public data object CloseEditor : Intent
        public data class UpdateDuration(public val seconds: Int) : Intent
        public data class ToggleInterval(public val seconds: Int) : Intent
        public data class UpdateBreakDuration(public val seconds: Int) : Intent
        public data object SaveSettings : Intent
    }

    public data class State(
        public val isRunning: Boolean = false,
        public val elapsedMillis: Long = 0L,
        public val progress: Float = 0f,
        public val phase: TimerPhase = TimerPhase.Active,
        public val showEditor: Boolean = false,
        public val appliedSettings: TimerSettings = TimerSettings(),
        public val draftSettings: TimerSettings = TimerSettings(),
    )
}
