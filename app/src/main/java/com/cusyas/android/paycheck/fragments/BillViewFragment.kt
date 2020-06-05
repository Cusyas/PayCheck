package com.cusyas.android.paycheck.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment

import com.cusyas.android.paycheck.R

/**
 * A simple [Fragment] subclass.
 */
class BillViewFragment : Fragment() {

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        activity?.menuInflater?.inflate(R.menu.menu_main, menu)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bill_view, container, false)
    }

}
