package com.soorya.wealthmanager.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.soorya.wealthmanager.data.local.dao.*
import com.soorya.wealthmanager.data.local.entity.*

@Database(
    entities = [TransactionEntity::class, GoalEntity::class, AssetEntity::class],
    version = 1,
    exportSchema = false
)
abstract class WealthDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun goalDao(): GoalDao
    abstract fun assetDao(): AssetDao

    companion object {
        const val DATABASE_NAME = "wealth_manager_db"
    }
}
