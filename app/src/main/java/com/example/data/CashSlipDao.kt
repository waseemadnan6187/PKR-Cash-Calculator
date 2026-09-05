package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CashSlipDao {
    @Query("SELECT * FROM cash_slips ORDER BY timestamp DESC")
    fun getAllSlips(): Flow<List<CashSlipEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSlip(slip: CashSlipEntity): Long

    @Delete
    suspend fun deleteSlip(slip: CashSlipEntity)

    @Query("SELECT * FROM cash_slips WHERE id = :id")
    suspend fun getSlipById(id: Long): CashSlipEntity?

    @Query("DELETE FROM cash_slips")
    suspend fun deleteAllSlips()
}
