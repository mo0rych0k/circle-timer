package com.circle.timer.common.core.persistence

import com.circle.timer.common.core.persistence.db.DatabaseCreator
import com.circle.timer.common.core.persistence.entity.WeatherObservationLogSD
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest

public interface WeatherObservationStorage {
    public fun observeAll(): Flow<List<WeatherObservationLogSD>>

    public suspend fun getById(id: Long): WeatherObservationLogSD?

    public suspend fun insert(entity: WeatherObservationLogSD): Long

    public suspend fun deleteById(id: Long)
}

internal class WeatherObservationStorageImpl(
    private val creator: DatabaseCreator,
) : WeatherObservationStorage {

    override fun observeAll(): Flow<List<WeatherObservationLogSD>> {
        return creator.dbFlow.flatMapLatest { it.weatherLogDao().observeAll() }
            .distinctUntilChanged()
    }

    override suspend fun getById(id: Long): WeatherObservationLogSD? {
        return creator.getDb().weatherLogDao().getById(id)
    }

    override suspend fun insert(entity: WeatherObservationLogSD): Long {
        return creator.getDb().weatherLogDao().insert(entity)
    }

    override suspend fun deleteById(id: Long) {
        creator.getDb().weatherLogDao().deleteById(id)
    }
}
