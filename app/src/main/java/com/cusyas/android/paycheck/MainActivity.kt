package com.cusyas.android.paycheck

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cusyas.android.paycheck.BillDatabase.BillViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.NumberFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var billViewModel: BillViewModel
    private lateinit var totalDueTextView: TextView

    private val newBillActivityRequestCode = 1
    private var totalDue: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        totalDueTextView = findViewById(R.id.tv_total_due)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        val adapter = BillListAdapter(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        billViewModel = ViewModelProvider(this).get(BillViewModel::class.java)

        billViewModel.allBills.observe(this, Observer { bills ->
            bills?.let { list ->
                adapter.setBills(list)
                var payReset: Boolean = false
                val sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
                val recentPaidMonth = sharedPref.getInt(getString(R.string.most_recent_paid_month_key), -1)
                val calendarInstance = Calendar.getInstance()
                if(recentPaidMonth != calendarInstance.get( Calendar.MONTH)){
                    payReset = true
                    sharedPref.edit().putInt(getString(R.string.most_recent_paid_month_key), calendarInstance.get(Calendar.MONTH)).apply()
                }else { payReset = false }
                list.forEach {
                    if (payReset && it.bill_paid_month_advance){
                        it.bill_paid_month_advance = false
                        it.bill_paid = true
                        billViewModel.updateBill(it)
                    }else if (payReset && !it.bill_paid_month_advance){
                        it.bill_paid = false
                        billViewModel.updateBill(it)
                    }
                    else if (!payReset && !it.bill_paid){
                        totalDue += it.bill_amount
                    }
                }
                totalDueTextView.text = resources.getText(R.string.bill_total_amount_due).toString() + ("\n" + NumberFormat.getCurrencyInstance().format(totalDue))
            }
        })


        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(this@MainActivity, NewBillActivity::class.java)
            startActivityForResult(intent,newBillActivityRequestCode)
        }
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

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }
}