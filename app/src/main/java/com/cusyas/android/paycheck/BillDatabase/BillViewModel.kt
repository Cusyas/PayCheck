package com.cusyas.android.paycheck.BillDatabase

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class BillViewModel(application: Application) : AndroidViewModel(application){

    private val repository: BillRepository

    val allBills: LiveData<List<Bill>>

    init {
        val billsDao = BillRoomDatabase.getDatabase(application, viewModelScope).billDao()
        repository = BillRepository(billsDao)
        allBills = repository.allBills
    }

    fun insert(bill: Bill) = viewModelScope.launch {
        repository.insert(bill)
    }

    fun loadById(billId: Int):LiveData<Bill>{
        return repository.loadById(billId)
    }

    fun loadAllByIds(billId: IntArray):LiveData<List<Bill>>{
        return repository.loadAllByIds(billId)
    }

}