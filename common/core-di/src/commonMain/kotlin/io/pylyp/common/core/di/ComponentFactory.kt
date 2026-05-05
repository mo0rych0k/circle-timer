package com.circle.timer.common.core.di

import org.koin.core.Koin
import org.koin.core.component.KoinComponent

public class ComponentFactory(private val localKoin: Koin) : KoinComponent {
    override fun getKoin(): Koin = localKoin
}