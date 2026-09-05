package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cash_slips")
data class CashSlipEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val timestamp: Long = System.currentTimeMillis(),
    val totalAmount: Long,
    val totalNotesCount: Int,
    val totalCoinsCount: Int,
    val targetAmount: Long? = null,
    val countsMapJson: String, // JSON serialization of Map<Int, Int>
    val remarks: String = ""
)
