package com.cusyas.android.paycheck

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.lifecycle.ViewModelProvider
import com.cusyas.android.paycheck.BillDatabase.Bill
import com.cusyas.android.paycheck.BillDatabase.BillViewModel
import kotlinx.android.synthetic.main.activity_new_bill.*
import java.text.NumberFormat

class NewBillActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var editWordView: EditText
    private lateinit var daySpinner: Spinner
    private lateinit var billViewModel: BillViewModel

    private var selectedDay: String = "1"

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
        editWordView = findViewById(R.id.edit_bill_name)
        daySpinner = findViewById(R.id.spinner_days)

        billViewModel = ViewModelProvider(this).get(billViewModel::class.java)

        edit_bill_amount.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
            var current = ""
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.toString().equals(current)){
                    edit_bill_amount.removeTextChangedListener(this)

                    var cleanString = s.toString().replace("\$", "")
                    cleanString = cleanString.replace(",", "")
                    cleanString = cleanString.replace(".", "")

                    var parsed = cleanString.toDouble()
                    var formatted = NumberFormat.getCurrencyInstance().format(parsed/100)

                    current = formatted
                    edit_bill_amount.setText(formatted)
                    edit_bill_amount.setSelection(formatted.length)

                    edit_bill_amount.addTextChangedListener(this)
                }
            }
        })

        val button = findViewById<Button>(R.id.button_save)
        button.setOnClickListener {
            if (editWordView.text.isEmpty()){
                Toast.makeText(this,"Bill name cannot be empty", Toast.LENGTH_LONG).show()
            }
            else{
                //if (selectedDay == null) selectedDay = "1"
                var billAmount = edit_bill_amount.text.toString().replace("\$", "")
                billAmount = billAmount.replace(",", "")
                val bill = Bill(edit_bill_name.text.toString(), billAmount.toDouble(), selectedDay.toInt())
                billViewModel.insert(bill)
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }

    }


}
