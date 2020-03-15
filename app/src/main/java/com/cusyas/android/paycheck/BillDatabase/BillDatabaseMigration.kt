package com.cusyas.android.paycheck.BillDatabase

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object: Migration(1,2){
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE bill_table"
        + " ADD COLUMN 'bill_paid' INT NOT NULL DEFAULT 0")
    }
}

val MIGRATION_2_3 = object: Migration(2,3){
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE bill_table"
        + " ADD COLUMN 'bill_paid_month_advance' INT NOT NULL DEFAULT 0")
    }
}