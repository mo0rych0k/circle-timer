package com.circle.timer.features.timer.data.audio

import android.media.AudioManager
import android.media.ToneGenerator

internal actual class PlatformTimerAudioPlayer actual constructor() :
    com.circle.timer.features.timer.domain.TimerAudioPlayer {
    private val toneGenerator = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100)

    actual override fun playInterval(intervalSeconds: Int) {
        toneGenerator.stopTone()
        val tone = when (intervalSeconds) {
            1 -> ToneGenerator.TONE_PROP_BEEP
            5, 10, 15, 30 -> ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD
            else -> ToneGenerator.TONE_CDMA_PIP
        }
        toneGenerator.startTone(tone, if (intervalSeconds == 1) 90 else 150)
    }

    actual override fun playCycleComplete() {
        toneGenerator.stopTone()
        toneGenerator.startTone(ToneGenerator.TONE_CDMA_ALERT_NETWORK_LITE, 220)
    }

    actual override fun stop() {
        toneGenerator.stopTone()
    }
}
