package com.cusyas.android.paycheck.BillDatabase

import androidx.lifecycle.LiveData

class BillRepository(private val billDao: BillDao) {
    val allBills: LiveData<List<Bill>> = billDao.getAlphabetizedBills()

    suspend fun insert(bill: Bill){
        billDao.insert(bill)
    }

    suspend fun loadById(billId: Int): LiveData<Bill>{
        return billDao.loadById(billId)
    }
}