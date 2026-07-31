package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "maintenance_thresholds")
data class MaintenanceThresholdEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val unitId: String,
    val componentName: String,
    val currentMileageKm: Double,
    val targetMileageKm: Double,
    val warningLeadKm: Double = 500.0, // trigger alert when within 500 km
    val isAlertTriggered: Boolean = false,
    val isServiced: Boolean = false,
    val lastUpdatedDate: String = ""
)
