package com.circle.timer.common.core.persistence


import com.circle.timer.common.core.persistence.db.DatabaseCreator
import com.circle.timer.common.core.persistence.entity.CoffeeImageSD
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest

public interface CoffeeImagesStorage {
    public suspend fun insert(items: List<CoffeeImageSD>)
    public fun getFlow(): Flow<List<CoffeeImageSD>>
}

internal class CoffeeImagesStorageImpl(
    private val creator: DatabaseCreator
) : CoffeeImagesStorage {

    override suspend fun insert(items: List<CoffeeImageSD>) {
        creator.getDb().coffeeImageDao().insert(items)
    }

    override fun getFlow(): Flow<List<CoffeeImageSD>> {
        return creator.dbFlow.flatMapLatest { it.coffeeImageDao().getFlow() }
            .distinctUntilChanged()
    }
}