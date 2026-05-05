package com.circle.timer.common.core.persistence.di

import com.circle.timer.common.core.persistence.CoffeeImagesStorage
import com.circle.timer.common.core.persistence.CoffeeImagesStorageImpl
import com.circle.timer.common.core.persistence.WeatherObservationStorage
import com.circle.timer.common.core.persistence.WeatherObservationStorageImpl
import com.circle.timer.common.core.persistence.db.DatabaseCreator
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

public val persistenceDatabaseModule: Module = module {
    singleOf(::DatabaseCreator)
    singleOf(::CoffeeImagesStorageImpl) bind CoffeeImagesStorage::class
    singleOf(::WeatherObservationStorageImpl) bind WeatherObservationStorage::class
}

public expect val persistenceDatabasePlatformModule: Module

internal const val DB_NAME = "appDatabase"