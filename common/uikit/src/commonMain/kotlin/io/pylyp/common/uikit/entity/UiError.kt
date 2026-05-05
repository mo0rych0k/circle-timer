package com.circle.timer.common.uikit.entity

import androidx.compose.runtime.Immutable
import org.jetbrains.compose.resources.DrawableResource

@Immutable
public data class UiError(
    val title: PrintableText,
    val description: PrintableText?,
    val image: DrawableResource?,
    val cause: Throwable,
)
