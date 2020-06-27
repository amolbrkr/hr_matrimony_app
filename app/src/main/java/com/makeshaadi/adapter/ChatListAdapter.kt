package com.makeshaadi.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.makeshaadi.ChatActivity
import com.makeshaadi.CustomUtils
import com.makeshaadi.R
import com.makeshaadi.models.ChatListItem
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
        holder.itemView.apply {
            chatItemTitleText.text = chatList[position].displayName
            chatItemSubtitleText.text = chatList[position].lastMsg
            chatTimeText.text = CustomUtils.genTimeString(chatList[position].lastMsgTime)

            if (chatList[position].readStatus) chatUnreadBadge.visibility = View.GONE
            else chatUnreadBadge.visibility = View.VISIBLE

            if (chatList[position].photoUrl.length > 5)
                Picasso.get().load(chatList[position].photoUrl).into(chatProfileImg)

            setOnClickListener { v ->
                val i = Intent(context, ChatActivity::class.java)
                i.apply {
                    putExtra("conversationId", chatList[position].conversationId)
                    putExtra("currentId", chatList[position].currentId)
                    putExtra("targetId", chatList[position].targetId)
                    putExtra("targetName", chatList[position].displayName)
                    putExtra("targetPhotoUrl", chatList[position].photoUrl)
                    v.context.startActivity(this)
                }
            }
        }
    }

    override fun getItemCount() = chatList.size
}