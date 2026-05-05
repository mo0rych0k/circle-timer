package com.circle.timer.common.core.persistence

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import com.circle.timer.common.core.persistence.dao.CoffeeImageDao
import com.circle.timer.common.core.persistence.dao.WeatherLogDao
import com.circle.timer.common.core.persistence.entity.CoffeeImageSD
import com.circle.timer.common.core.persistence.entity.WeatherObservationLogSD


@Database(
    entities = [
        CoffeeImageSD::class,
        WeatherObservationLogSD::class,
    ],
    version = 1,
)
@ConstructedBy(AppDatabaseConstructor::class)
internal abstract class AppDatabase : RoomDatabase() {
    internal abstract fun coffeeImageDao(): CoffeeImageDao

    internal abstract fun weatherLogDao(): WeatherLogDao
}

@Suppress("KotlinNoActualForExpect")
internal expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}
