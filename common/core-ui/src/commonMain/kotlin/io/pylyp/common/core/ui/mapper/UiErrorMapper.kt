package com.circle.timer.common.core.ui.mapper

import com.circle.timer.common.uikit.entity.UiError

public fun Throwable.toUiError(): UiError {
    return mapErrorPlatform(this)
}
