package com.circle.timer.features.timer.ui.store

import com.circle.timer.features.timer.domain.TimerSettings

internal actual object TimerServiceController {
    actual fun start(settings: TimerSettings) = Unit
    actual fun stop() = Unit
    actual fun restart(settings: TimerSettings) = Unit
    actual fun isServiceBackedRuntimeEnabled(): Boolean = false
    actual fun hasNotificationPermission(): Boolean = true
}
