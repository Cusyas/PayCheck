package com.cusyas.android.paycheck.BillDatabase

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "bill_table")
class Bill(@ColumnInfo(name = "bill_name")  val bill_name: String,
            @ColumnInfo(name = "bill_amount") val bill_amount: Double = 0.00,
            @ColumnInfo(name = "bill_due_date") val bill_due_date: Int = 1,
            @ColumnInfo(name = "bill_paid") val bill_paid: Boolean = false){
    @PrimaryKey(autoGenerate = true) var bill_id: Int = 0

}