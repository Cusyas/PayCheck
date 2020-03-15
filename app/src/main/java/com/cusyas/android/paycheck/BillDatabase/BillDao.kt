package com.cusyas.android.paycheck.BillDatabase

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface BillDao {

    @Query("SELECT * FROM bill_table ORDER BY bill_name ASC")
    fun getAlphabetizedBills(): LiveData<List<Bill>>

    @Query("SELECT * FROM bill_table WHERE bill_id IN (:billIds)")
    fun loadAllByIds(billIds: IntArray): LiveData<List<Bill>>

    @Query("SELECT * FROM BILL_TABLE WHERE bill_id = :billId")
    fun loadById(billId: Int): LiveData<Bill>

    @Query("SELECT * FROM bill_table ORDER BY bill_paid ASC, bill_due_date ASC")
    fun getSortedBills(): LiveData<List<Bill>>

    @Update
    suspend fun updateBill(bill: Bill)

    @Transaction
    open suspend fun resetAllPaid(){
        resetPaid()
        resetPaidMonthAdvance()
    }

    @Query ("UPDATE bill_table SET bill_paid = 0 WHERE bill_paid_month_advance = 0")
    suspend fun resetPaid()

    @Query ("UPDATE bill_table SET bill_paid = 1 AND bill_paid_month_advance = 0 WHERE bill_paid_month_advance = 1")
    suspend fun resetPaidMonthAdvance()

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(bill: Bill)

    @Delete
    suspend fun delete(bill: Bill)
}