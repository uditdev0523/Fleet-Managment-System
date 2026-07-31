package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fleet_units")
data class FleetUnitEntity(
    @PrimaryKey val id: String, // e.g. "GF-101"
    val registrationNo: String, // e.g. "RJ-14-GB-9921"
    val modelName: String, // e.g. "Tata Prima 3525.K"
    val status: String, // "ACTIVE", "STEALTH", "MAINTENANCE", "IDLE"
    val driverName: String,
    val materialLoad: String, // "32.4 Tons Silica"
    val originLocation: String,
    val destinationLocation: String,
    val currentLocation: String,
    val latitude: Double,
    val longitude: Double,
    val speedKmH: Int,
    val fuelPercent: Int,
    val adBluePercent: Int,
    val fuelFlowL100km: Float,
    val stealthActive: Boolean,
    val lastPingTime: String,
    val healthScore: Int,
    val cargoWeightKg: Int,
    val maxCapacityKg: Int,
    val tpmsPressuresPsi: String, // "115,112,118,114,116,110"
    val engineTempC: Int,
    val idleMinutes: Int,
    val ecoScore: Int
)

@Entity(tableName = "fleet_alerts")
data class FleetAlertEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val unitId: String,
    val title: String,
    val message: String,
    val type: String, // "FUEL_LEAK", "STEALTH_TRIGGER", "TPMS_WARN", "G_IMPACT", "MAINTENANCE"
    val timestamp: String,
    val severity: String, // "HIGH", "MEDIUM", "INFO"
    val isResolved: Boolean = false
)

@Entity(tableName = "stealth_config")
data class StealthConfigEntity(
    @PrimaryKey val id: Int = 1,
    val globalStealthEnabled: Boolean = true,
    val privacyHoursEnabled: Boolean = true,
    val startTime: String = "22:00",
    val endTime: String = "06:00",
    val silentAlertsActive: Boolean = true,
    val cryptoKeyStatus: String = "ACTIVE • RSA-4096",
    val maskLocationInPublicFeed: Boolean = true,
    val gSensorSensitivity: String = "HIGH (0.8G)"
)

@Entity(tableName = "maintenance_tasks")
data class MaintenanceTaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val unitId: String,
    val componentName: String,
    val dueKm: Int,
    val status: String, // "HEALTHY", "DUE_SOON", "OVERDUE"
    val estimatedCostRs: Int
)
