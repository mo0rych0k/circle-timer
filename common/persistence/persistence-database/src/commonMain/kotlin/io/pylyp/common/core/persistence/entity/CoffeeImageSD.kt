package com.circle.timer.common.core.persistence.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
public data class CoffeeImageSD(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val url: String
)

