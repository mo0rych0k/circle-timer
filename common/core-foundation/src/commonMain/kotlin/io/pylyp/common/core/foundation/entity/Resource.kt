package com.circle.timer.common.core.foundation.entity

public sealed interface Resource<out T> {
    public data object Idle : Resource<Nothing>
    public data object Loading : Resource<Nothing>
    public data class Success<out T>(val data: T) : Resource<T>
    public data class Error(
        val message: String,
        val throwable: Throwable? = null,
    ) : Resource<Nothing>
}
