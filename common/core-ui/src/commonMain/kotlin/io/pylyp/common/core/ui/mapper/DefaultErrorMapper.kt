package com.circle.timer.common.core.ui.mapper

import com.circle.timer.common.uikit.entity.UiError

public object DefaultErrorMapper {

    public fun mapError(error: Throwable): UiError {
        return mapErrorPlatform(error)
    }

}
