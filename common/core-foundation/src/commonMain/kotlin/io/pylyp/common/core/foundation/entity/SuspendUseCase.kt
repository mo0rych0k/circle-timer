package com.circle.timer.common.core.foundation.entity

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

public abstract class SuspendUseCase<in P, R> {

    protected abstract suspend fun execute(parameters: P): R

    public operator fun invoke(parameters: P): Flow<Resource<R>> = flow {
        emit(Resource.Loading)
        try {
            val data = execute(parameters)
            emit(Resource.Success(data = data))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Unknown Error", e))
        }
    }
}
