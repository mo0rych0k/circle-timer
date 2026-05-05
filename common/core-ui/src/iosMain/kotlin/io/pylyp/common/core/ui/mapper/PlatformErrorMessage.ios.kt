package com.circle.timer.common.core.ui.mapper

import io.ktor.client.engine.darwin.DarwinHttpRequestException

internal actual fun Throwable.getPlatformErrorMessage(): String? {
    var current: Throwable? = this
    while (current != null) {
        if (current is DarwinHttpRequestException) {
            return current.origin.localizedDescription
        }
        current = current.cause
    }
    return this.message
}
