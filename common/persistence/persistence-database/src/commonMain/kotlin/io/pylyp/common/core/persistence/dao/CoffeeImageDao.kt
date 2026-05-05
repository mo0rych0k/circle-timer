package com.circle.timer.common.core.persistence.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.circle.timer.common.core.persistence.entity.CoffeeImageSD
import kotlinx.coroutines.flow.Flow

@Dao
internal interface CoffeeImageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(items: List<CoffeeImageSD>)

    @Query("SELECT * FROM CoffeeImageSD")
    fun getFlow(): Flow<List<CoffeeImageSD>>
}