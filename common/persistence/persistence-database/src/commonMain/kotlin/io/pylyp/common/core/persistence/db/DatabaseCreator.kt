package com.circle.timer.common.core.persistence.db

import com.circle.timer.common.core.persistence.AppDatabase
import com.circle.timer.core.threading.DispatcherProvider
import org.koin.core.component.KoinComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

internal class DatabaseCreator(
    private val database: AppDatabase,
    private val dispatcherProvider: DispatcherProvider,
) : KoinComponent {

    internal val dbFlow: Flow<AppDatabase> by lazy {
        flow { emit(database) }
            .flowOn(dispatcherProvider.IO)
    }

    internal suspend fun getDb(): AppDatabase = withContext(dispatcherProvider.IO) { database }
}


