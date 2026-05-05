package com.circle.timer.common.core.persistence.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.circle.timer.common.core.persistence.entity.WeatherObservationLogSD
import kotlinx.coroutines.flow.Flow

@Dao
internal interface WeatherLogDao {
    @Query("SELECT * FROM weather_logs ORDER BY createdAtEpochMs DESC")
    fun observeAll(): Flow<List<WeatherObservationLogSD>>

    @Query("SELECT * FROM weather_logs WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): WeatherObservationLogSD?

    @Insert
    suspend fun insert(entity: WeatherObservationLogSD): Long

    @Query("DELETE FROM weather_logs WHERE id = :id")
    suspend fun deleteById(id: Long)
}
