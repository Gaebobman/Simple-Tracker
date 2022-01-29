package com.standardlee.simpletracker.data

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

data class Usage(
    @PrimaryKey val label: String,
    @ColumnInfo(name = "daily_data") val dailyData:Long?,
    @ColumnInfo(name = "weekly_data") val weekly_data:Long?,
    @ColumnInfo(name = "monthly_data") val monthly_data:Long?
)
