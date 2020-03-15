package com.cusyas.android.paycheck.Utils

import java.time.YearMonth
import java.util.*

object BillDueDateDistance {
    fun getDaysUntilDue(dueDate:Int): Int{
        val calenderInstance = Calendar.getInstance()
        val todaysDate = calenderInstance.get(Calendar.DAY_OF_MONTH)
        if (todaysDate < dueDate){
            return dueDate - todaysDate
        } else if (todaysDate == dueDate){
            return 0
        }else {
            val monthObject = GregorianCalendar(
                calenderInstance.get(Calendar.YEAR),
                calenderInstance.get(Calendar.MONTH),
                1
            )
            val daysInMonth = monthObject.getActualMaximum(Calendar.DAY_OF_MONTH)
            return daysInMonth - todaysDate + dueDate
        }
    }
    fun getColorMix(dueDate: Int): Float{
        val daysUntilDue = getDaysUntilDue(dueDate)
        if (daysUntilDue >= 30){
            //411dp will fill the entire box with whatever the current status color is
            return 1f
        } else if (daysUntilDue == 0){
            return .01f
        }
        else{
            return daysUntilDue / 30f
        }
    }
}