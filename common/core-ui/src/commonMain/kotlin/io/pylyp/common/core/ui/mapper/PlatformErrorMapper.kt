package com.circle.timer.common.core.ui.mapper

import com.circle.timer.common.uikit.entity.UiError

internal expect fun mapErrorPlatform(error: Throwable): UiError
