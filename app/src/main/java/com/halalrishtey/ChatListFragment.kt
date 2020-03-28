package com.halalrishtey


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.halalrishtey.adapter.ChatListAdapter
import com.halalrishtey.models.ChatListItem
import com.halalrishtey.viewmodels.UserViewModel
import kotlinx.android.synthetic.main.fragment_chatlist.*
import kotlinx.android.synthetic.main.fragment_chatlist.view.*

class ChatsFragment : Fragment() {

    private val userVM: UserViewModel by activityViewModels()

    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var adapter: ChatListAdapter
    private lateinit var chatList: ArrayList<ChatListItem>

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
        v.chatListRV.addItemDecoration(
            DividerItemDecoration(
                v.context,
                DividerItemDecoration.VERTICAL
            )
        );
        return v
    }

    override fun onStart() {
        super.onStart()

        userVM.currentUser.observe(viewLifecycleOwner, Observer { cUser ->
            userVM.getConversationsByIds(cUser.conversations).observe(viewLifecycleOwner, Observer {
                chatListProgress.visibility = View.GONE
                chatList.clear()
                it.forEachIndexed { i, map ->
                    val name =
                        if (map["p1"] == cUser.uid) map["p2Name"].toString() else map["p1Name"].toString()
                    val photoUrl =
                        if (map["p1"] == cUser.uid) map["p2PhotoUrl"].toString() else map["p1PhotoUrl"].toString()

                    chatList.add(
                        ChatListItem(
                            cUser.conversations[i],
                            cUser.uid!!,
                            name,
                            photoUrl,
                            map["lastMessage"].toString(),
                            map["lastMessageTime"].toString().toLong()
                        )
                    )
                }
                adapter.notifyDataSetChanged()
            })
        })
    }
}
