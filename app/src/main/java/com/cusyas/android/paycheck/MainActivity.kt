package com.cusyas.android.paycheck

import android.app.Activity
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.cusyas.android.paycheck.billDatabase.BillViewModel
import com.cusyas.android.paycheck.settings.SettingsActivity
import com.cusyas.android.paycheck.utils.BillDueDateDistance
import com.cusyas.android.paycheck.utils.NotificationWorker
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.NumberFormat
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private val newBillActivityRequestCode = 1


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.tb_bill_list))
        createNotificationChannel()

        val sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        if (!sharedPreferences.getBoolean(getString(R.string.bill_worker_created), false)) billNotificationWorker(sharedPreferences)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == newBillActivityRequestCode && resultCode == Activity.RESULT_OK){
            data?.getIntExtra("id", -1)

        } else{
            Toast.makeText(
                applicationContext,
                R.string.empty_not_saved,
                Toast.LENGTH_LONG).show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.menu_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun createNotificationChannel(){
        //Creating the NotificationChannel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val name = getString(R.string.channel_bill_notification_name)
            val descriptionText = getString(R.string.channel_bill_notification_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("Bill Notification", name, importance).apply {
                description = descriptionText
            }

            // Registering the channel
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun billNotificationWorker(sharedPref: SharedPreferences){
        val notificationWorkRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInitialDelay(BillDueDateDistance.getMinutesForWorker(applicationContext), TimeUnit.MINUTES)
            .addTag(getString(R.string.worker_bill_notification_tag))
            .build()
        WorkManager.getInstance(applicationContext).enqueue(notificationWorkRequest)

        sharedPref.edit().putBoolean(getString(R.string.bill_worker_created), true).apply()
    }
}