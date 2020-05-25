package com.cusyas.android.paycheck.utils

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.cusyas.android.paycheck.billDatabase.BillRoomDatabase
import java.util.*

class BillDueWorker(context: Context, params: WorkerParameters) : Worker(context,params) {
    override fun doWork(): Result {
        val appContext = applicationContext

        try {
            val unpaidBillList = BillRoomDatabase.getDatabase(appContext).billDao().getUnpaidBills()
            val todayDate = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        }catch (throwable: Throwable){
            Result.failure()
        }



        return Result.success()
    }

}