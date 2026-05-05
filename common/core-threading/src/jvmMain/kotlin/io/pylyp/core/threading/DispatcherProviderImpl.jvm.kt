package com.circle.timer.core.threading

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

internal actual val ioDispatcher: CoroutineDispatcher = Dispatchers.IO