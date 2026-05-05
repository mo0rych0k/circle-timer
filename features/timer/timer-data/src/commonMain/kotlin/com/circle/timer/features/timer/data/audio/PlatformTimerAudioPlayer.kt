package com.circle.timer.features.timer.data.audio

import com.circle.timer.features.timer.domain.TimerAudioPlayer

internal expect class PlatformTimerAudioPlayer() : TimerAudioPlayer {
    override fun playInterval(intervalSeconds: Int)
    override fun playCycleComplete()
    override fun stop()
}
