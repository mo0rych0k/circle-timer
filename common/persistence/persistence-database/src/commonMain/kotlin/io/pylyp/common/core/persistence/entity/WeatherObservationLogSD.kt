package com.circle.timer.common.core.persistence.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "weather_logs",
    indices = [
        Index(
            name = "idx_weather_logs_created_at_epoch_ms",
            value = ["createdAtEpochMs"],
        ),
    ],
)
public data class WeatherObservationLogSD(
    @PrimaryKey(autoGenerate = true)
    public val id: Long = 0L,
    public val createdAtEpochMs: Long,
    public val latitude: Double?,
    public val longitude: Double?,
    public val locationLabel: String?,
    public val userTemperatureC: Double,
    public val userHumidityPercent: Int,
    public val userPressureMmHg: Int,
    public val userWindDirectionKey: String,
    public val userWindStrengthPercent: Int,
    public val userWeatherTypeKey: String,
    public val apiTemperatureC: Double?,
    public val apiHumidityPercent: Int?,
    public val apiPressureMmHg: Double?,
    public val apiWindDescription: String?,
    public val apiDescription: String?,
    public val temperatureDeltaC: Double,
    public val discrepancyLevelKey: String,
    public val isHighDiscrepancy: Boolean,
)
