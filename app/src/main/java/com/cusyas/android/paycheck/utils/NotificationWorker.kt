package com.cusyas.android.paycheck.utils

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.preference.PreferenceManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.cusyas.android.paycheck.MainActivity
import com.cusyas.android.paycheck.R
import com.cusyas.android.paycheck.billDatabase.BillRoomDatabase
import java.text.NumberFormat
import java.util.*
import java.util.concurrent.TimeUnit

class NotificationWorker(context: Context, workerParameters: WorkerParameters):
    Worker(context, workerParameters){
    val contextUse = context
    private val DAYDUE = "Day bill is due"
    private val DAYBEFORE = "Day before bill is due"
    private val DAYDUEANDBEFORE = "Day before bill is due and day due"

    override fun doWork(): Result {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)

        // return success if notifications are disabled
        if (!sharedPreferences.getBoolean("bills_notification", true)) return Result.success()

        val unpaidBills =  BillRoomDatabase.getDatabase(contextUse).billDao().getUnpaidBills()

        var notificationDay = mutableListOf<Int>()
        when (sharedPreferences.getString("bills_notification_distance", DAYDUE)){
            DAYDUE -> notificationDay.add(0)
            DAYBEFORE -> notificationDay.add(-1)
            DAYDUEANDBEFORE -> notificationDay.addAll(listOf(0,-1))
        }

        val day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)

        var paymentAmount = 0.0
        var billAmount = 0

        // for loop to loop through all unpaid bills
        for (bill in unpaidBills){
            // for loop to loop through the range of days (day due, day before, day both
            for (days in notificationDay){
                if (bill.bill_due_date == (day + days)){
                    paymentAmount += bill.bill_amount
                    billAmount++
                    break
                }
            }
        }

        val cleanString: String = billAmount.toString().replace(".", "")

        val parsed: Double = cleanString.toDouble()
        val billAmountFormatted: String = NumberFormat.getCurrencyInstance().format(parsed/100)

        val intent = Intent(contextUse, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(contextUse, 0, intent, 0)

        var notificationBuilder = NotificationCompat.Builder(contextUse, "Bill Notification")
            .setSmallIcon(R.drawable.pay_check_logo)
            .setContentTitle("Bills due")
            .setContentText("You have $billAmount bills due worth ${billAmountFormatted}")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(contextUse)) {
            notify(1,notificationBuilder.build())
        }

        val notificationWorkRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInitialDelay(BillDueDateDistance.getMinutesForWorker(contextUse), TimeUnit.MINUTES)
            .addTag(contextUse.getString(R.string.worker_bill_notification_tag))
            .build()
        WorkManager.getInstance(contextUse).enqueue(notificationWorkRequest)


        return Result.success()
    }
}