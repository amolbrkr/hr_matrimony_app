package com.halalrishtey

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.halalrishtey.adapter.MessageAdapter
import com.halalrishtey.models.MessageItem
import com.halalrishtey.viewmodels.UserViewModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_chat.*

class ChatActivity : AppCompatActivity() {

    private val userVM: UserViewModel by viewModels()
    private var conversationId: String? = null

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: MessageAdapter
    private lateinit var messages: ArrayList<MessageItem>
    private var lastScrollPos: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        setSupportActionBar(chatToolbar)

        chatToolbar.overflowIcon = ContextCompat.getDrawable(this, R.drawable.ic_more_vert)

        //Get required data from intent
        conversationId = intent.extras?.getString("conversationId")
        val senderId = intent.extras?.getString("senderId")
        val title = intent.extras?.getString("targetName")
        val targetPhotoUrl = intent.extras?.getString("targetPhotoUrl")

        //Setup RV
        messages = ArrayList()
        adapter = MessageAdapter(senderId!!, messages)
        linearLayoutManager = LinearLayoutManager(this)
        messageRV.layoutManager = linearLayoutManager
        messageRV.adapter = adapter


        //Setup toolbar and sendChatBtn
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = ""

        chatTitleTextView.text = title
        sendChatBtn.setOnClickListener {
            var msg = chatTextInp.editText?.text.toString()
            msg = msg.trim()

            if (msg.length > 1 && conversationId != null && senderId != null) {
                Log.d("ChatActivity", "Message sent: $msg")
                userVM.sendMessage(conversationId!!, senderId, msg)

                chatTextInp.editText?.setText("")
            }
        }

        if (targetPhotoUrl != null && targetPhotoUrl.length > 5) {
            Picasso.get().load(targetPhotoUrl)
                .into(chatProfileImgView)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.chat_options_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.option_report -> {
                Log.d("ChatActivity", "Report clicked")
                ReportDialog().show(this.supportFragmentManager, "ReportDialog")
                true
            }

            R.id.option_block -> {
                BlockDialog().show(this.supportFragmentManager, "BlockDialog")
                true
            }

            else -> {
                true
            }
        }
    }

    override fun onStart() {
        super.onStart()

        if (conversationId != null) {
            userVM.observeConversation(conversationId!!).observe(this, Observer {
                val temp = it["messages"] as ArrayList<Map<String, Any>>

                messages.clear()
                for (item in temp) {
                    messages.add(
                        MessageItem(
                            item["senderId"].toString(),
                            item["content"].toString(),
                            item["timestamp"].toString().toLong()
                        )
                    )
                }
                adapter.notifyDataSetChanged()
            })
        }
    }

    override fun onPause() {
        super.onPause()

        lastScrollPos = linearLayoutManager.findFirstVisibleItemPosition()
    }

    override fun onResume() {
        super.onResume()

        messageRV.layoutManager?.scrollToPosition(lastScrollPos)
    }
}
