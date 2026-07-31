package com.example.repository

import com.example.data.local.*
import kotlinx.coroutines.flow.Flow

class FleetRepository(private val dao: FleetDao) {

    val allUnits: Flow<List<FleetUnitEntity>> = dao.getAllFleetUnits()
    val allAlerts: Flow<List<FleetAlertEntity>> = dao.getAllAlerts()
    val stealthConfig: Flow<StealthConfigEntity?> = dao.getStealthConfig()
    val maintenanceTasks: Flow<List<MaintenanceTaskEntity>> = dao.getAllMaintenanceTasks()
    val allFuelLogs: Flow<List<FuelLogEntity>> = dao.getAllFuelLogs()
    val maintenanceThresholds: Flow<List<MaintenanceThresholdEntity>> = dao.getAllMaintenanceThresholds()

    fun getUnitById(unitId: String): Flow<FleetUnitEntity?> {
        return dao.getFleetUnitById(unitId)
    }

    suspend fun insertFuelLog(log: FuelLogEntity) {
        dao.insertFuelLog(log)
    }

    suspend fun deleteFuelLog(id: Long) {
        dao.deleteFuelLogById(id)
    }

    suspend fun insertMaintenanceThreshold(threshold: MaintenanceThresholdEntity) {
        dao.insertMaintenanceThreshold(threshold)
    }

    suspend fun updateMaintenanceThreshold(threshold: MaintenanceThresholdEntity) {
        dao.updateMaintenanceThreshold(threshold)
    }

    suspend fun deleteMaintenanceThreshold(id: Long) {
        dao.deleteMaintenanceThresholdById(id)
    }

    suspend fun updateUnit(unit: FleetUnitEntity) {
        dao.updateFleetUnit(unit)
    }

    suspend fun resolveAlert(alertId: Long) {
        dao.resolveAlert(alertId)
    }

    suspend fun updateStealthConfig(config: StealthConfigEntity) {
        dao.insertOrUpdateStealthConfig(config)
    }

    suspend fun toggleUnitStealth(unitId: String, enableStealth: Boolean) {
        // Fetch current unit and update stealth state
    }

