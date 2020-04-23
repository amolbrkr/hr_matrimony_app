package com.halalrishtey.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.halalrishtey.CustomUtils
import com.halalrishtey.R
import com.halalrishtey.models.MessageItem
import kotlinx.android.synthetic.main.in_message.view.*
import kotlinx.android.synthetic.main.out_message.view.*

class MessageAdapter(private val senderId: String, private val data: List<MessageItem>) :
    RecyclerView.Adapter<MessageAdapter.MessageVH>() {

    class MessageVH(v: View) : RecyclerView.ViewHolder(v)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageVH {
        val inflater = LayoutInflater.from(parent.context)

        val inflatedView = if (viewType == 0)
            inflater.inflate(R.layout.in_message, parent, false)
        else if (viewType == 2) inflater.inflate(R.layout.meetup_prompt, parent, false)
        else inflater.inflate(R.layout.out_message, parent, false)

        return MessageVH(inflatedView)
    }

    override fun onBindViewHolder(holder: MessageVH, position: Int) {

        if (holder.itemViewType == 0) {
            holder.itemView.inMsgText.text = data[position].content
            holder.itemView.inMsgTimeText.text = CustomUtils.genTimeString(data[position].timestamp)
        } else {
            holder.itemView.outMsgText.text = data[position].content
            holder.itemView.outMsgTimeText.text =
                CustomUtils.genTimeString(data[position].timestamp)
        }
    }

    override fun getItemViewType(position: Int): Int {
        //On every multiple of a value show meetup prompt
        if (position % 10 == 0) return 2

        return if (data[position].senderId == senderId) 1 else 0
    }

    override fun getItemCount() = data.size
}