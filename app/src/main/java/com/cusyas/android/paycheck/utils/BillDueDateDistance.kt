package com.cusyas.android.paycheck.utils

import android.content.Context
import android.os.Build
import com.cusyas.android.paycheck.R
import java.time.YearMonth
import java.util.*

class BillDueDateDistance {
    companion object {
        fun getDaysUntilDue(dueDate: Int): Int {
            val calenderInstance = Calendar.getInstance()
            val todaysDate = calenderInstance.get(Calendar.DAY_OF_MONTH)
            if (todaysDate < dueDate) {
                return dueDate - todaysDate
            } else if (todaysDate == dueDate) {
                return 0
            } else {
//            val monthObject: Calendar = GregorianCalendar(
//                calenderInstance.get(Calendar.YEAR),
//                calenderInstance.get(Calendar.MONTH),
//                1
//            )
//            val daysInMonth = monthObject.getActualMaximum(Calendar.DAY_OF_MONTH)
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

                return daysInMonth - todaysDate + dueDate
            }
        }

        fun getColorMix(dueDate: Int): Float {
            val daysUntilDue = getDaysUntilDue(dueDate)
            return if (daysUntilDue >= 30) {
                //411dp will fill the entire box with whatever the current status color is
                1f
            } else if (daysUntilDue == 0) {
                .01f
            } else {
                daysUntilDue / 30f
            }
        }
        fun getMinutesForWorker(context:Context): Long{
            var delay = 0L
            val sharedPrefs = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE)
            val requiredHour = sharedPrefs.getInt(context.getString(R.string.bill_notification_hour), 8)
            val requiredMinute = sharedPrefs.getInt(context.getString(R.string.bill_notification_minute), 0)

            val cal = Calendar.getInstance()
            val currentHour = cal.get(Calendar.HOUR_OF_DAY)
            val currentMinute = cal.get(Calendar.MINUTE)
            when {
                currentHour < requiredHour -> delay += ((requiredHour - currentHour - 1) * 60)
                currentHour > requiredHour -> delay += (24 - (currentHour - requiredHour  + 1)) * 60
                currentHour == requiredHour -> {
                    /* if the hours are the same but the required minutes are AFTER the current
                    minutes then all that is needed is the difference between the required minutes
                    and the current minutes
                    */
                    if (currentMinute < requiredMinute) return (requiredMinute - currentMinute).toLong()
                    /* if the hours are the same and the current minute is less then the required
                    minute then 23 hours will need to pass before the hours can match up where adding
                    the required minutes will line up the time
                    ex. if it is 9:30AM and the required time is 9:15AM, the time has already passed
                    so the notification can't go off until the next day
                    */
                    else delay += 23
                }
            }
            when {
                currentMinute < requiredMinute -> delay += (requiredMinute - currentMinute)
                currentMinute > requiredMinute -> delay += (60 - (currentMinute - requiredMinute))
            }
            return delay
        }
    }
}