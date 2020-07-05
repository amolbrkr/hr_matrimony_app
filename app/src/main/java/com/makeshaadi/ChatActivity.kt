package com.makeshaadi

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.makeshaadi.adapter.MessageAdapter
import com.makeshaadi.models.MessageItem
import com.makeshaadi.viewmodels.UserViewModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_chat.*


class ChatActivity : AppCompatActivity() {

    private val userVM: UserViewModel by viewModels()
    private var conversationId: String? = null
    private var currentUserId: String? = null
    private var targetUserId: String? = null

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
        currentUserId = intent.extras?.getString("currentId")
        targetUserId = intent.extras?.getString("targetId")
        val title = intent.extras?.getString("targetName")
        val targetPhotoUrl = intent.extras?.getString("targetPhotoUrl")

        //Setup RV
        messages = ArrayList()
        adapter = MessageAdapter(currentUserId!!, messages)
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

            if (msg.length > 1 && conversationId != null && currentUserId != null) {
                Log.d("ChatActivity", "Message sent: $msg")
                userVM.sendMessage(conversationId!!, currentUserId!!, targetUserId!!, msg)

                chatTextInp.editText?.setText("")
            }
        }

        if (targetPhotoUrl != null && targetPhotoUrl.length > 5) {
            Picasso.get().load(targetPhotoUrl)
                .into(chatProfileImgView)
        }

        chatToolbarLayout.setOnClickListener {
            CustomUtils.openUserDetails(this, targetUserId!!)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.chat_options_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }

            R.id.option_report -> {
                ReportDialog(
                    currentUserId!!,
                    targetUserId!!
                ).show(this.supportFragmentManager, "ReportDialog")
                true
            }

            R.id.option_block -> {
                BlockDialog(
                    currentUserId!!,
                    targetUserId!!
                ).show(this.supportFragmentManager, "BlockDialog")
                true
            }

            else -> {
                true
            }
        }
    }

    override fun onStart() {
        super.onStart()
        messageRV.addOnLayoutChangeListener { _, _, _, _, bottom, _, _, _, oldBottom ->
            if (bottom < oldBottom) {
                messageRV.postDelayed(Runnable {
                    messageRV.adapter?.itemCount?.minus(1)?.let {
                        if (it < messages.size) {
                            messageRV.smoothScrollToPosition(it)
                        }
                    }
                }, 100)
            }
        }

        if (conversationId != null) {
            userVM.updateReadStatus(conversationId!!, currentUserId!!)
            userVM.observeConversation(conversationId!!).observe(this, Observer {
                var temp = ArrayList<Map<String, Any>>()
                if (it != null)
                    temp = it["messages"] as ArrayList<Map<String, Any>>

                messages.clear()
                temp.map { t ->
                    messages.add(
                        MessageItem(
                            t["senderId"].toString(),
                            t["content"].toString(),
                            t["timestamp"].toString().toLong()
                        )
                    )
                }
                adapter.notifyDataSetChanged()
                messageRV.scrollToPosition(messages.size - 1)
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
