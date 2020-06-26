package com.cusyas.android.paycheck.settings

import android.app.TimePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.TimePicker
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.cusyas.android.paycheck.R
import com.cusyas.android.paycheck.utils.BillDueDateDistance
import com.cusyas.android.paycheck.utils.NotificationWorker
import java.util.concurrent.TimeUnit

class SettingsActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings, SettingsFragment())
            .commit()
        setSupportActionBar(findViewById(R.id.toolbar_settings))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId){
            android.R.id.home ->{
                NavUtils.navigateUpFromSameTask(this)
                return true
                }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    class SettingsFragment : PreferenceFragmentCompat(), TimePickerDialog.OnTimeSetListener {

        private var notificationHour: Int = 8
        private var notificationMinute: Int = 0

        private lateinit var sharedPref: SharedPreferences
        private lateinit var prefTimePicker: Preference

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            sharedPref = requireActivity().getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE
            )
            notificationHour = sharedPref.getInt(getString(R.string.bill_notification_hour), 8)
            notificationMinute = sharedPref.getInt(getString(R.string.bill_notification_minute), 0)

            val notificationEnabled: SwitchPreferenceCompat? = findPreference("bills_notification")
            val billsNotificationDistance: ListPreference? =
                findPreference("bills_notification_distance")
            prefTimePicker = findPreference("prefTimePicker")!!


            // check if notifications are enabled, if so show the picker for time
            if (notificationEnabled?.isChecked == true) {
                billsNotificationDistance?.isVisible = true
                prefTimePicker.isVisible = true
            }

            /* click listener to change the visibility of the notification distance based on if
            notifications are currently enabled
             */
            notificationEnabled?.setOnPreferenceClickListener {
                billsNotificationDistance?.isVisible = !billsNotificationDistance?.isVisible!!
                prefTimePicker.isVisible = !prefTimePicker.isVisible
                true
            }

            notificationTimeSummarySetter()

            prefTimePicker.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                showTimePicker()
                false
            }
        }

        private fun notificationTimeSummarySetter() {
            val displayHour: Int
            val displayDayNight: String
            when {
                notificationHour > 12 -> {
                    displayHour = notificationHour - 12
                    displayDayNight = "PM"
                }
                else -> {
                    displayHour = notificationHour
                    displayDayNight = "AM"
                }
            }
            when {
                notificationMinute < 10 -> {
                    prefTimePicker.summary = "$displayHour:0${notificationMinute} $displayDayNight"
                }
                else -> {
                    prefTimePicker.summary = "$displayHour:$notificationMinute $displayDayNight"
                }
            }
        }

        private fun showTimePicker() {
            TimePickerDialog(context, this, notificationHour, notificationMinute, false).show()
        }

        override fun onTimeSet(p0: TimePicker?, p1: Int, p2: Int) {
            notificationHour = p1
            notificationMinute = p2
            Log.i("timepick", "Hour: $p1 Minute: $p2")
            with(sharedPref.edit()) {
                putInt(getString(R.string.bill_notification_hour), p1)
                putInt(getString(R.string.bill_notification_minute), p2)
                commit()
            }
            notificationTimeSummarySetter()

            WorkManager.getInstance(requireContext()).cancelAllWorkByTag(getString(R.string.worker_bill_notification_tag))

            val notificationWorkRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
                .setInitialDelay(BillDueDateDistance.getMinutesForWorker(requireContext()), TimeUnit.MINUTES)
                .addTag(requireContext().getString(R.string.worker_bill_notification_tag))
                .build()
            WorkManager.getInstance(requireContext()).enqueue(notificationWorkRequest)
        }
    }
}