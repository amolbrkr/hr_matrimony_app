package com.halalrishtey.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.halalrishtey.R
import com.halalrishtey.models.PlanItem
import kotlinx.android.synthetic.main.plan_item.view.*

class PlansAdapter(
    private val planList: ArrayList<PlanItem>
) : RecyclerView.Adapter<PlansAdapter.PlansVH>() {

    class PlansVH(v: View) : RecyclerView.ViewHolder(v)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlansVH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.plan_item, parent, false)
        return PlansVH(v)
    }

    override fun onBindViewHolder(holder: PlansVH, position: Int) {
        holder.itemView.apply {
            pNameText.text = planList[position].name
            pAPText.text = "₹" + planList[position].actualPrice
            pAPText.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            pDPText.text =
                "₹ ${planList[position].discountPrice} for ${planList[position].validity} month"

            pBuyBtn.setOnClickListener {
                Toast.makeText(this.context, "Coming Soon!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    override fun getItemCount() = planList.size;
}