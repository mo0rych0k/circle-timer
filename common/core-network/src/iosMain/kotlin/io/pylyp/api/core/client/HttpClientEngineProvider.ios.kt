package com.circle.timer.api.core.client

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.darwin.Darwin

public actual fun provideHttpClientEngine(): HttpClientEngineFactory<*> {
    return Darwin
}