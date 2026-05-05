package com.circle.timer.features.timer.data.audio

import com.circle.timer.features.timer.domain.TimerAudioPlayer

/**
 * Stops any in-flight tone before starting the next one so tones never overlap.
 * [TimerStore] already enforces at most one logical event per tick; this handles hardware overlap.
 */
internal class CoalescingTimerAudioPlayer(
    private val delegate: TimerAudioPlayer,
) : TimerAudioPlayer {
    override fun playInterval(intervalSeconds: Int) {
        delegate.stop()
        delegate.playInterval(intervalSeconds)
    }

    override fun playCycleComplete() {
        delegate.stop()
        delegate.playCycleComplete()
    }

    override fun stop() {
        delegate.stop()
    }
}
