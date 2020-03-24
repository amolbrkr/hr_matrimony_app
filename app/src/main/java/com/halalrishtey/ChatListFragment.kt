package com.halalrishtey


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.halalrishtey.adapter.ChatListAdapter
import com.halalrishtey.models.ConversationItem
import com.halalrishtey.viewmodels.UserViewModel
import kotlinx.android.synthetic.main.fragment_chatlist.view.*

class ChatsFragment : Fragment() {

    private val userVM: UserViewModel by viewModels()

    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var adapter: ChatListAdapter
    private lateinit var chatList: ArrayList<ConversationItem>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_chatlist, container, false)

        chatList = ArrayList()
        adapter = ChatListAdapter(requireContext(), chatList)
        layoutManager = LinearLayoutManager(context)

        v.chatListRV.layoutManager = layoutManager
        v.chatListRV.adapter = adapter
        return v
    }

    override fun onStart() {
        super.onStart()

        userVM.observeUser(userVM.currentUserId.value!!).observe(viewLifecycleOwner, Observer { u ->
            chatList.clear()
            u.conversations.forEach {
                chatList.add(
                    ConversationItem(
                        it["conversationId"].toString(),
                        it["senderId"].toString(),
                        it["displayName"].toString(),
                        it["photoUrl"].toString(),
                        it["lastMessage"].toString(),
                        it["initialTimestamp"].toString().toLong()
                    )
                )
            }
            adapter.notifyDataSetChanged()
        })
    }
}
