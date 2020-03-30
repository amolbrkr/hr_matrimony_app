package com.halalrishtey

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
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
    private var currentUserId: String? = null
    private var targetUserId: String? = null

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: MessageAdapter
    private lateinit var messages: ArrayList<MessageItem>
    private var lastScrollPos: Int = 0

    fun blockUser(ctx: Context, currentId: String, targetId: String) {
        userVM.blockUser(currentId, targetId).observe(this, Observer {
            if (it.contains("Successfully")) {
                this.finish()
            } else {
                Toast.makeText(ctx, "Error: $it", Toast.LENGTH_SHORT).show()
            }
        })
    }

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
                userVM.sendMessage(conversationId!!, currentUserId!!, msg)

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

        if (conversationId != null) {
            userVM.observeConversation(conversationId!!).observe(this, Observer {
                val temp = it["messages"] as ArrayList<Map<String, Any>>

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
