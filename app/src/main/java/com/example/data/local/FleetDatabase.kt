package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        FleetUnitEntity::class,
        FleetAlertEntity::class,
        StealthConfigEntity::class,
        MaintenanceTaskEntity::class,
        FuelLogEntity::class,
        MaintenanceThresholdEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class FleetDatabase : RoomDatabase() {

    abstract fun fleetDao(): FleetDao

    companion object {
        @Volatile
        private var INSTANCE: FleetDatabase? = null

        fun getInstance(context: Context): FleetDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FleetDatabase::class.java,
                    "ghost_fleet.db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
