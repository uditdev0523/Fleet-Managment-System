package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FleetDao {

    @Query("SELECT * FROM fleet_units")
    fun getAllFleetUnits(): Flow<List<FleetUnitEntity>>

    @Query("SELECT * FROM fleet_units WHERE id = :unitId")
    fun getFleetUnitById(unitId: String): Flow<FleetUnitEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFleetUnits(units: List<FleetUnitEntity>)

    @Update
    suspend fun updateFleetUnit(unit: FleetUnitEntity)

    @Query("SELECT * FROM fleet_alerts ORDER BY id DESC")
    fun getAllAlerts(): Flow<List<FleetAlertEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlerts(alerts: List<FleetAlertEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alert: FleetAlertEntity)

    @Query("UPDATE fleet_alerts SET isResolved = 1 WHERE id = :alertId")
    suspend fun resolveAlert(alertId: Long)

    @Query("SELECT * FROM stealth_config WHERE id = 1")
    fun getStealthConfig(): Flow<StealthConfigEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateStealthConfig(config: StealthConfigEntity)

    @Query("SELECT * FROM maintenance_tasks")
    fun getAllMaintenanceTasks(): Flow<List<MaintenanceTaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMaintenanceTasks(tasks: List<MaintenanceTaskEntity>)

    @Query("SELECT * FROM fuel_logs ORDER BY id DESC")
    fun getAllFuelLogs(): Flow<List<FuelLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFuelLog(log: FuelLogEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFuelLogs(logs: List<FuelLogEntity>)

    @Query("DELETE FROM fuel_logs WHERE id = :id")
    suspend fun deleteFuelLogById(id: Long)

    @Query("SELECT * FROM maintenance_thresholds ORDER BY id DESC")
    fun getAllMaintenanceThresholds(): Flow<List<MaintenanceThresholdEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMaintenanceThreshold(threshold: MaintenanceThresholdEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMaintenanceThresholds(thresholds: List<MaintenanceThresholdEntity>)

    @Update
    suspend fun updateMaintenanceThreshold(threshold: MaintenanceThresholdEntity)

    @Query("DELETE FROM maintenance_thresholds WHERE id = :id")
    suspend fun deleteMaintenanceThresholdById(id: Long)
}
