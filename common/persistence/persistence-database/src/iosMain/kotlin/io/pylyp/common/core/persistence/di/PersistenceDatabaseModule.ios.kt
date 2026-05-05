package com.circle.timer.common.core.persistence.di

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.circle.timer.common.core.persistence.AppDatabase
import org.koin.core.module.Module
import org.koin.dsl.module
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask
import kotlinx.cinterop.ExperimentalForeignApi


public actual val persistenceDatabasePlatformModule: Module = module {
    single<AppDatabase> {
        val dbFilePath = documentDirectory() + "/$DB_NAME"
        Room.databaseBuilder<AppDatabase>(name = dbFilePath)
            .setDriver(BundledSQLiteDriver())
            .build()
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun documentDirectory(): String {
    val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null,
    )
    return requireNotNull(documentDirectory?.path)
}
