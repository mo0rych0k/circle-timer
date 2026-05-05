package com.circle.timer.features.timer.data.audio

import com.circle.timer.features.timer.domain.TimerAudioPlayer
import kotlin.test.Test
import kotlin.test.assertEquals

class CoalescingTimerAudioPlayerTest {

    private class RecordingDelegate : TimerAudioPlayer {
        val events = mutableListOf<String>()
        override fun playInterval(intervalSeconds: Int) {
            events.add("playInterval($intervalSeconds)")
        }

        override fun playCycleComplete() {
            events.add("playCycleComplete")
        }

        override fun stop() {
            events.add("stop")
        }
    }

    @Test
    fun stops_before_each_interval_play() {
        val delegate = RecordingDelegate()
        val player = CoalescingTimerAudioPlayer(delegate)
        player.playInterval(5)
        assertEquals(listOf("stop", "playInterval(5)"), delegate.events)
    }

    @Test
    fun stops_before_cycle_complete() {
        val delegate = RecordingDelegate()
        val player = CoalescingTimerAudioPlayer(delegate)
        player.playCycleComplete()
        assertEquals(listOf("stop", "playCycleComplete"), delegate.events)
    }

    @Test
    fun interval_plays_after_cycle_complete() {
        val delegate = RecordingDelegate()
        val player = CoalescingTimerAudioPlayer(delegate)
        player.playCycleComplete()
        player.playInterval(1)
        assertEquals(
            listOf("stop", "playCycleComplete", "stop", "playInterval(1)"),
            delegate.events,
        )
    }

    @Test
    fun stop_forwards_to_delegate() {
        val delegate = RecordingDelegate()
        val player = CoalescingTimerAudioPlayer(delegate)
        player.stop()
        assertEquals(listOf("stop"), delegate.events)
    }
}
