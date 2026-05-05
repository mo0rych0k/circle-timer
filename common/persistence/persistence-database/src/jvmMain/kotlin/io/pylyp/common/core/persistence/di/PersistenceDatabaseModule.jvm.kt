package com.circle.timer.common.core.persistence.di

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.circle.timer.common.core.persistence.AppDatabase
import org.koin.core.module.Module
import org.koin.dsl.module
import java.io.File


public actual val persistenceDatabasePlatformModule: Module = module {
    single<AppDatabase> {
        val dbFile = File(System.getProperty("java.io.tmpdir"), DB_NAME)
        Room.databaseBuilder<AppDatabase>(name = dbFile.absolutePath)
            .setDriver(BundledSQLiteDriver())
            .build()
    }
}