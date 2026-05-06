package com.circle.timer.features.timer.ui.store

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import com.circle.timer.common.core.di.IsolatedKoinContext
import com.circle.timer.features.timer.domain.RuntimeConfig
import com.circle.timer.features.timer.domain.RuntimeCue
import com.circle.timer.features.timer.domain.RuntimePhase
import com.circle.timer.features.timer.domain.RuntimeState
import com.circle.timer.features.timer.domain.TimerAudioPlayer
import com.circle.timer.features.timer.domain.TimerPhase
import com.circle.timer.features.timer.domain.TimerRuntimeEngine
import com.circle.timer.features.timer.domain.TimerSettings
import com.circle.timer.features.timer.domain.TimerSettingsRepository
import com.circle.timer.features.timer.domain.TimerWidgetSnapshot
import com.circle.timer.features.timer.domain.TimerWidgetSnapshotRepository
import com.circle.timer.features.timer.domain.idleTimerWidgetSnapshot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

internal class TimerForegroundService : Service() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var wakeLock: PowerManager.WakeLock? = null
    private var tickerJob: Job? = null
    private var isTimerRunning: Boolean = false
    private var lastPhase: RuntimePhase = RuntimePhase.Active
    private var lastRemainingSeconds: Int = 0
    private var lastSettings: TimerSettings? = null
    private var postedPhase: RuntimePhase? = null
    private var postedRemainingSeconds: Int? = null
    private var postedIsRunning: Boolean? = null

    private val settingsRepository: TimerSettingsRepository by lazy {
        IsolatedKoinContext.koin().get<TimerSettingsRepository>()
    }
    private val snapshotRepository: TimerWidgetSnapshotRepository by lazy {
        IsolatedKoinContext.koin().get<TimerWidgetSnapshotRepository>()
    }
    private val audioPlayer: TimerAudioPlayer by lazy {
        IsolatedKoinContext.koin().get<TimerAudioPlayer>()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand action=${intent?.action} startId=$startId")
        when (intent?.action) {
            ACTION_START -> startRuntime(settingsFromIntent(intent))
            ACTION_START_FROM_NOTIFICATION -> {
                scope.launch {
                    Log.d(TAG, "Starting runtime from notification action")
                    startRuntime(settingsRepository.getTimerSettings())
                }
            }

            ACTION_STOP_TIMER -> stopTimerOnly()
            ACTION_STOP_SERVICE -> stopRuntimeAndService()
            else -> Log.w(TAG, "Received unknown or null action: ${intent?.action}")
        }
        return START_STICKY
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")
        stopRuntimeAndService()
        scope.cancel()
        super.onDestroy()
    }

    private fun startRuntime(settings: TimerSettings) {
        Log.i(
            TAG,
            "startRuntime total=${settings.totalDurationSeconds}s break=${settings.breakDurationSeconds}s intervals=${settings.enabledIntervals}",
        )
        lastSettings = settings
        isTimerRunning = true
        lastPhase = RuntimePhase.Active
        lastRemainingSeconds = settings.totalDurationSeconds
        val config = RuntimeConfig(
            totalDurationSeconds = settings.totalDurationSeconds,
            breakDurationSeconds = settings.breakDurationSeconds,
            enabledIntervals = settings.enabledIntervals,
            countdownLast5TimerEnabled = settings.countdownLast5TimerEnabled,
            countdownLast5BreakEnabled = settings.countdownLast5BreakEnabled,
        )
        val engine = TimerRuntimeEngine(config = config)
        val started = engine.start(System.currentTimeMillis())
        TimerNotificationFactory.ensureChannel(this)
        startForeground(
            TimerNotificationFactory.NOTIFICATION_ID,
            TimerNotificationFactory.build(
                context = this,
                phase = started.phase,
                remainingSeconds = settings.totalDurationSeconds,
                phaseDurationSeconds = settings.totalDurationSeconds,
                isRunning = isTimerRunning,
            ),
        )
        acquireWakeLock()
        persistRunningSnapshot(started, settings)
        tickerJob?.cancel()
        tickerJob = scope.launch {
            while (isActive) {
                val step = engine.step(System.currentTimeMillis())
                playCue(step.cue)
                val duration = durationForPhase(step.state.phase, settings)
                val remaining = ((duration * 1000L - step.state.elapsedMillis).coerceAtLeast(0L) / 1000L).toInt()
                lastPhase = step.state.phase
                lastRemainingSeconds = remaining
                maybeUpdateNotification(step.state.phase, remaining)
                if (step.cue is RuntimeCue.PhaseComplete) {
                    Log.d(TAG, "Phase complete -> ${step.state.phase}")
                    persistRunningSnapshot(step.state, settings)
                }
                delay(120L)
            }
        }
    }

    private fun stopTimerOnly() {
        Log.i(TAG, "stopTimerOnly")
        tickerJob?.cancel()
        tickerJob = null
        isTimerRunning = false
        audioPlayer.stop()
        releaseWakeLock()
        val knownSettings = lastSettings
        lastPhase = RuntimePhase.Active
        lastRemainingSeconds = knownSettings?.totalDurationSeconds ?: lastRemainingSeconds.coerceAtLeast(0)
        refreshForegroundNotification(lastPhase, lastRemainingSeconds)
        scope.launch {
            val settings = knownSettings ?: settingsRepository.getTimerSettings()
            lastSettings = settings
            snapshotRepository.saveSnapshot(
                idleTimerWidgetSnapshot(
                    totalDurationSeconds = settings.totalDurationSeconds,
                    breakDurationSeconds = settings.breakDurationSeconds,
                ),
            )
        }

    }

    private fun stopRuntimeAndService() {
        Log.i(TAG, "stopRuntimeAndService")
        stopTimerOnly()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun playCue(cue: RuntimeCue?) {
        when (cue) {
            is RuntimeCue.IntervalTick -> audioPlayer.playInterval(cue.intervalSeconds)
            is RuntimeCue.CountdownTick -> audioPlayer.playCountdown(
                isBreak = cue.phase == RuntimePhase.Break,
                secondsRemaining = cue.secondsRemaining,
            )

            RuntimeCue.PhaseComplete -> audioPlayer.playCycleComplete()
            null -> Unit
        }
    }

    private fun maybeUpdateNotification(phase: RuntimePhase, remainingSeconds: Int) {
        if (postedPhase == phase && postedRemainingSeconds == remainingSeconds && postedIsRunning == isTimerRunning) {
            return
        }
        updateNotification(phase, remainingSeconds)
    }

    private fun updateNotification(phase: RuntimePhase, remainingSeconds: Int) {
        Log.v(TAG, "updateNotification phase=$phase remaining=${remainingSeconds}s")
        val phaseDuration = resolvePhaseDuration(phase, remainingSeconds)
        val manager = getSystemService(NOTIFICATION_SERVICE) as android.app.NotificationManager
        manager.notify(
            TimerNotificationFactory.NOTIFICATION_ID,
            TimerNotificationFactory.build(
                context = this,
                phase = phase,
                remainingSeconds = remainingSeconds,
                phaseDurationSeconds = phaseDuration,
                isRunning = isTimerRunning,
            ),
        )
        postedPhase = phase
        postedRemainingSeconds = remainingSeconds
        postedIsRunning = isTimerRunning
    }

    private fun refreshForegroundNotification(phase: RuntimePhase, remainingSeconds: Int) {
        val phaseDuration = resolvePhaseDuration(phase, remainingSeconds)
        startForeground(
            TimerNotificationFactory.NOTIFICATION_ID,
            TimerNotificationFactory.build(
                context = this,
                phase = phase,
                remainingSeconds = remainingSeconds,
                phaseDurationSeconds = phaseDuration,
                isRunning = isTimerRunning,
            ),
        )
        postedPhase = phase
        postedRemainingSeconds = remainingSeconds
        postedIsRunning = isTimerRunning
    }

    private fun persistRunningSnapshot(state: RuntimeState, settings: TimerSettings) {
        scope.launch {
            snapshotRepository.saveSnapshot(
                TimerWidgetSnapshot(
                    isRunning = state.isRunning,
                    phase = when (state.phase) {
                        RuntimePhase.Active -> TimerPhase.Active
                        RuntimePhase.Break -> TimerPhase.Break
                    },
                    phaseStartedEpochMillis = state.phaseStartedEpochMillis,
                    totalDurationSeconds = settings.totalDurationSeconds,
                    breakDurationSeconds = settings.breakDurationSeconds,
                ),
            )
        }
    }

    private fun settingsFromIntent(intent: Intent): TimerSettings = TimerSettings(
        totalDurationSeconds = intent.getIntExtra(EXTRA_TOTAL_DURATION, 60),
        breakDurationSeconds = intent.getIntExtra(EXTRA_BREAK_DURATION, 0),
        enabledIntervals = intent.getIntArrayExtra(EXTRA_INTERVALS)?.toSet() ?: emptySet(),
        countdownLast5TimerEnabled = intent.getBooleanExtra(EXTRA_COUNTDOWN_ACTIVE_ENABLED, true),
        countdownLast5BreakEnabled = intent.getBooleanExtra(EXTRA_COUNTDOWN_BREAK_ENABLED, true),
    )

    private fun durationForPhase(phase: RuntimePhase, settings: TimerSettings): Int = when (phase) {
        RuntimePhase.Active -> settings.totalDurationSeconds
        RuntimePhase.Break -> settings.breakDurationSeconds
    }

    private fun resolvePhaseDuration(phase: RuntimePhase, remainingSeconds: Int): Int {
        val settings = lastSettings
        if (settings == null) return remainingSeconds.coerceAtLeast(1)
        return durationForPhase(phase, settings).coerceAtLeast(1)
    }

    private fun acquireWakeLock() {
        if (wakeLock?.isHeld == true) return
        val pm = getSystemService(POWER_SERVICE) as PowerManager
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "circle-timer:runtime").apply {
            setReferenceCounted(false)
            acquire(10 * 60 * 1000L)
        }
        Log.d(TAG, "WakeLock acquired")
    }

    private fun releaseWakeLock() {
        wakeLock?.let { if (it.isHeld) it.release() }
        wakeLock = null
        Log.d(TAG, "WakeLock released")
    }

    companion object {
        private const val TAG: String = "TimerForegroundService"
        const val ACTION_START: String = "com.circle.timer.action.START_FOREGROUND_TIMER"
        const val ACTION_START_FROM_NOTIFICATION: String =
            "com.circle.timer.action.START_FOREGROUND_TIMER_FROM_NOTIFICATION"
        const val ACTION_STOP_TIMER: String = "com.circle.timer.action.STOP_FOREGROUND_TIMER"
        const val ACTION_STOP_SERVICE: String = "com.circle.timer.action.STOP_FOREGROUND_SERVICE"
        const val EXTRA_TOTAL_DURATION: String = "extra.total_duration"
        const val EXTRA_BREAK_DURATION: String = "extra.break_duration"
        const val EXTRA_INTERVALS: String = "extra.intervals"
        const val EXTRA_COUNTDOWN_ACTIVE_ENABLED: String = "extra.countdown_active_enabled"
        const val EXTRA_COUNTDOWN_BREAK_ENABLED: String = "extra.countdown_break_enabled"
    }
}
