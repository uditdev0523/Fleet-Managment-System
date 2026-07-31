package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fuel_logs")
data class FuelLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val unitId: String,
    val liters: Double,
    val totalCostRs: Double,
    val odometerKm: Double,
    val stationName: String,
    val dateString: String,
    val notes: String = ""
)
