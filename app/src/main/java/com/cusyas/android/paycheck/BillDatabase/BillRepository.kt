package com.cusyas.android.paycheck.BillDatabase

import androidx.lifecycle.LiveData

class BillRepository(private val billDao: BillDao) {
    val allBills: LiveData<List<Bill>> = billDao.getSortedBills()

    suspend fun insert(bill: Bill){
        billDao.insert(bill)
    }

    fun loadById(billId: Int): LiveData<Bill>{
        return billDao.loadById(billId)
    }
    fun loadAllByIds(billId: IntArray): LiveData<List<Bill>>{
        return billDao.loadAllByIds(billId)
    }
    suspend fun updateBill(bill: Bill){
        billDao.updateBill(bill)
    }
    suspend fun deleteBill(bill: Bill){
        billDao.delete(bill)
    }

    suspend fun resetAllPaid(){
        billDao.resetAllPaid()
    }

}