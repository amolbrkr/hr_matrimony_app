package com.halalrishtey.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.halalrishtey.ChatActivity
import com.halalrishtey.CustomUtils
import com.halalrishtey.R
import com.halalrishtey.models.ChatListItem
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.chat_item.view.*

class ChatListAdapter(
    private val context: Context,
    private val chatList: ArrayList<ChatListItem>
) :
    RecyclerView.Adapter<ChatListAdapter.ChatListVH>() {

    class ChatListVH(v: View) : RecyclerView.ViewHolder(v)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatListVH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.chat_item, parent, false)
        return ChatListVH(view)
    }

    override fun onBindViewHolder(holder: ChatListVH, position: Int) {
        holder.itemView.chatItemTitleText.text = chatList[position].displayName
        holder.itemView.chatItemSubtitleText.text = chatList[position].lastMsg
        holder.itemView.chatTimeText.text =
            CustomUtils.genTimeString(chatList[position].lastMsgTime)

        if (chatList[position].readStatus) holder.itemView.chatUnreadBadge.visibility = View.GONE
        else holder.itemView.chatUnreadBadge.visibility = View.VISIBLE

        if (chatList[position].photoUrl.length > 5)
            Picasso.get().load(chatList[position].photoUrl)
                .into(holder.itemView.chatProfileImg)

        holder.itemView.setOnClickListener { v ->
            val i = Intent(context, ChatActivity::class.java)
            i.putExtra("conversationId", chatList[position].conversationId)
            i.putExtra("currentId", chatList[position].currentId)
            i.putExtra("targetId", chatList[position].targetId)
            i.putExtra("targetPhotoUrl", chatList[position].photoUrl)
            i.putExtra("targetName", chatList[position].displayName)
            v.context.startActivity(i)
        }
    }

    override fun getItemCount() = chatList.size
}