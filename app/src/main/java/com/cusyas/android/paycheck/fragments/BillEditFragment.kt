package com.cusyas.android.paycheck.fragments

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.cusyas.android.paycheck.MainActivity
import com.cusyas.android.paycheck.R
import com.cusyas.android.paycheck.billDatabase.Bill
import com.cusyas.android.paycheck.billDatabase.BillViewModel
import com.cusyas.android.paycheck.databinding.FragmentBillViewBinding
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.activity_new_bill.*
import java.text.NumberFormat
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class BillEditFragment : Fragment() {

    private lateinit var bill: Bill
    private lateinit var billViewModel: BillViewModel
    private var billId: Int = -1

    private lateinit var buttonSave: ExtendedFloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bill_edit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args: BillEditFragmentArgs by navArgs()
        val billId = args.billId
        var billPaidBeforeLoading: Boolean

        buttonSave = requireActivity().findViewById(R.id.button_save)


        val binding = FragmentBillViewBinding.bind(view)

        billViewModel = ViewModelProvider(this).get(BillViewModel::class.java)

        billViewModel.loadById(billId).observe(viewLifecycleOwner, androidx.lifecycle.Observer {
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
                    activity?.actionBar?.setBackgroundDrawable(ColorDrawable(Color.GREEN))
                }
                else{
                    if (bill.bill_due_date < Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) {
                        activity?.actionBar?.setBackgroundDrawable(ColorDrawable(Color.RED))
                    }else{
                        activity?.actionBar?.setBackgroundDrawable(ColorDrawable(Color.YELLOW))
                    }
                }
            }
        })


        binding.editBillAmount.editText!!.addTextChangedListener(object: TextWatcher {
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

                binding.swBillPaidMonthAdvance.visibility = View.VISIBLE
                binding.switchMonthAdvanceTextView.visibility = View.VISIBLE
            } else{
                binding.switchBillPaidTextView.text = resources.getText(R.string.switch_not_paid)
                binding.swBillPaidMonthAdvance.isChecked = false
                binding.swBillPaidMonthAdvance.visibility = View.INVISIBLE
                binding.switchMonthAdvanceTextView.visibility = View.INVISIBLE
            }
        }

        binding.swBillPaidMonthAdvance.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                binding.switchMonthAdvanceTextView.setText(resources.getString(R.string.switch_paid_month_advance))
            } else{
                binding.switchMonthAdvanceTextView.setText(resources.getString(R.string.switch_not_paid_month_advance))
            }
        }

        buttonSave.setOnClickListener {
            when {
                binding.editBillName.editText!!.text.isEmpty() -> Toast.makeText(activity?.applicationContext ,"Bill name cannot be empty", Toast.LENGTH_LONG).show()
                binding.editBillAmount.editText!!.text.isEmpty() -> Toast.makeText(activity?.applicationContext ,"Bill amount cannot be empty", Toast.LENGTH_LONG).show()
                else -> {
                    var billAmount = binding.editBillAmount.editText!!.text.toString()
                    billAmount = billAmount.replace("\$", "")
                    billAmount = billAmount.replace(",", "")
                    val amount = billAmount.toDouble()
                    // +1 is added to the daySpinner because the index starts at 0 but the spinner is for the due date
                    bill.bill_name = binding.editBillName.editText?.text.toString()
                    bill.bill_amount = amount
                    bill.bill_due_date = binding.spinnerDays.selectedItemPosition+1
                    bill.bill_paid = binding.swBillPaid.isChecked
                    bill.bill_paid_month_advance = binding.swBillPaidMonthAdvance.isChecked

                    //bill = Bill(binding.editBillName.editText?.text.toString(), amount, binding.spinnerDays.selectedItemPosition+1, binding.swBillPaid.isChecked, binding.swBillPaidMonthAdvance.isChecked)

                    billViewModel.updateBill(bill)

                    val action = BillEditFragmentDirections.actionBillEditFragmentToBillListFragment()
                    findNavController().navigate(action)
                }
            }
        }

        // moving the cursor to the end of the name
        binding.editBillName.editText!!.requestFocus()
        binding.editBillName.editText!!.setSelection(binding.editBillName.editText!!.text.length)

        setHasOptionsMenu(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }


    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId){
        R.id.action_delete_bill -> {
            val deleteDialogBuilder = AlertDialog.Builder(context)
            deleteDialogBuilder.setMessage("Are you sure you want to delete this bill?")
                .setCancelable(false)
                .setPositiveButton("Cancel") { dialog, which -> dialog.cancel() }


                .setNegativeButton("Delete") { dialog, which ->
                    billViewModel.deleteBill(bill)
                    findNavController().navigate(R.id.action_billEditFragment_to_billListFragment)
                }

            val alert = deleteDialogBuilder.create()
            alert.setTitle("Confirm Delete")
            alert.show()
            true
        }
        android.R.id.home -> findNavController().navigateUp()
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_edit_bill, menu)
    }

}
