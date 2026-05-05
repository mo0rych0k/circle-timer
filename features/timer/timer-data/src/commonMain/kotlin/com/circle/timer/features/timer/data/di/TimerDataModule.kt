package com.circle.timer.features.timer.data.di

import com.circle.timer.features.timer.data.TimerSettingsRepositoryImpl
import com.circle.timer.features.timer.data.audio.CoalescingTimerAudioPlayer
import com.circle.timer.features.timer.data.audio.PlatformTimerAudioPlayer
import com.circle.timer.features.timer.domain.TimerAudioPlayer
import com.circle.timer.features.timer.domain.TimerSettingsRepository
import com.russhwolf.settings.Settings
import org.koin.core.module.Module
import org.koin.dsl.module

public val timerDataModule: Module = module {
    single<Settings> { Settings() }
    single<TimerSettingsRepository> { TimerSettingsRepositoryImpl(settings = get()) }
    single<TimerAudioPlayer> { CoalescingTimerAudioPlayer(PlatformTimerAudioPlayer()) }
}