    suspend fun populateInitialDataIfEmpty(currentUnits: List<FleetUnitEntity>) {
        if (currentUnits.isNotEmpty()) return

        val initialUnits = listOf(
            FleetUnitEntity(
                id = "GF-309",
                registrationNo = "RJ-14-GB-9921",
                modelName = "Tata Prima 3525.K",
                status = "ACTIVE",
                driverName = "Rajesh Sharma",
                materialLoad = "32.4 Tons Silica Sand",
                originLocation = "Jaipur Quarry Hub",
                destinationLocation = "Noida Industrial Area",
                currentLocation = "NH-48 Corridor (Km 142)",
                latitude = 27.2046,
                longitude = 76.8491,
                speedKmH = 68,
                fuelPercent = 84,
                adBluePercent = 92,
                fuelFlowL100km = 14.2f,
                stealthActive = false,
                lastPingTime = "12s ago",
                healthScore = 96,
                cargoWeightKg = 32400,
                maxCapacityKg = 35000,
                tpmsPressuresPsi = "118,116,120,118,114,116",
                engineTempC = 88,
                idleMinutes = 12,
                ecoScore = 94
            ),
            FleetUnitEntity(
                id = "GF-512",
                registrationNo = "MH-04-EK-8810",
                modelName = "Tata Signa 5525.K",
                status = "STEALTH",
                driverName = "Vikram Singh",
                materialLoad = "45.0 Tons Iron Ore",
                originLocation = "Bhilai Steel Complex",
                destinationLocation = "Mumbai Port Yard 4",
                currentLocation = "Encrypted Route • Masked",
                latitude = 19.0760,
                longitude = 72.8777,
                speedKmH = 74,
                fuelPercent = 62,
                adBluePercent = 78,
                fuelFlowL100km = 16.8f,
                stealthActive = true,
                lastPingTime = "34s ago",
                healthScore = 92,
                cargoWeightKg = 45000,
                maxCapacityKg = 55000,
                tpmsPressuresPsi = "122,120,121,119,118,120",
                engineTempC = 91,
                idleMinutes = 8,
                ecoScore = 91
            ),
            FleetUnitEntity(
                id = "GF-104",
                registrationNo = "KA-01-HG-4512",
                modelName = "Ashok Leyland 4825",
                status = "ACTIVE",
                driverName = "Amitabh Verma",
                materialLoad = "38.2 Tons Bauxite Ore",
                originLocation = "Belagavi Mine Alpha",
                destinationLocation = "Bengaluru Depot",
                currentLocation = "Tumakuru Expressway",
                latitude = 13.3409,
                longitude = 77.1010,
                speedKmH = 55,
                fuelPercent = 45,
                adBluePercent = 60,
                fuelFlowL100km = 15.1f,
                stealthActive = false,
                lastPingTime = "4m ago",
                healthScore = 89,
                cargoWeightKg = 38200,
                maxCapacityKg = 48000,
                tpmsPressuresPsi = "110,112,114,108,112,110",
                engineTempC = 86,
                idleMinutes = 24,
                ecoScore = 88
            ),
            FleetUnitEntity(
                id = "GF-808",
                registrationNo = "DL-01-AX-3312",
                modelName = "Volvo FMX 460 Heavy",
                status = "MAINTENANCE",
                driverName = "Suresh Kumar",
                materialLoad = "Empty • Unloaded",
                originLocation = "Gurugram Logistics Hub",
                destinationLocation = "Delhi Fleet Workshop",
                currentLocation = "Workshop Bay 3",
                latitude = 28.4595,
                longitude = 77.0266,
                speedKmH = 0,
                fuelPercent = 28,
                adBluePercent = 40,
                fuelFlowL100km = 0.0f,
                stealthActive = false,
                lastPingTime = "1h ago",
                healthScore = 64,
                cargoWeightKg = 0,
                maxCapacityKg = 42000,
                tpmsPressuresPsi = "98,102,115,112,100,105",
                engineTempC = 42,
                idleMinutes = 180,
                ecoScore = 72
            )
        )

        val initialAlerts = listOf(
            FleetAlertEntity(
                unitId = "GF-808",
                title = "Fuel Reservoir Fluctuation Detected",
                message = "Possible siphon attempt or sensor noise in Bay 3. Drop: -4.2L in 10 mins.",
                type = "FUEL_LEAK",
                timestamp = "02:14 AM",
                severity = "HIGH"
            ),
            FleetAlertEntity(
                unitId = "GF-512",
                title = "Ghost Stealth Mode Engaged",
                message = "Cryptographic GPS masking active. Transit privacy enabled for MH-04-EK-8810.",
                type = "STEALTH_TRIGGER",
                timestamp = "01:45 AM",
                severity = "INFO"
            ),
            FleetAlertEntity(
                unitId = "GF-104",
                title = "Axle 3 TPMS Pressure Low",
                message = "Tire #4 pressure at 108 PSI (Recommended 118 PSI). Monitor impact shocks.",
                type = "TPMS_WARN",
                timestamp = "12:50 AM",
                severity = "MEDIUM"
            )
        )

        val initialStealthConfig = StealthConfigEntity(
            globalStealthEnabled = true,
            privacyHoursEnabled = true,
            startTime = "22:00",
            endTime = "06:00",
            silentAlertsActive = true,
            cryptoKeyStatus = "ACTIVE • RSA-4096 HARDENED",
            maskLocationInPublicFeed = true,
            gSensorSensitivity = "HIGH (0.8G Impact)"
        )

        val initialTasks = listOf(
            MaintenanceTaskEntity(
                unitId = "GF-808",
                componentName = "Hydro-Retarder & Brake Calipers",
                dueKm = 1200,
                status = "OVERDUE",
                estimatedCostRs = 34500
            ),
            MaintenanceTaskEntity(
                unitId = "GF-104",
                componentName = "AdBlue Exhaust Injector Clean",
                dueKm = 3400,
                status = "DUE_SOON",
                estimatedCostRs = 12000
            ),
            MaintenanceTaskEntity(
                unitId = "GF-309",
                componentName = "Heavy Duty Oil Filter & Synthetic 15W40",
                dueKm = 8900,
                status = "HEALTHY",
                estimatedCostRs = 18500
            )
        )

        val initialFuelLogs = listOf(
            FuelLogEntity(
                unitId = "GF-309",
                liters = 210.0,
                totalCostRs = 19950.0,
                odometerKm = 84250.0,
                stationName = "Indian Oil Express Depot, NH-48",
                dateString = "30 Jul 2026, 08:30 AM",
                notes = "Full tank refuel before Jaipur highway stretch."
            ),
            FuelLogEntity(
                unitId = "GF-512",
                liters = 280.0,
                totalCostRs = 26600.0,
                odometerKm = 112400.0,
                stationName = "HPCL Heavy Transport Hub, Bhilai",
                dateString = "29 Jul 2026, 06:15 PM",
                notes = "Pre-load high capacity diesel fill."
            ),
            FuelLogEntity(
                unitId = "GF-104",
                liters = 180.0,
                totalCostRs = 17100.0,
                odometerKm = 67800.0,
                stationName = "BPCL Expressway Station, Tumakuru",
                dateString = "28 Jul 2026, 02:40 PM",
                notes = "Routine mid-route top-up."
            )
        )

        val initialThresholds = listOf(
            MaintenanceThresholdEntity(
                unitId = "GF-309",
                componentName = "Engine Oil & Filter Service",
                currentMileageKm = 84250.0,
                targetMileageKm = 85000.0,
                warningLeadKm = 1000.0,
                isAlertTriggered = true,
                isServiced = false,
                lastUpdatedDate = "30 Jul 2026"
            ),
            MaintenanceThresholdEntity(
                unitId = "GF-512",
                componentName = "Heavy Hydro-Retarder Inspection",
                currentMileageKm = 112400.0,
                targetMileageKm = 115000.0,
                warningLeadKm = 1500.0,
                isAlertTriggered = false,
                isServiced = false,
                lastUpdatedDate = "29 Jul 2026"
            ),
            MaintenanceThresholdEntity(
                unitId = "GF-104",
                componentName = "Brake Pad & Drum Replacement",
                currentMileageKm = 67800.0,
                targetMileageKm = 68000.0,
                warningLeadKm = 500.0,
                isAlertTriggered = true,
                isServiced = false,
                lastUpdatedDate = "28 Jul 2026"
            ),
            MaintenanceThresholdEntity(
                unitId = "GF-808",
                componentName = "Differential Gearbox Fluid Renewal",
                currentMileageKm = 120500.0,
                targetMileageKm = 120000.0,
                warningLeadKm = 1000.0,
                isAlertTriggered = true,
                isServiced = false,
                lastUpdatedDate = "25 Jul 2026"
            )
        )

        dao.insertFleetUnits(initialUnits)
        dao.insertAlerts(initialAlerts)
        dao.insertOrUpdateStealthConfig(initialStealthConfig)
        dao.insertMaintenanceTasks(initialTasks)
        dao.insertFuelLogs(initialFuelLogs)
        dao.insertMaintenanceThresholds(initialThresholds)
    }
}
