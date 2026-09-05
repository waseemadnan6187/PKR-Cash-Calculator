package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [CashSlipEntity::class], version = 1, exportSchema = false)
abstract class CashDatabase : RoomDatabase() {
    abstract fun cashSlipDao(): CashSlipDao

    companion object {
        @Volatile
        private var INSTANCE: CashDatabase? = null

        fun getDatabase(context: Context): CashDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CashDatabase::class.java,
                    "pkr_cash_counter.db"
                ).fallbackToDestructiveMigration(dropAllTables = true).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
