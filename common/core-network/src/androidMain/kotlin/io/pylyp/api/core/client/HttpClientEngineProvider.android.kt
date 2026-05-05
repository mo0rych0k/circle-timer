package com.circle.timer.api.core.client

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.android.Android

public actual fun provideHttpClientEngine(): HttpClientEngineFactory<*> {
    return Android
}