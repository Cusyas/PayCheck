package com.cusyas.android.paycheck

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.core.view.get
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cusyas.android.paycheck.BillDatabase.Bill
import com.cusyas.android.paycheck.BillDatabase.BillViewModel
import kotlinx.android.synthetic.main.activity_new_bill.*
import java.text.NumberFormat


class NewBillActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var editBillNameView: EditText
    private lateinit var daySpinner: Spinner
    private lateinit var billViewModel: BillViewModel
    private lateinit var editBillAmount: EditText

    private var selectedDay: String = "1"
    private var billId: Int = -1

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        selectedDay = parent?.getItemAtPosition(position).toString()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        selectedDay = "1"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        billViewModel = ViewModelProvider(this).get(BillViewModel::class.java)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_bill)

        val button = findViewById<Button>(R.id.button_save)
        editBillAmount = findViewById(R.id.edit_bill_amount)
        editBillNameView = findViewById(R.id.edit_bill_name)
        daySpinner = findViewById(R.id.spinner_days)

        billViewModel = ViewModelProvider(this).get(billViewModel::class.java)



        editBillAmount.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
            var current = ""
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.toString().equals(current)){
                    editBillAmount.removeTextChangedListener(this)

                    var cleanString = s.toString().replace("\$", "")
                    cleanString = cleanString.replace(",", "")

                    var parsed = cleanString.toDouble()
                    var formatted = NumberFormat.getCurrencyInstance().format(parsed)

                    current = formatted
                    editBillAmount.setText(formatted)
                    editBillAmount.setSelection(formatted.length)

                    editBillAmount.addTextChangedListener(this)
                }
            }
        })

        button.setOnClickListener {
            if (editBillNameView.text.isEmpty()){
                Toast.makeText(this,"Bill name cannot be empty", Toast.LENGTH_LONG).show()
            }
            else{
                //if (selectedDay == null) selectedDay = "1"
                var billAmount = editBillAmount.text.toString().replace("\$", "")
                billAmount = billAmount.replace(",", "")
                val bill = Bill(edit_bill_name.text.toString(), billAmount.toDouble(), daySpinner.selectedItemPosition)
                billViewModel.insert(bill)
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }
        var billId = intent.getIntExtra("billId", -1)
        //billId[0] = intent.getIntExtra("billId", -1)
        if (billId > -1) {
            billViewModel.loadById(billId).observe(this, Observer {
                editBillNameView.setText(it.bill_name)
                editBillAmount.setText(it.bill_amount.toString())
                daySpinner.setSelection(it.bill_due_date)
            })


        }

    }


}
