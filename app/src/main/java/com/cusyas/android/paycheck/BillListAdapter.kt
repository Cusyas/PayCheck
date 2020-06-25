package com.cusyas.android.paycheck

import android.content.Context
import android.content.Intent
import android.graphics.Color.*
import android.graphics.drawable.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.cusyas.android.paycheck.billDatabase.Bill
import com.cusyas.android.paycheck.fragments.BillEditFragment
import com.cusyas.android.paycheck.fragments.BillListFragmentDirections
import com.cusyas.android.paycheck.utils.BillDueDateDistance
import java.text.NumberFormat
import java.util.*

class BillListAdapter internal constructor(
    context: Context
) : RecyclerView.Adapter<BillListAdapter.BillViewHolder>(){

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private lateinit var customDrawable: BillColorBarDrawable
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
        holder.billAmountItemView.text = context.getString(R.string.bill_amount_due).toString() + NumberFormat.getCurrencyInstance().format(bills[position].bill_amount)
        val daysUntilDuePercentage = BillDueDateDistance.getColorMix(bills[position].bill_due_date)
        // check if the bill is paid, setting the background accordingly


        if (bills[position].bill_paid){
            if (bills[position].bill_paid_month_advance){
                holder.itemView.background = ColorDrawable(context.getColor(R.color.billGreen))
            }
            else {
                if (bills[position].bill_due_date < Calendar.getInstance()
                        .get(Calendar.DAY_OF_MONTH)
                ) {
                    val colors: IntArray = intArrayOf(
                        ContextCompat.getColor(context, R.color.billYellow),
                        ContextCompat.getColor(context, R.color.billRed)
                    )
                    customDrawable = BillColorBarDrawable(colors, daysUntilDuePercentage)
                } else {
                    val colors: IntArray = intArrayOf(
                        ContextCompat.getColor(context, R.color.billGreen),
                        ContextCompat.getColor(context, R.color.billYellow)
                    )
                    customDrawable = BillColorBarDrawable(colors, daysUntilDuePercentage)
                }
                holder.itemView.background = customDrawable
            }
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

        val action = BillListFragmentDirections.actionBillListFragmentToBillEditFragment(bills[position].bill_id)
        v.findNavController().navigate(action)
        //intent.putExtra(v.context.getString(R.string.bill_id), bills[position].bill_id)

    }
}