package com.cusyas.android.paycheck

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Color.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cusyas.android.paycheck.BillDatabase.Bill
import java.text.NumberFormat

class BillListAdapter internal constructor(
    context: Context
) : RecyclerView.Adapter<BillListAdapter.BillViewHolder>(){

    interface OnItemClickListener{
        fun onItemClicked(position: Int,view: View)
    }

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var bills = emptyList<Bill>()

    class BillViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val billNameItemView: TextView = itemView.findViewById(R.id.tv_bill_name)
        val billDueDateItemView: TextView = itemView.findViewById(R.id.tv_bill_due_date)
        val billAmountItemView: TextView = itemView.findViewById(R.id.tv_bill_amount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillViewHolder{
        val itemView = inflater.inflate(R.layout.recyclerview_item, parent, false)
        val viewHolder = BillViewHolder(itemView)
        itemView.setOnClickListener {
            onClick(viewHolder.layoutPosition, itemView)
        }
            return viewHolder
        }


    override fun onBindViewHolder(holder: BillViewHolder, position: Int) {
        holder.billNameItemView.text = bills[position].bill_name
        holder.billDueDateItemView.append(bills[position].bill_due_date.toString())
        holder.billAmountItemView.append(NumberFormat.getCurrencyInstance().format(bills[position].bill_amount))

        // check if the bill is paid, setting the background accordingly
        if (bills[position].bill_paid){
            holder.itemView.setBackgroundColor(GREEN)
        }
        else{
            holder.itemView.setBackgroundColor(RED)
        }
    }

    internal fun setBills(bills: List<Bill>){
        this.bills = bills
        notifyDataSetChanged()
    }

    override fun getItemCount() = bills.size


    fun onClick(position: Int, v: View) {

        val intent = Intent(v.context, NewBillActivity::class.java)
        intent.putExtra("billId", bills[position].bill_id)
        v.context.startActivity(intent)

    }
}