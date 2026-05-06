package com.circle.timer.features.timer.data.audio

import java.awt.Toolkit

internal actual class PlatformTimerAudioPlayer actual constructor() :
    com.circle.timer.features.timer.domain.TimerAudioPlayer {
    actual override fun playInterval(intervalSeconds: Int) {
        Toolkit.getDefaultToolkit().beep()
    }

    actual override fun playCycleComplete() {
        repeat(2) { Toolkit.getDefaultToolkit().beep() }
    }

    actual override fun playCountdown(isBreak: Boolean, secondsRemaining: Int) {
        Toolkit.getDefaultToolkit().beep()
    }

    actual override fun stop() = Unit
}
