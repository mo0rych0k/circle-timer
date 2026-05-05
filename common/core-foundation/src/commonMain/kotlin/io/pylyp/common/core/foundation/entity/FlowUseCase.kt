package com.circle.timer.common.core.foundation.entity

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

public abstract class FlowUseCase<in P, R> {

    protected abstract fun execute(parameters: P): Flow<R>

    public operator fun invoke(parameters: P): Flow<Resource<R>> =
        execute(parameters)
            .map<R, Resource<R>> { Resource.Success(it) }
            .onStart { emit(Resource.Loading) }
            .catch { e ->
                emit(Resource.Error(e.message ?: "Unknown Error", e))
            }
}
