package com.cusyas.android.paycheck.utils

import android.os.Build
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
            if (daysUntilDue >= 30) {
                //411dp will fill the entire box with whatever the current status color is
                return 1f
            } else if (daysUntilDue == 0) {
                return .01f
            } else {
                return daysUntilDue / 30f
            }
        }
    }
}