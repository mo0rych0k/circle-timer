package com.circle.timer.features.timer.data

import com.circle.timer.features.timer.domain.TimerSettings
import com.russhwolf.settings.Settings
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.runBlocking

class TimerSettingsRepositoryImplTest {

    @Test
    fun saves_and_loads_break_duration() = runBlocking {
        val settings = Settings()
        val repository = TimerSettingsRepositoryImpl(settings)

        repository.saveTimerSettings(
            TimerSettings(
                totalDurationSeconds = 30,
                enabledIntervals = setOf(5, 10),
                breakDurationSeconds = 15,
                countdownLast5TimerEnabled = false,
                countdownLast5BreakEnabled = true,
                notificationPermissionPromptShown = true,
            ),
        )

        val loaded = repository.getTimerSettings()
        assertEquals(30, loaded.totalDurationSeconds)
        assertEquals(setOf(5, 10), loaded.enabledIntervals)
        assertEquals(15, loaded.breakDurationSeconds)
        assertEquals(false, loaded.countdownLast5TimerEnabled)
        assertEquals(true, loaded.countdownLast5BreakEnabled)
        assertEquals(true, loaded.notificationPermissionPromptShown)
    }

    @Test
    fun invalid_break_duration_is_normalized_to_none() = runBlocking {
        val settings = Settings().apply {
            putInt("timer.totalDurationSeconds", 60)
            putString("timer.enabledIntervals", "1,5,10")
            putInt("timer.breakDurationSeconds", 17)
            putBoolean("timer.notification.promptShown", false)
        }
        val repository = TimerSettingsRepositoryImpl(settings)

        val loaded = repository.getTimerSettings()
        assertEquals(0, loaded.breakDurationSeconds)
        assertEquals(true, loaded.countdownLast5TimerEnabled)
        assertEquals(true, loaded.countdownLast5BreakEnabled)
        assertEquals(false, loaded.notificationPermissionPromptShown)
    }
}
