package com.cusyas.android.paycheck.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cusyas.android.paycheck.BillListAdapter
import com.cusyas.android.paycheck.NewBillActivity
import com.cusyas.android.paycheck.R
import com.cusyas.android.paycheck.billDatabase.BillViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.transition.Hold
import java.text.NumberFormat
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class BillListFragment : Fragment() {

    private var totalDue: Double = 0.0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bill_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        var totalDueTextView: TextView? = activity?.findViewById(R.id.tv_total_due)
        val recyclerView = activity?.findViewById<RecyclerView>(R.id.recyclerview)
        val adapter = activity?.applicationContext?.let { BillListAdapter(it) }
        recyclerView?.adapter = adapter
        recyclerView?.layoutManager = LinearLayoutManager(activity?.applicationContext)

        var billViewModel = ViewModelProvider(this).get(BillViewModel::class.java)

        billViewModel!!.allBills.observe(viewLifecycleOwner, androidx.lifecycle.Observer { bills ->
            bills?.let { list ->
                adapter?.setBills(list)
                var payReset = false
                val sharedPref = activity?.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
                val recentPaidMonth = sharedPref?.getInt(getString(R.string.most_recent_paid_month_key), -1)
                val calendarInstance = Calendar.getInstance()
                if(recentPaidMonth != calendarInstance.get( Calendar.MONTH)){
                    payReset = true
                    sharedPref?.edit()?.putInt(getString(R.string.most_recent_paid_month_key), calendarInstance.get(
                        Calendar.MONTH))?.apply()
                }else { payReset = false }
                list.forEach {
                    if (payReset && it.bill_paid_month_advance){
                        it.bill_paid_month_advance = false
                        it.bill_paid = true
                        billViewModel?.updateBill(it)
                    }else if (payReset && !it.bill_paid_month_advance){
                        it.bill_paid = false
                        billViewModel?.updateBill(it)
                    }
                    else if (!payReset && !it.bill_paid){
                        totalDue += it.bill_amount
                    }
                }
                totalDueTextView?.text = resources.getText(R.string.bill_total_amount_due).toString() + ("\n" + NumberFormat.getCurrencyInstance().format(totalDue))
            }
        })


        val fab = activity?.findViewById<FloatingActionButton>(R.id.fab)
        fab?.setOnClickListener {
            val extras = FragmentNavigatorExtras(fab to "fab_element_container")
            it.findNavController().navigate(R.id.action_billListFragment_to_newBillFragment, null, null, extras)
        }
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(this){
            requireActivity().finishAffinity()
        }

        exitTransition = Hold()
    }
}
