package com.circle.timer.features.timer.ui.store

import com.circle.timer.features.timer.domain.TimerSettings

internal expect object TimerServiceController {
    fun start(settings: TimerSettings)
    fun stop()
    fun restart(settings: TimerSettings)
    fun isServiceBackedRuntimeEnabled(): Boolean
    fun hasNotificationPermission(): Boolean
}
