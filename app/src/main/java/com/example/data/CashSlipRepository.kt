package com.example.data

import kotlinx.coroutines.flow.Flow

class CashSlipRepository(private val dao: CashSlipDao) {
    val allSlips: Flow<List<CashSlipEntity>> = dao.getAllSlips()

    suspend fun insertSlip(slip: CashSlipEntity): Long = dao.insertSlip(slip)

    suspend fun deleteSlip(slip: CashSlipEntity) = dao.deleteSlip(slip)

    suspend fun getSlipById(id: Long): CashSlipEntity? = dao.getSlipById(id)

    suspend fun deleteAllSlips() = dao.deleteAllSlips()
}
