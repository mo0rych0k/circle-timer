package com.circle.timer.features.timer.data.audio

import platform.AudioToolbox.AudioServicesPlaySystemSound

internal actual class PlatformTimerAudioPlayer actual constructor() :
    com.circle.timer.features.timer.domain.TimerAudioPlayer {
    actual override fun playInterval(intervalSeconds: Int) {
        val soundId: UInt = when (intervalSeconds) {
            1 -> 1103u
            5, 10, 15, 30 -> 1016u
            else -> 1057u
        }
        AudioServicesPlaySystemSound(soundId)
    }

    actual override fun playCycleComplete() {
        AudioServicesPlaySystemSound(1005u)
    }

    actual override fun playCountdown(isBreak: Boolean, secondsRemaining: Int) {
        AudioServicesPlaySystemSound(if (isBreak) 1025u else 1123u)
    }

    actual override fun stop() = Unit
}
