package com.cusyas.android.paycheck.settings

import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import androidx.preference.SwitchPreferenceCompat
import com.cusyas.android.paycheck.R

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

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            val notificationEnabled: SwitchPreferenceCompat? = findPreference("bills_notification")
            val billsNotificationDistance: ListPreference? = findPreference("bills_notification_distance")

            // check if notifications are enabled, if so show the picker for time
            if (notificationEnabled?.isChecked == true){
                billsNotificationDistance?.isVisible = true
            }

            /* click listener to change the visibility of the notification distance based on if
            notifications are currently enabled
             */
            notificationEnabled?.setOnPreferenceClickListener {
                billsNotificationDistance?.isVisible = !billsNotificationDistance?.isVisible!!
                true
            }
        }
    }
}