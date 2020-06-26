package com.cusyas.android.paycheck.utils

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.preference.PreferenceManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.cusyas.android.paycheck.MainActivity
import com.cusyas.android.paycheck.R
import com.cusyas.android.paycheck.billDatabase.Bill
import com.cusyas.android.paycheck.billDatabase.BillRoomDatabase
import java.text.NumberFormat
import java.time.YearMonth
import java.util.*
import java.util.concurrent.TimeUnit

class NotificationWorker(context: Context, workerParameters: WorkerParameters):
    Worker(context, workerParameters){
    private val contextUse = context
    private val DAYDUE = "day_due"
    private val DAYBEFORE = "day_before_due"
    private val DAYDUEANDBEFORE = "day_before_and_day_due"

    override fun doWork(): Result {
        val defaultSharedPreference = PreferenceManager.getDefaultSharedPreferences(applicationContext)

        var dueBills: List<Bill> = listOf()

        // return success if notifications are disabled
        if (!defaultSharedPreference.getBoolean("bills_notification", true)) return Result.success()

        val calenderInstance = Calendar.getInstance()

        val today = calenderInstance.get(Calendar.DAY_OF_MONTH)
        val nextDay: Int

        val daysInMonth: Int

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val monthObject = YearMonth.of(
                calenderInstance.get(Calendar.YEAR),
                calenderInstance.get(Calendar.MONTH)
            )
            daysInMonth = monthObject.lengthOfMonth()
        } else {
            val monthObject: Calendar = GregorianCalendar(
                calenderInstance.get(Calendar.YEAR),
                calenderInstance.get(Calendar.MONTH),
                1
            )
            daysInMonth = monthObject.getActualMaximum(Calendar.DAY_OF_MONTH)
        }

        if (today == daysInMonth){
            nextDay = 1
        } else{
            nextDay = today + 1
        }
        val distance = defaultSharedPreference.getString("bills_notification_distance", DAYDUE)
        Log.i("Worker", distance)
        when (distance){
            DAYDUE -> dueBills = BillRoomDatabase.getDatabase(contextUse).billDao().getUnpaidDue(today)
            DAYBEFORE -> dueBills = BillRoomDatabase.getDatabase(contextUse).billDao().getUnpaidDue(nextDay)
            DAYDUEANDBEFORE -> dueBills = BillRoomDatabase.getDatabase(contextUse).billDao().getUnpaidBothDue(today,nextDay)
        }

        var paymentAmount = 0.0
        val billAmount = dueBills.size

        if (billAmount == 0) return Result.success()


        // for loop to loop through all unpaid bills
        for (bill in dueBills){
            paymentAmount += bill.bill_amount
        }

        val cleanString: String = paymentAmount.toString().replace(".", "")

        val parsed: Double = cleanString.toDouble()
        val billAmountFormatted: String = NumberFormat.getCurrencyInstance().format(parsed/100)

        val intent = Intent(contextUse, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(contextUse, 0, intent, 0)

        val billAmountLabel: String

        if (billAmount == 1) billAmountLabel = "bill"
        else billAmountLabel = "bills"

        val notificationBuilder = NotificationCompat.Builder(contextUse, "Bill Notification")
            .setSmallIcon(R.drawable.pay_check_logo)
            .setContentTitle("Bills Due")
            .setContentText("You have $billAmount $billAmountLabel due worth ${billAmountFormatted}")
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