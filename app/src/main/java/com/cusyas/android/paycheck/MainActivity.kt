package com.cusyas.android.paycheck

import android.app.Activity
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
            bills?.let {
                adapter.setBills(it)
                it.forEach {
                    if (!it.bill_paid){
                        totalDue += it.bill_amount
                    }
                }
                totalDueTextView.append("\n" + NumberFormat.getCurrencyInstance().format(totalDue))
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
}