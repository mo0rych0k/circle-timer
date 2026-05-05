package com.circle.timer.core.threading.test

import com.circle.timer.core.threading.DispatcherProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainCoroutineDispatcher
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher

internal class TestDispatcherProviderImpl : DispatcherProvider {
    override val Default: CoroutineDispatcher = testDispatcher
    override val IO: CoroutineDispatcher = testDispatcher
    override val Unconfined: CoroutineDispatcher = testDispatcher
    override val Main: MainCoroutineDispatcher = Dispatchers.Main

}

public val testDispatcher: TestDispatcher = StandardTestDispatcher()
