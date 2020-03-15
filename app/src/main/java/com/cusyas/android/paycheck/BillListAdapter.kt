package com.cusyas.android.paycheck

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Color.*
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.ShapeDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.cusyas.android.paycheck.BillDatabase.Bill
import com.cusyas.android.paycheck.Utils.BillDueDateDistance
import java.text.NumberFormat
import java.util.*

class BillListAdapter internal constructor(
    context: Context
) : RecyclerView.Adapter<BillListAdapter.BillViewHolder>(){

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
        val context = holder.billAmountItemView.context
        holder.billNameItemView.text = bills[position].bill_name
        holder.billDueDateItemView.text = (context.getText(R.string.bill_due_on).toString() + bills[position].bill_due_date.toString())
        holder.billAmountItemView.text = context.getText(R.string.bill_amount_due).toString() + NumberFormat.getCurrencyInstance().format(bills[position].bill_amount)
        val daysUntilDuePercentage = BillDueDateDistance.getColorMix(bills[position].bill_due_date)
        // check if the bill is paid, setting the background accordingly
        if (bills[position].bill_paid){
            //holder.itemView.setBackgroundColor(GREEN)
            val colors: IntArray = intArrayOf(ContextCompat.getColor(context,R.color.billGreen), ContextCompat.getColor(context,R.color.billYellow))
            val customDrawable = BillColorBarDrawable(colors,daysUntilDuePercentage)
            holder.itemView.background = customDrawable
        }
        else{
            if (bills[position].bill_due_date < Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) {
                holder.itemView.setBackgroundColor(RED)
            }else{
                val colors: IntArray = intArrayOf(ContextCompat.getColor(context,R.color.billYellow), ContextCompat.getColor(context,R.color.billRed))
                val customDrawable = BillColorBarDrawable(colors,daysUntilDuePercentage)
                holder.itemView.background = customDrawable
            }
        }
    }

    internal fun setBills(bills: List<Bill>){
        this.bills = bills
        notifyDataSetChanged()
    }

    override fun getItemCount() = bills.size


    private fun onClick(position: Int, v: View) {

        val intent = Intent(v.context, NewBillActivity::class.java)
        intent.putExtra("billId", bills[position].bill_id)
        v.context.startActivity(intent)

    }
}