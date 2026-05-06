package com.circle.timer.features.timer.ui.store

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.circle.timer.common.core.di.IsolatedKoinContext
import com.circle.timer.features.timer.domain.TimerSettings

internal actual object TimerServiceController {
    actual fun start(settings: TimerSettings) {
        val context = IsolatedKoinContext.koin().get<android.content.Context>()
        context.startForegroundService(
            Intent(context, TimerForegroundService::class.java).apply {
                action = TimerForegroundService.ACTION_START
                putExtra(TimerForegroundService.EXTRA_TOTAL_DURATION, settings.totalDurationSeconds)
                putExtra(TimerForegroundService.EXTRA_BREAK_DURATION, settings.breakDurationSeconds)
                putExtra(
                    TimerForegroundService.EXTRA_INTERVALS,
                    settings.enabledIntervals.sorted().toIntArray(),
                )
                putExtra(
                    TimerForegroundService.EXTRA_COUNTDOWN_ACTIVE_ENABLED,
                    settings.countdownLast5TimerEnabled,
                )
                putExtra(
                    TimerForegroundService.EXTRA_COUNTDOWN_BREAK_ENABLED,
                    settings.countdownLast5BreakEnabled,
                )
            },
        )
    }

    actual fun stop() {
        val context = IsolatedKoinContext.koin().get<android.content.Context>()
        context.startService(
            Intent(context, TimerForegroundService::class.java).apply {
                action = TimerForegroundService.ACTION_STOP_SERVICE
            },
        )
    }

    actual fun restart(settings: TimerSettings) {
        start(settings)
    }

    actual fun isServiceBackedRuntimeEnabled(): Boolean = true

    actual fun hasNotificationPermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
        val context = IsolatedKoinContext.koin().get<android.content.Context>()
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS,
        ) == PackageManager.PERMISSION_GRANTED
    }
}
