package com.circle.timer.api.core.client

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.cio.CIO


public actual fun provideHttpClientEngine(): HttpClientEngineFactory<*> {
    return CIO
}