package com.circle.timer.core.threading

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.MainCoroutineDispatcher

@Suppress("VariableNaming") //copy of standard names
public interface DispatcherProvider {

    public val Default: CoroutineDispatcher
    public val IO: CoroutineDispatcher
    public val Unconfined: CoroutineDispatcher
    public val Main: MainCoroutineDispatcher

}