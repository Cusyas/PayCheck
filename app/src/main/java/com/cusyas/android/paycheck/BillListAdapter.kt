package com.cusyas.android.paycheck

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.recyclerview.widget.RecyclerView
import com.cusyas.android.paycheck.Database.Bill

class BillListAdapter internal constructor(
    context: Context
) : RecyclerView.Adapter<BillListAdapter.BillViewHolder>(){

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var bills = emptyList<Bill>()

    inner class BillViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val billItemView: TextView = itemView.findViewById(R.id.textView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillViewHolder{
        val itemView = inflater.inflate(R.layout.recyclerview_item, parent, false)
        return BillViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BillViewHolder, position: Int) {
        val current = bills[position]
        holder.billItemView.text = current.bill_name
    }

    internal fun setBills(bills: List<Bill>){
        this.bills = bills
        notifyDataSetChanged()
    }

    override fun getItemCount() = bills.size
}