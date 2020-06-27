package com.makeshaadi.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.makeshaadi.R
import com.makeshaadi.models.PlanItem
import kotlinx.android.synthetic.main.plan_item.view.*
import org.json.JSONObject

class PlansAdapter(
    private val planList: ArrayList<PlanItem>,
    private val currentUid: String,
    private val initPaymentCb: (PlanItem, JSONObject) -> Unit
) : RecyclerView.Adapter<PlansAdapter.PlansVH>() {

    class PlansVH(v: View) : RecyclerView.ViewHolder(v)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlansVH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.plan_item, parent, false)
        return PlansVH(v)
    }

    override fun onBindViewHolder(holder: PlansVH, position: Int) {
        holder.itemView.apply {
            pNameText.text = planList[position].name
            pDetailText.text =
                "Messages: ${planList[position].chatCount}, Direct Contacts: ${planList[position].dcCount} \nMeetups: ${planList[position].meetupCount}"
            pAPText.text = "₹" + planList[position].actualPrice
            pAPText.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            pDPText.text =
                "₹ ${planList[position].discountPrice} for ${planList[position].validity} month"

            pBuyBtn.setOnClickListener {
                val options = JSONObject()
                options.put("name", "Nikahal Services Pvt Ltd")
                options.put(
                    "description",
                    "Reference No: ${System.currentTimeMillis()}_${currentUid}_${planList[position].id}"
                )
                options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png")
//                options.put("order_id", "order_9A33XWu170gUtm")
                options.put("currency", "INR")

                //Amount is always passed in currency subunits (PAISE)  * Eg: "500" = INR 5.00
                options.put("amount", "${planList[position].discountPrice * 100}")
                initPaymentCb(planList[position], options)
            }
        }
    }

    override fun getItemCount() = planList.size;
}