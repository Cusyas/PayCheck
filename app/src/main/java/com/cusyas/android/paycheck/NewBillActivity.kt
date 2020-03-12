package com.cusyas.android.paycheck

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.Selection
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.lifecycle.Observer
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

        var current = ""
        billId = intent.getIntExtra("billId", -1)
        if (billId > -1) {
            billViewModel.loadById(billId).observe(this, Observer {
                editBillNameView.setText(it.bill_name)
                var billAmount = it.bill_amount * 10
                var cleanString = billAmount.toString().replace(",", "")
                cleanString = cleanString.replace(".", "")

                val parsed = cleanString.toDouble()
                val formatted = NumberFormat.getCurrencyInstance().format(parsed/100)

                current = formatted
                editBillAmount.setText(formatted)
                // -1 because the index for the spinner starts at 0
                daySpinner.setSelection(it.bill_due_date-1)
            })
        }




        editBillAmount.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                var formatted = ""
                if (!s.toString().isNullOrEmpty()){
                    var cleanString = s.toString().replace(",", "")
                    cleanString = cleanString.replace("\$", "")
                    cleanString = cleanString.replace(".", "")

                    val parsed = cleanString.toDouble()
                    formatted = NumberFormat.getCurrencyInstance().format(parsed/100)
                }
                if (formatted != current){

                    editBillAmount.removeTextChangedListener(this)

                    var cleanString = s.toString().replace("\$", "")
                    cleanString = cleanString.replace(",", "")
                    cleanString = cleanString.replace(".", "")

                    val parsed = cleanString.toDouble()
                    val formatted = NumberFormat.getCurrencyInstance().format(parsed/100)

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
                var billAmount = editBillAmount.text.toString()
                billAmount = billAmount.replace("\$", "")
                billAmount = billAmount.replace(",", "")

                // +1 is added to the daySpinner because the index starts at 0 but the spinner is for the due date
                val bill = Bill(edit_bill_name.text.toString(), billAmount.toDouble(), daySpinner.selectedItemPosition+1)
                if (billId == -1){
                    billViewModel.insert(bill)
                } else{
                    bill.bill_id = billId
                    billViewModel.updateBill(bill)
                }
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }

        // moving the cursor to the end of the name
        editBillNameView.requestFocus()
        editBillNameView.setSelection(editBillNameView.text.length)
    }
}