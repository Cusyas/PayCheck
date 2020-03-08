package com.cusyas.android.paycheck.Database

import androidx.lifecycle.LiveData

class BillRepository(private val billDao: BillDao) {
    val allBills: LiveData<List<Bill>> = billDao.getAlphabetizedBills()

    suspend fun insert(bill: Bill){
        billDao.insert(bill)
    }
}