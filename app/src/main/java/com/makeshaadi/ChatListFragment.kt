package com.makeshaadi


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.makeshaadi.adapter.ChatListAdapter
import com.makeshaadi.models.ChatListItem
import com.makeshaadi.viewmodels.UserViewModel
import kotlinx.android.synthetic.main.fragment_chatlist.*
import kotlinx.android.synthetic.main.fragment_chatlist.view.*

class ChatListFragment : Fragment() {

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

    override fun onResume() {
        super.onResume()

        userVM.currentUser.observe(viewLifecycleOwner, Observer { cUser ->
            if (cUser.conversations.size == 0) {
                chatListProgress.visibility = View.GONE
                chatPhImg.visibility = View.VISIBLE
                chatlistText.visibility = View.VISIBLE
            } else {
                userVM.getConversationsByIds(cUser.conversations)
                    .observe(viewLifecycleOwner, Observer {
                        chatlistText.visibility = View.GONE
                        chatListProgress.visibility = View.GONE

                        chatList.clear()
                        it.forEach { map ->
                            val rs = map["readStatus"] as Map<*, *>
                            val name =
                                if (map["p1"] == cUser.uid) map["p2Name"].toString() else map["p1Name"].toString()
                            val photoUrl =
                                if (map["p1"] == cUser.uid) map["p2PhotoUrl"].toString() else map["p1PhotoUrl"].toString()
                            val targetId =
                                if (map["p1"] == cUser.uid) map["p2"].toString() else map["p1"].toString()

                            chatList.add(
                                ChatListItem(
                                    map["id"].toString(),
                                    rs[cUser.uid!!].toString().toBoolean(),
                                    cUser.uid!!,
                                    targetId,
                                    name,
                                    photoUrl,
                                    map["lastMessage"].toString(),
                                    map["lastMessageTime"].toString().toLong()
                                )
                            )
                        }
                        adapter.notifyDataSetChanged()
                    })
            }
        })
    }
}
