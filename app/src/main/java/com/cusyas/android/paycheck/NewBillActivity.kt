package com.cusyas.android.paycheck

import android.content.Intent
import android.graphics.Color.*
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.cusyas.android.paycheck.BillDatabase.Bill
import com.cusyas.android.paycheck.BillDatabase.BillViewModel
import com.cusyas.android.paycheck.databinding.ActivityNewBillBinding
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.activity_new_bill.*
import java.text.NumberFormat
import java.util.*


class NewBillActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var binding: ActivityNewBillBinding

    private lateinit var billViewModel: BillViewModel

    private var billPaidBeforeLoading: Boolean? = null

    private lateinit var bill: Bill

    private var selectedDay: String = "1"
    private var billId: Int = -1

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        selectedDay = parent?.getItemAtPosition(position).toString()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        selectedDay = "1"
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_edit_bill, menu)
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewBillBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        billViewModel = ViewModelProvider(this).get(BillViewModel::class.java)

        setSupportActionBar(findViewById(R.id.tb_bill_edit))

        billViewModel = ViewModelProvider(this).get(billViewModel::class.java)

        billId = intent.getIntExtra("billId", -1)
        if (billId > -1) {
            billViewModel.loadById(billId).observe(this, Observer {
                if(it != null) {
                    billPaidBeforeLoading = it.bill_paid
                    bill = it

                    binding.swBillPaid.isChecked = it.bill_paid

                    binding.swBillPaidMonthAdvance.isChecked = it.bill_paid_month_advance
                    if (!it.bill_paid){
                        binding.swBillPaidMonthAdvance.visibility = View.INVISIBLE
                        binding.switchMonthAdvanceTextView.visibility = View.INVISIBLE
                    }

                    binding.editBillName.editText!!.setText(it.bill_name)

                    val formatted = NumberFormat.getCurrencyInstance().format(it.bill_amount)

                    binding.editBillAmount.editText!!.setText(formatted)
                    // -1 because the index for the spinner starts at 0
                    binding.spinnerDays.setSelection(it.bill_due_date - 1)
                    // check if the bill is paid, setting the background accordingly
                    if (bill.bill_paid){
                        supportActionBar?.setBackgroundDrawable(ColorDrawable(GREEN))
                    }
                    else{
                        if (bill.bill_due_date < Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) {
                            supportActionBar?.setBackgroundDrawable(ColorDrawable(RED))
                        }else{
                            supportActionBar?.setBackgroundDrawable(ColorDrawable(YELLOW))
                        }
                    }
                }
            })
        }

        binding.editBillAmount.editText!!.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
            var textCurrent = ""
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.editBillAmount.editText!!.removeTextChangedListener(this)
                var cleanString: String = s.toString().replace("\$","")
                cleanString = cleanString.replace(",", "")
                cleanString = cleanString.replace(".", "")

                val parsed: Double = cleanString.toDouble()
                val formatted: String = NumberFormat.getCurrencyInstance().format(parsed/100)

                binding.editBillAmount.editText!!.setText(formatted)
                binding.editBillAmount.editText!!.setSelection(formatted.length)

                binding.editBillAmount.editText!!.addTextChangedListener(this)
            }
        })

        binding.swBillPaid.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                binding.switchBillPaidTextView.text = resources.getText(R.string.switch_paid)
                if (billPaidBeforeLoading != null) { supportActionBar?.setBackgroundDrawable(ColorDrawable(GREEN)) }
                binding.swBillPaidMonthAdvance.visibility = View.VISIBLE
                binding.switchMonthAdvanceTextView.visibility = View.VISIBLE
            } else{
                binding.switchBillPaidTextView.text = resources.getText(R.string.switch_not_paid)
                binding.swBillPaidMonthAdvance.isChecked = false
                binding.swBillPaidMonthAdvance.visibility = View.INVISIBLE
                binding.switchMonthAdvanceTextView.visibility = View.INVISIBLE
                if (billPaidBeforeLoading != null){
                    if (bill.bill_due_date < Calendar.getInstance().get(Calendar.DAY_OF_MONTH)){ supportActionBar?.setBackgroundDrawable(ColorDrawable(RED)) }
                    else{ supportActionBar?.setBackgroundDrawable(ColorDrawable(YELLOW)) }
                }
            }
        }

        binding.swBillPaidMonthAdvance.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                binding.switchMonthAdvanceTextView.setText(resources.getString(R.string.switch_paid_month_advance))
            } else{
                binding.switchMonthAdvanceTextView.setText(resources.getString(R.string.switch_not_paid_month_advance))
            }
        }

        binding.buttonSave.setOnClickListener {
            when {
                binding.editBillName.editText!!.text.isEmpty() -> Toast.makeText(this,"Bill name cannot be empty", Toast.LENGTH_LONG).show()
                binding.editBillAmount.editText!!.text.isEmpty() -> Toast.makeText(this,"Bill amount cannot be empty", Toast.LENGTH_LONG).show()
                else -> {
                    var billAmount = binding.editBillAmount.editText!!.text.toString()
                    billAmount = billAmount.replace("\$", "")
                    billAmount = billAmount.replace(",", "")
                    val amount = billAmount.toDouble()
                    // +1 is added to the daySpinner because the index starts at 0 but the spinner is for the due date
                    bill = Bill(edit_bill_name.editText?.text.toString(), amount, binding.spinnerDays.selectedItemPosition+1, binding.swBillPaid.isChecked, binding.swBillPaidMonthAdvance.isChecked)
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
        }

        // moving the cursor to the end of the name
        binding.editBillName.editText!!.requestFocus()
        binding.editBillName.editText!!.setSelection(binding.editBillName.editText!!.text.length)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId){
        R.id.action_delete_bill -> {
            val deleteDialogBuilder = AlertDialog.Builder(this)
            deleteDialogBuilder.setMessage("Are you sure you want to delete this bill?")
                .setCancelable(false)
                .setPositiveButton("Cancel") { dialog, which -> dialog.cancel() }


                .setNegativeButton("Delete") { dialog, which ->  if (billId != -1) {
                    billViewModel.deleteBill(bill)
                    startActivity(Intent(this,MainActivity::class.java))
                    }
                }

            val alert = deleteDialogBuilder.create()
            alert.setTitle("Confirm Delete")
            alert.show()
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }

    }
}