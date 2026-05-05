package com.circle.timer.features.timer.ui.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.circle.timer.features.timer.domain.TimerAudioPlayer
import com.circle.timer.features.timer.domain.TimerSettings
import com.circle.timer.features.timer.domain.TimerSettingsRepository
import com.circle.timer.features.timer.domain.allowedIntervalsForDuration
import com.circle.timer.features.timer.domain.normalizeBreakDuration
import com.circle.timer.features.timer.domain.normalizeDuration
import com.circle.timer.features.timer.domain.normalizeIntervals
import com.circle.timer.features.timer.domain.tickerSoundResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val TICK_MILLIS = 16L

internal class TimerStoreFactory(
    private val storeFactory: StoreFactory,
    private val repository: TimerSettingsRepository,
    private val audioPlayer: TimerAudioPlayer,
) {
    fun create(): TimerStore =
        object : TimerStore,
            Store<TimerStore.Intent, TimerStore.State, Nothing> by storeFactory.create(
                name = "TimerStore",
                initialState = TimerStore.State(),
                bootstrapper = BootstrapperImpl(),
                executorFactory = ::ExecutorImpl,
                reducer = ReducerImpl,
            ) {}

    private sealed interface Action {
        data class Loaded(val settings: TimerSettings) : Action
    }

    private sealed interface Msg {
        data class SetAppliedSettings(val settings: TimerSettings) : Msg
        data class SetDraftSettings(val settings: TimerSettings) : Msg
        data class SetRunning(val value: Boolean) : Msg
        data class SetTimerVisuals(
            val elapsedMillis: Long,
            val progress: Float,
            val phase: TimerStore.TimerPhase,
        ) : Msg

        data class SetShowEditor(val value: Boolean) : Msg
    }

    private inner class BootstrapperImpl : CoroutineBootstrapper<Action>() {
        override fun invoke() {
            scope.launch {
                dispatch(Action.Loaded(repository.getTimerSettings()))
            }
        }
    }

    private inner class ExecutorImpl : CoroutineExecutor<TimerStore.Intent, Action, TimerStore.State, Msg, Nothing>() {
        private var tickerJob: Job? = null
        private var elapsedMillis: Long = 0L

        override fun executeAction(action: Action) {
            when (action) {
                is Action.Loaded -> {
                    val normalized = normalizeSettings(action.settings)
                    dispatch(
                        Msg.SetAppliedSettings(normalized),
                    )
                    dispatch(Msg.SetDraftSettings(normalized))
                }
            }
        }

        override fun executeIntent(intent: TimerStore.Intent) {
            when (intent) {
                TimerStore.Intent.ToggleRun -> onToggleRun()
                TimerStore.Intent.Stop -> stopAndReset()
                TimerStore.Intent.OpenEditor -> openEditor()
                TimerStore.Intent.CloseEditor -> closeEditor()
                is TimerStore.Intent.UpdateDuration -> updateDuration(intent.seconds)
                is TimerStore.Intent.ToggleInterval -> toggleInterval(intent.seconds)
                is TimerStore.Intent.UpdateBreakDuration -> updateBreakDuration(intent.seconds)
                TimerStore.Intent.SaveSettings -> saveSettings()
            }
        }

        private fun onToggleRun() {
            if (state().isRunning) {
                stopAndReset()
                return
            }
            elapsedMillis = 0L
            dispatch(
                Msg.SetTimerVisuals(
                    elapsedMillis = 0L,
                    progress = 0f,
                    phase = TimerStore.TimerPhase.Active,
                ),
            )
            dispatch(Msg.SetRunning(true))
            // Play an immediate start cue.
            audioPlayer.playInterval(intervalSeconds = 1)
            startTicker()
        }

        private fun startTicker() {
            tickerJob?.cancel()
            tickerJob = scope.launch {
                while (true) {
                    delay(TICK_MILLIS)
                    val previous = elapsedMillis
                    elapsedMillis += TICK_MILLIS
                    val phase = state().phase
                    val phaseDurationSeconds = currentPhaseDurationSeconds(state(), phase)
                    val durationMillis = phaseDurationSeconds * 1000L
                    if (durationMillis <= 0L) continue
                    val sound = tickerSoundResult(
                        previousMillis = previous,
                        elapsedMillis = elapsedMillis,
                        durationMillis = durationMillis,
                        enabledIntervals = if (phase == TimerStore.TimerPhase.Active) {
                            state().appliedSettings.enabledIntervals
                        } else {
                            emptySet()
                        },
                    )
                    when {
                        sound.cycleComplete -> {
                            // Always play finish sound at phase boundaries:
                            // Active -> Break/Active and Break -> Active.
                            audioPlayer.playCycleComplete()
                            elapsedMillis = 0L
                            val nextPhase = if (phase == TimerStore.TimerPhase.Active &&
                                state().appliedSettings.breakDurationSeconds > 0
                            ) {
                                TimerStore.TimerPhase.Break
                            } else {
                                TimerStore.TimerPhase.Active
                            }
                            dispatch(
                                Msg.SetTimerVisuals(
                                    elapsedMillis = elapsedMillis,
                                    progress = 0f,
                                    phase = nextPhase,
                                ),
                            )
                            continue
                        }

                        else -> {
                            val intervalSec = sound.intervalSeconds
                            if (intervalSec != null) {
                                audioPlayer.playInterval(intervalSec)
                            }
                        }
                    }
                    dispatch(
                        Msg.SetTimerVisuals(
                            elapsedMillis = elapsedMillis,
                            progress = (elapsedMillis.toFloat() / durationMillis.toFloat()).coerceIn(0f, 1f),
                            phase = phase,
                        ),
                    )
                }
            }
        }

        private fun stopAndReset() {
            tickerJob?.cancel()
            tickerJob = null
            audioPlayer.stop()
            elapsedMillis = 0L
            dispatch(Msg.SetRunning(false))
            dispatch(
                Msg.SetTimerVisuals(
                    elapsedMillis = 0L,
                    progress = 0f,
                    phase = TimerStore.TimerPhase.Active,
                ),
            )
        }

        private fun updateDuration(seconds: Int) {
            val safeDuration = normalizeDuration(seconds)
            val safeIntervals = normalizeIntervals(
                totalDurationSeconds = safeDuration,
                enabledIntervals = state().draftSettings.enabledIntervals,
            )
            dispatch(
                Msg.SetDraftSettings(
                    state().draftSettings.copy(
                        totalDurationSeconds = safeDuration,
                        enabledIntervals = safeIntervals,
                    ),
                ),
            )
        }

        private fun toggleInterval(seconds: Int) {
            val allowed = allowedIntervalsForDuration(state().draftSettings.totalDurationSeconds)
            if (!allowed.contains(seconds)) return
            val current = state().draftSettings.enabledIntervals
            val updated = if (current.contains(seconds)) current - seconds else current + seconds
            dispatch(
                Msg.SetDraftSettings(
                    state().draftSettings.copy(
                        enabledIntervals = normalizeIntervals(
                            totalDurationSeconds = state().draftSettings.totalDurationSeconds,
                            enabledIntervals = updated,
                        ),
                    ),
                ),
            )
        }

        private fun updateBreakDuration(seconds: Int) {
            dispatch(
                Msg.SetDraftSettings(
                    state().draftSettings.copy(
                        breakDurationSeconds = normalizeBreakDuration(seconds),
                    ),
                ),
            )
        }

        private fun openEditor() {
            dispatch(Msg.SetDraftSettings(state().appliedSettings))
            dispatch(Msg.SetShowEditor(true))
        }

        private fun closeEditor() {
            dispatch(Msg.SetDraftSettings(state().appliedSettings))
            dispatch(Msg.SetShowEditor(false))
        }

        private fun saveSettings() {
            val applied = normalizeSettings(state().draftSettings)
            dispatch(Msg.SetAppliedSettings(applied))
            dispatch(Msg.SetDraftSettings(applied))
            if (state().isRunning) {
                stopAndReset()
            }
            dispatch(Msg.SetShowEditor(false))
            scope.launch { repository.saveTimerSettings(applied) }
        }

        private fun normalizeSettings(settings: TimerSettings): TimerSettings {
            val safeDuration = normalizeDuration(settings.totalDurationSeconds)
            return settings.copy(
                totalDurationSeconds = safeDuration,
                enabledIntervals = normalizeIntervals(safeDuration, settings.enabledIntervals),
                breakDurationSeconds = normalizeBreakDuration(settings.breakDurationSeconds),
            )
        }

        private fun currentPhaseDurationSeconds(
            state: TimerStore.State,
            phase: TimerStore.TimerPhase,
        ): Int = when (phase) {
            TimerStore.TimerPhase.Active -> state.appliedSettings.totalDurationSeconds
            TimerStore.TimerPhase.Break -> state.appliedSettings.breakDurationSeconds
        }
    }

    private object ReducerImpl : Reducer<TimerStore.State, Msg> {
        override fun TimerStore.State.reduce(msg: Msg): TimerStore.State =
            when (msg) {
                is Msg.SetAppliedSettings -> copy(appliedSettings = msg.settings)
                is Msg.SetDraftSettings -> copy(draftSettings = msg.settings)
                is Msg.SetRunning -> copy(isRunning = msg.value)
                is Msg.SetTimerVisuals -> copy(
                    elapsedMillis = msg.elapsedMillis,
                    progress = msg.progress,
                    phase = msg.phase,
                )

                is Msg.SetShowEditor -> copy(showEditor = msg.value)
            }
    }
}
