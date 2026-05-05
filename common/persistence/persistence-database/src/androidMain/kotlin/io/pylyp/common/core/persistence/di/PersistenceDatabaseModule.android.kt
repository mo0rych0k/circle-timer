package com.circle.timer.common.core.persistence.di

import android.content.Context
import androidx.room.Room
import com.circle.timer.common.core.persistence.AppDatabase
import org.koin.core.module.Module
import org.koin.dsl.module


public actual val persistenceDatabasePlatformModule: Module = module {
    single<AppDatabase> {
        val appContext: Context = get()
        val dbFile = appContext.getDatabasePath(DB_NAME)
        Room.databaseBuilder<AppDatabase>(
            context = appContext,
            name = dbFile.absolutePath,
        )
            .build()
    }
}
