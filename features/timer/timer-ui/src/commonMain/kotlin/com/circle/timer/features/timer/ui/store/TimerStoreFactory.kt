package com.circle.timer.features.timer.ui.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.circle.timer.features.timer.domain.RuntimeConfig
import com.circle.timer.features.timer.domain.RuntimeCue
import com.circle.timer.features.timer.domain.RuntimePhase
import com.circle.timer.features.timer.domain.TimerAudioPlayer
import com.circle.timer.features.timer.domain.TimerPhase
import com.circle.timer.features.timer.domain.TimerRuntimeEngine
import com.circle.timer.features.timer.domain.TimerSettings
import com.circle.timer.features.timer.domain.TimerSettingsRepository
import com.circle.timer.features.timer.domain.TimerWidgetSnapshot
import com.circle.timer.features.timer.domain.TimerWidgetSnapshotRepository
import com.circle.timer.features.timer.domain.allowedIntervalsForDuration
import com.circle.timer.features.timer.domain.idleTimerWidgetSnapshot
import com.circle.timer.features.timer.domain.normalizeBreakDuration
import com.circle.timer.features.timer.domain.normalizeDuration
import com.circle.timer.features.timer.domain.normalizeIntervals
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val TICK_MILLIS = 120L

internal class TimerStoreFactory(
    private val storeFactory: StoreFactory,
    private val repository: TimerSettingsRepository,
    private val snapshotRepository: TimerWidgetSnapshotRepository,
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
        data class Loaded(
            val settings: TimerSettings,
            val snapshot: TimerWidgetSnapshot,
        ) : Action
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
        data class SetSnackbarMessage(val message: String?, val actionLabel: String?) : Msg
        data class SetShowNotificationPermissionSheet(val value: Boolean) : Msg
        data class SetRequestNotificationPermission(val value: Boolean) : Msg
    }

    private inner class BootstrapperImpl : CoroutineBootstrapper<Action>() {
        override fun invoke() {
            scope.launch {
                val settings = repository.getTimerSettings()
                val snapshot = snapshotRepository.getSnapshot(settings)
                dispatch(Action.Loaded(settings = settings, snapshot = snapshot))
            }
        }
    }

    private inner class ExecutorImpl : CoroutineExecutor<TimerStore.Intent, Action, TimerStore.State, Msg, Nothing>() {
        private var runtimeJob: Job? = null
        private var mirrorJob: Job? = null
        private var runtimeEngine: TimerRuntimeEngine? = null
        private var phaseStartedEpochMillis: Long = 0L

        override fun executeAction(action: Action) {
            when (action) {
                is Action.Loaded -> {
                    val normalized = normalizeSettings(action.settings)
                    dispatch(Msg.SetAppliedSettings(normalized))
                    dispatch(Msg.SetDraftSettings(normalized))
                    applySnapshot(action.snapshot, normalized)
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
                is TimerStore.Intent.SetCountdownLast5TimerEnabled -> updateCountdownTimerEnabled(intent.enabled)
                is TimerStore.Intent.SetCountdownLast5BreakEnabled -> updateCountdownBreakEnabled(intent.enabled)
                TimerStore.Intent.DismissNotificationPermissionSheet -> dismissNotificationPermissionSheet()
                TimerStore.Intent.RequestNotificationPermission -> requestNotificationPermission()
                is TimerStore.Intent.NotificationPermissionRequestResult -> onNotificationPermissionResult(intent.granted)
                TimerStore.Intent.ConsumeSnackbar -> dispatch(
                    Msg.SetSnackbarMessage(
                        message = null,
                        actionLabel = null,
                    ),
                )
                TimerStore.Intent.SaveSettings -> saveSettings()
            }
        }

        private fun onToggleRun() {
            if (state().isRunning) {
                stopAndReset()
                return
            }
            val now = nowEpochMillis()
            val settings = state().appliedSettings
            val missingNotificationPermission =
                TimerServiceController.isServiceBackedRuntimeEnabled() && !TimerServiceController.hasNotificationPermission()
            if (missingNotificationPermission) {
                if (!state().appliedSettings.notificationPermissionPromptShown) {
                    dispatch(Msg.SetShowNotificationPermissionSheet(true))
                    return
                }
                dispatch(
                    Msg.SetSnackbarMessage(
                        message = "Timer started. Enable notifications for reliable background updates.",
                        actionLabel = "Allow",
                    ),
                )
            }
            phaseStartedEpochMillis = now
            runtimeEngine = TimerRuntimeEngine(runtimeConfig(settings)).also { it.start(now) }
            dispatch(
                Msg.SetTimerVisuals(
                    elapsedMillis = 0L,
                    progress = 0f,
                    phase = TimerStore.TimerPhase.Active,
                ),
            )
            dispatch(Msg.SetRunning(true))
            dispatch(Msg.SetShowNotificationPermissionSheet(false))
            persistSnapshot(
                TimerWidgetSnapshot(
                    isRunning = true,
                    phase = TimerPhase.Active,
                    phaseStartedEpochMillis = now,
                    totalDurationSeconds = settings.totalDurationSeconds,
                    breakDurationSeconds = settings.breakDurationSeconds,
                ),
            )
            TimerServiceController.start(settings)
            if (TimerServiceController.isServiceBackedRuntimeEnabled()) {
                startMirrorTicker()
            } else {
                audioPlayer.playInterval(intervalSeconds = 1)
                startRuntimeTicker()
            }
        }

        private fun startRuntimeTicker() {
            runtimeJob?.cancel()
            runtimeJob = scope.launch {
                while (true) {
                    delay(TICK_MILLIS)
                    val engine = runtimeEngine ?: continue
                    val step = engine.step(nowEpochMillis())
                    consumeRuntimeStep(step)
                }
            }
        }

        private fun startMirrorTicker() {
            mirrorJob?.cancel()
            mirrorJob = scope.launch {
                while (true) {
                    val snapshot = snapshotRepository.getSnapshot(state().appliedSettings)
                    val visuals = snapshot.resolveVisuals(nowEpochMillis())
                    dispatch(Msg.SetRunning(visuals.isRunning))
                    dispatch(
                        Msg.SetTimerVisuals(
                            elapsedMillis = elapsedFromVisuals(visuals.remainingSeconds, visuals.phase),
                            progress = visuals.progress,
                            phase = visuals.phase.toStorePhase(),
                        ),
                    )
                    if (!visuals.isRunning) break
                    delay(TICK_MILLIS)
                }
            }
        }

        private fun consumeRuntimeStep(step: com.circle.timer.features.timer.domain.RuntimeStepResult) {
            val settings = state().appliedSettings
            val durationMillis = when (step.state.phase) {
                RuntimePhase.Active -> settings.totalDurationSeconds * 1000L
                RuntimePhase.Break -> settings.breakDurationSeconds * 1000L
            }.coerceAtLeast(1L)
            phaseStartedEpochMillis = step.state.phaseStartedEpochMillis
            when (val cue = step.cue) {
                RuntimeCue.PhaseComplete -> {
                    audioPlayer.playCycleComplete()
                    persistSnapshot(
                        TimerWidgetSnapshot(
                            isRunning = true,
                            phase = step.state.phase.toDomainPhase(),
                            phaseStartedEpochMillis = step.state.phaseStartedEpochMillis,
                            totalDurationSeconds = settings.totalDurationSeconds,
                            breakDurationSeconds = settings.breakDurationSeconds,
                        ),
                    )
                }

                is RuntimeCue.IntervalTick -> audioPlayer.playInterval(cue.intervalSeconds)
                is RuntimeCue.CountdownTick -> audioPlayer.playCountdown(
                    isBreak = cue.phase == RuntimePhase.Break,
                    secondsRemaining = cue.secondsRemaining,
                )

                null -> Unit
            }
            dispatch(
                Msg.SetTimerVisuals(
                    elapsedMillis = step.state.elapsedMillis,
                    progress = (step.state.elapsedMillis.toFloat() / durationMillis.toFloat()).coerceIn(0f, 1f),
                    phase = step.state.phase.toStorePhase(),
                ),
            )
        }

        private fun stopAndReset() {
            runtimeJob?.cancel()
            runtimeJob = null
            mirrorJob?.cancel()
            mirrorJob = null
            runtimeEngine = null
            TimerServiceController.stop()
            audioPlayer.stop()
            dispatch(Msg.SetRunning(false))
            dispatch(
                Msg.SetTimerVisuals(
                    elapsedMillis = 0L,
                    progress = 0f,
                    phase = TimerStore.TimerPhase.Active,
                ),
            )
            dispatch(Msg.SetShowNotificationPermissionSheet(false))
            dispatch(Msg.SetRequestNotificationPermission(false))
            persistSnapshot(
                idleTimerWidgetSnapshot(
                    totalDurationSeconds = state().appliedSettings.totalDurationSeconds,
                    breakDurationSeconds = state().appliedSettings.breakDurationSeconds,
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

        private fun updateCountdownTimerEnabled(enabled: Boolean) {
            dispatch(
                Msg.SetDraftSettings(
                    state().draftSettings.copy(
                        countdownLast5TimerEnabled = enabled,
                    ),
                ),
            )
        }

        private fun updateCountdownBreakEnabled(enabled: Boolean) {
            dispatch(
                Msg.SetDraftSettings(
                    state().draftSettings.copy(
                        countdownLast5BreakEnabled = enabled,
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
            TimerServiceController.restart(applied)
            dispatch(Msg.SetShowEditor(false))
            persistSnapshot(
                idleTimerWidgetSnapshot(
                    totalDurationSeconds = applied.totalDurationSeconds,
                    breakDurationSeconds = applied.breakDurationSeconds,
                ),
            )
            scope.launch { repository.saveTimerSettings(applied) }
        }

        private fun dismissNotificationPermissionSheet() {
            dispatch(Msg.SetShowNotificationPermissionSheet(false))
            if (!state().appliedSettings.notificationPermissionPromptShown) {
                persistNotificationPermissionPromptShown()
            }
        }

        private fun requestNotificationPermission() {
            dispatch(Msg.SetRequestNotificationPermission(true))
        }

        private fun onNotificationPermissionResult(granted: Boolean) {
            dispatch(Msg.SetRequestNotificationPermission(false))
            dispatch(Msg.SetShowNotificationPermissionSheet(false))
            if (!state().appliedSettings.notificationPermissionPromptShown) {
                persistNotificationPermissionPromptShown()
            }
            if (granted) {
                dispatch(Msg.SetSnackbarMessage(message = "Notifications enabled", actionLabel = null))
            }
        }

        private fun persistNotificationPermissionPromptShown() {
            val updated = state().appliedSettings.copy(notificationPermissionPromptShown = true)
            dispatch(Msg.SetAppliedSettings(updated))
            scope.launch { repository.saveTimerSettings(updated) }
        }

        private fun applySnapshot(snapshot: TimerWidgetSnapshot, settings: TimerSettings) {
            val visuals = snapshot.resolveVisuals(nowEpochMillis())
            val phase = visuals.phase.toStorePhase()
            dispatch(
                Msg.SetTimerVisuals(
                    elapsedMillis = elapsedFromVisuals(visuals.remainingSeconds, visuals.phase),
                    progress = visuals.progress,
                    phase = phase,
                ),
            )
            dispatch(Msg.SetRunning(visuals.isRunning))
            if (visuals.isRunning && TimerServiceController.isServiceBackedRuntimeEnabled()) {
                startMirrorTicker()
            }
        }

        private fun persistSnapshot(snapshot: TimerWidgetSnapshot) {
            scope.launch { snapshotRepository.saveSnapshot(snapshot) }
        }

        private fun normalizeSettings(settings: TimerSettings): TimerSettings {
            val safeDuration = normalizeDuration(settings.totalDurationSeconds)
            return settings.copy(
                totalDurationSeconds = safeDuration,
                enabledIntervals = normalizeIntervals(safeDuration, settings.enabledIntervals),
                breakDurationSeconds = normalizeBreakDuration(settings.breakDurationSeconds),
            )
        }

        private fun runtimeConfig(settings: TimerSettings): RuntimeConfig = RuntimeConfig(
            totalDurationSeconds = settings.totalDurationSeconds,
            breakDurationSeconds = settings.breakDurationSeconds,
            enabledIntervals = settings.enabledIntervals,
            countdownLast5TimerEnabled = settings.countdownLast5TimerEnabled,
            countdownLast5BreakEnabled = settings.countdownLast5BreakEnabled,
        )

        private fun elapsedFromVisuals(remainingSeconds: Int, phase: TimerPhase): Long {
            val duration = when (phase) {
                TimerPhase.Active -> state().appliedSettings.totalDurationSeconds
                TimerPhase.Break -> state().appliedSettings.breakDurationSeconds
            }
            return ((duration - remainingSeconds).coerceAtLeast(0) * 1000L)
        }

        private fun nowEpochMillis(): Long = currentEpochMillis()
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
                is Msg.SetSnackbarMessage -> copy(
                    snackbarMessage = msg.message,
                    snackbarActionLabel = msg.actionLabel,
                )

                is Msg.SetShowNotificationPermissionSheet -> copy(showNotificationPermissionSheet = msg.value)
                is Msg.SetRequestNotificationPermission -> copy(requestNotificationPermission = msg.value)
            }
    }
}

private fun RuntimePhase.toStorePhase(): TimerStore.TimerPhase = when (this) {
    RuntimePhase.Active -> TimerStore.TimerPhase.Active
    RuntimePhase.Break -> TimerStore.TimerPhase.Break
}

private fun RuntimePhase.toDomainPhase(): TimerPhase = when (this) {
    RuntimePhase.Active -> TimerPhase.Active
    RuntimePhase.Break -> TimerPhase.Break
}

private fun TimerPhase.toStorePhase(): TimerStore.TimerPhase = when (this) {
    TimerPhase.Active -> TimerStore.TimerPhase.Active
    TimerPhase.Break -> TimerStore.TimerPhase.Break
}
