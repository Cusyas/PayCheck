package com.cusyas.android.paycheck.billDatabase

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "bill_table")
class Bill(@ColumnInfo(name = "bill_name")  var bill_name: String,
           @ColumnInfo(name = "bill_amount") var bill_amount: Double = 0.00,
           @ColumnInfo(name = "bill_due_date") var bill_due_date: Int = 1,
           @ColumnInfo(name = "bill_paid") var bill_paid: Boolean = false,
           @ColumnInfo(name = "bill_paid_month_advance") var bill_paid_month_advance: Boolean = false){
    @PrimaryKey(autoGenerate = true) var bill_id: Int = 0

}