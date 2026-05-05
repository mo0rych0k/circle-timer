package com.circle.timer.common.core.ui.mapper

import com.circle.timer.common.resources.Res
import com.circle.timer.common.resources.error_generic_message
import com.circle.timer.common.resources.error_generic_title
import com.circle.timer.common.resources.error_serialization_message
import com.circle.timer.common.resources.error_serialization_title
import com.circle.timer.common.uikit.entity.UiError
import com.circle.timer.common.uikit.entity.rawPrintableText
import com.circle.timer.common.uikit.entity.resPrintableText
import kotlinx.coroutines.CancellationException

/**
 * Handles error types that are common across all platforms:
 * - [CancellationException] — should never surface in UI; rethrows
 * - Serialization errors matched by class name — avoids depending on kotlinx.serialization directly
 * - Everything else → generic "unknown error" UiError
 */
internal fun mapCommonError(error: Throwable): UiError {
    // Coroutine cancellations must always propagate — never swallow them
    if (error is CancellationException) throw error

    // Match kotlinx.serialization exceptions by class name to avoid a direct dependency
    val className = error::class.qualifiedName.orEmpty()
    if (className.startsWith("kotlinx.serialization")) {
        return UiError(
            title = resPrintableText(Res.string.error_serialization_title),
            description = resPrintableText(Res.string.error_serialization_message),
            image = null,
            cause = error,
        )
    }

    // Generic fallback
    return UiError(
        title = resPrintableText(Res.string.error_generic_title),
        description = error.getPlatformErrorMessage()
            ?.takeIf { it.isNotBlank() }
            ?.let { rawPrintableText(it) }
            ?: resPrintableText(Res.string.error_generic_message),
        image = null,
        cause = error,
    )
}
