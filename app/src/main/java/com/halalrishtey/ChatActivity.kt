package com.halalrishtey

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.halalrishtey.viewmodels.UserViewModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_chat.*

class ChatActivity : AppCompatActivity() {

    private val userVM: UserViewModel by viewModels()
    private var conversationId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        setSupportActionBar(chatToolbar)

        conversationId = intent.extras?.getString("conversationId")
        val senderId = intent.extras?.getString("senderId")
        val title = intent.extras?.getString("targetName")
        val targetPhotoUrl = intent.extras?.getString("targetPhotoUrl")

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

    override fun onStart() {
        super.onStart()

        if (conversationId != null) {
            userVM.observeConversation(conversationId!!).observe(this, Observer {
                chatTextView.text = it["messages"].toString()
            })
        }
    }
}
