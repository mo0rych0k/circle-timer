package com.circle.timer.api.core.client

import io.ktor.client.engine.HttpClientEngineFactory

public expect fun provideHttpClientEngine(): HttpClientEngineFactory<*>