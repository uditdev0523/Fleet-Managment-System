package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.*
import com.example.repository.FleetRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FleetViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: FleetRepository

    val unitsState: StateFlow<List<FleetUnitEntity>>
    val alertsState: StateFlow<List<FleetAlertEntity>>
    val stealthConfigState: StateFlow<StealthConfigEntity?>
    val maintenanceTasksState: StateFlow<List<MaintenanceTaskEntity>>
    val fuelLogsState: StateFlow<List<FuelLogEntity>>
    val maintenanceThresholdsState: StateFlow<List<MaintenanceThresholdEntity>>

    private val _selectedUnitId = MutableStateFlow<String?>("GF-309")
    val selectedUnitId: StateFlow<String?> = _selectedUnitId.asStateFlow()

    private val _unitFilter = MutableStateFlow("ALL") // "ALL", "ACTIVE", "STEALTH", "MAINTENANCE"
    val unitFilter: StateFlow<String> = _unitFilter.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        val dao = FleetDatabase.getInstance(application).fleetDao()
        repository = FleetRepository(dao)

        unitsState = repository.allUnits.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        alertsState = repository.allAlerts.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        stealthConfigState = repository.stealthConfig.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            null
        )

        maintenanceTasksState = repository.maintenanceTasks.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        fuelLogsState = repository.allFuelLogs.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        maintenanceThresholdsState = repository.maintenanceThresholds.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        viewModelScope.launch {
            unitsState.collect { units ->
                if (units.isEmpty()) {
                    repository.populateInitialDataIfEmpty(units)
                }
            }
        }
    }

    val selectedUnit: StateFlow<FleetUnitEntity?> = combine(
        unitsState,
        _selectedUnitId
    ) { units, id ->
        units.find { it.id == id } ?: units.firstOrNull()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val filteredUnits: StateFlow<List<FleetUnitEntity>> = combine(
        unitsState,
        _unitFilter,
        _searchQuery
    ) { units, filter, query ->
        units.filter { unit ->
            val matchesFilter = when (filter) {
                "ACTIVE" -> unit.status == "ACTIVE"
                "STEALTH" -> unit.status == "STEALTH" || unit.stealthActive
                "MAINTENANCE" -> unit.status == "MAINTENANCE"
                else -> true
            }
            val matchesQuery = query.isBlank() ||
                    unit.id.contains(query, ignoreCase = true) ||
                    unit.registrationNo.contains(query, ignoreCase = true) ||
                    unit.modelName.contains(query, ignoreCase = true) ||
                    unit.driverName.contains(query, ignoreCase = true) ||
                    unit.currentLocation.contains(query, ignoreCase = true)

            matchesFilter && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSelectedUnitId(unitId: String) {
        _selectedUnitId.value = unitId
    }

    fun setUnitFilter(filter: String) {
        _unitFilter.value = filter
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleStealthForUnit(unit: FleetUnitEntity) {
        viewModelScope.launch {
            val newStealth = !unit.stealthActive
            val newStatus = if (newStealth) "STEALTH" else "ACTIVE"
            val updatedUnit = unit.copy(
                stealthActive = newStealth,
                status = newStatus,
                currentLocation = if (newStealth) "Encrypted Route • Masked" else "NH-48 Corridor (Km 142)"
            )
            repository.updateUnit(updatedUnit)

            if (newStealth) {
                dao().insertAlert(
                    FleetAlertEntity(
                        unitId = unit.id,
                        title = "Ghost Mode Activated",
                        message = "Cryptographic location masking engaged for unit ${unit.registrationNo}.",
                        type = "STEALTH_TRIGGER",
                        timestamp = "Just Now",
                        severity = "INFO"
                    )
                )
            }
        }
    }

    fun resolveAlert(alertId: Long) {
        viewModelScope.launch {
            repository.resolveAlert(alertId)
        }
    }

    fun updateStealthConfig(config: StealthConfigEntity) {
        viewModelScope.launch {
            repository.updateStealthConfig(config)
        }
    }

    fun addFuelLog(
        unitId: String,
        liters: Double,
        totalCostRs: Double,
        odometerKm: Double,
        stationName: String,
        notes: String
    ) {
        viewModelScope.launch {
            val dateStr = java.text.SimpleDateFormat("dd MMM yyyy, hh:mm a", java.util.Locale.getDefault())
                .format(java.util.Date())
            val log = FuelLogEntity(
                unitId = unitId,
                liters = liters,
                totalCostRs = totalCostRs,
                odometerKm = odometerKm,
                stationName = stationName.ifBlank { "Highway Fuel Station" },
                dateString = dateStr,
                notes = notes
            )
            repository.insertFuelLog(log)

            // Optionally create a fuel entry alert log
            dao().insertAlert(
                FleetAlertEntity(
                    unitId = unitId,
                    title = "Fuel Refill Logged",
                    message = "Added $liters L refuel entry (₹$totalCostRs) at $stationName.",
                    type = "FUEL_FILL",
                    timestamp = "Just Now",
                    severity = "INFO"
                )
            )
        }
    }

    fun deleteFuelLog(id: Long) {
        viewModelScope.launch {
            repository.deleteFuelLog(id)
        }
    }

    fun addMaintenanceThreshold(
        unitId: String,
        componentName: String,
        targetMileageKm: Double,
        currentMileageKm: Double,
        warningLeadKm: Double = 500.0
    ) {
        viewModelScope.launch {
            val dateStr = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault())
                .format(java.util.Date())
            
            val remaining = targetMileageKm - currentMileageKm
            val shouldTrigger = remaining <= warningLeadKm && remaining >= 0
            val isOverdue = remaining < 0

            val threshold = MaintenanceThresholdEntity(
                unitId = unitId,
                componentName = componentName.ifBlank { "Routine Service Check" },
                currentMileageKm = currentMileageKm,
                targetMileageKm = targetMileageKm,
                warningLeadKm = warningLeadKm,
                isAlertTriggered = shouldTrigger || isOverdue,
                isServiced = false,
                lastUpdatedDate = dateStr
            )

            repository.insertMaintenanceThreshold(threshold)

            if (shouldTrigger || isOverdue) {
                val severity = if (isOverdue) "HIGH" else "MEDIUM"
                val title = if (isOverdue) "Maintenance Overdue!" else "Maintenance Mileage Threshold Reached"
                val message = "Unit $unitId $componentName at ${currentMileageKm.toInt()} km (Target: ${targetMileageKm.toInt()} km)."

                dao().insertAlert(
                    FleetAlertEntity(
                        unitId = unitId,
                        title = title,
                        message = message,
                        type = "MAINTENANCE",
                        timestamp = "Just Now",
                        severity = severity
                    )
                )
            }
        }
    }

    fun updateThresholdMileage(threshold: MaintenanceThresholdEntity, newMileageKm: Double) {
        viewModelScope.launch {
            val dateStr = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault())
                .format(java.util.Date())
            
            val remaining = threshold.targetMileageKm - newMileageKm
            val shouldTrigger = remaining <= threshold.warningLeadKm && remaining >= 0
            val isOverdue = remaining < 0

            val updated = threshold.copy(
                currentMileageKm = newMileageKm,
                isAlertTriggered = threshold.isAlertTriggered || shouldTrigger || isOverdue,
                lastUpdatedDate = dateStr
            )

            repository.updateMaintenanceThreshold(updated)

            if ((shouldTrigger || isOverdue) && !threshold.isAlertTriggered) {
                val severity = if (isOverdue) "HIGH" else "MEDIUM"
                val title = if (isOverdue) "Maintenance Overdue!" else "Mileage Threshold Triggered"
                val message = "Unit ${threshold.unitId} crossed ${threshold.componentName} threshold at ${newMileageKm.toInt()} km."

                dao().insertAlert(
                    FleetAlertEntity(
                        unitId = threshold.unitId,
                        title = title,
                        message = message,
                        type = "MAINTENANCE",
                        timestamp = "Just Now",
                        severity = severity
                    )
                )
            }
        }
    }

    fun markThresholdServiced(threshold: MaintenanceThresholdEntity) {
        viewModelScope.launch {
            val updated = threshold.copy(
                isServiced = true,
                isAlertTriggered = false
            )
            repository.updateMaintenanceThreshold(updated)

            dao().insertAlert(
                FleetAlertEntity(
                    unitId = threshold.unitId,
                    title = "Maintenance Completed",
                    message = "Service completed for ${threshold.componentName} on Unit ${threshold.unitId}.",
                    type = "MAINTENANCE",
                    timestamp = "Just Now",
                    severity = "INFO"
                )
            )
        }
    }

    fun deleteMaintenanceThreshold(id: Long) {
        viewModelScope.launch {
            repository.deleteMaintenanceThreshold(id)
        }
    }

    private fun dao() = FleetDatabase.getInstance(getApplication()).fleetDao()
}
