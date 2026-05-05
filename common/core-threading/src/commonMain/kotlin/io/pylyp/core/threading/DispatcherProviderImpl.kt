package com.circle.timer.core.threading

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainCoroutineDispatcher

internal expect val ioDispatcher: CoroutineDispatcher

internal class DispatcherProviderImpl : DispatcherProvider {
    override val Default: CoroutineDispatcher = Dispatchers.Default
    override val IO: CoroutineDispatcher = ioDispatcher // Використовуємо платформенну реалізацію
    override val Unconfined: CoroutineDispatcher = Dispatchers.Unconfined
    override val Main: MainCoroutineDispatcher = Dispatchers.Main
}