package com.halalrishtey.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.halalrishtey.R
import com.halalrishtey.models.MessageItem
import kotlinx.android.synthetic.main.in_message.view.*
import kotlinx.android.synthetic.main.out_message.view.*
import java.util.*

class MessageAdapter(private val senderId: String, private val data: List<MessageItem>) :
    RecyclerView.Adapter<MessageAdapter.MessageVH>() {

    class MessageVH(v: View) : RecyclerView.ViewHolder(v)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageVH {
        val inflater = LayoutInflater.from(parent.context)

        val inflatedView = if (viewType == 0)
            inflater.inflate(R.layout.in_message, parent, false)
        else inflater.inflate(R.layout.out_message, parent, false)

        return MessageVH(inflatedView)
    }

    override fun onBindViewHolder(holder: MessageVH, position: Int) {

        val cal = Calendar.getInstance()
        cal.timeInMillis = data[position].timestamp
        val msgTime =
            "${cal.get(Calendar.HOUR_OF_DAY)}:${cal.get(Calendar.MINUTE)}"

        if (holder.itemViewType == 0) {
            holder.itemView.inMsgText.text = data[position].content
            holder.itemView.inMsgTimeText.text = msgTime
        } else {
            holder.itemView.outMsgText.text = data[position].content
            holder.itemView.outMsgTimeText.text = msgTime
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (data[position].senderId == senderId) 1 else 0
    }

    override fun getItemCount() = data.size
}