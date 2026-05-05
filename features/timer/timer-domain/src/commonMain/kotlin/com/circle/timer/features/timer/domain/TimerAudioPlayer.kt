package com.circle.timer.features.timer.domain

public interface TimerAudioPlayer {
    public fun playInterval(intervalSeconds: Int)
    public fun playCycleComplete()
    public fun stop()
}
