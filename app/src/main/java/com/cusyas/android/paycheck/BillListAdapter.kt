package com.cusyas.android.paycheck

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cusyas.android.paycheck.BillDatabase.Bill

class BillListAdapter internal constructor(
    context: Context
) : RecyclerView.Adapter<BillListAdapter.BillViewHolder>(), View.OnClickListener{

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var bills = emptyList<Bill>()

    class BillViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val billItemView: TextView = itemView.findViewById(R.id.textView)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillViewHolder{
        val itemView = inflater.inflate(R.layout.recyclerview_item, parent, false)
        itemView.setOnClickListener(this)
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

    override fun onClick(v: View?) {
        val element = bills[v!!.id+1].bill_id
        val intent = Intent(v.context, NewBillActivity::class.java)
        intent.putExtra("billId", element)
        v.context.startActivity(intent)
    }
}