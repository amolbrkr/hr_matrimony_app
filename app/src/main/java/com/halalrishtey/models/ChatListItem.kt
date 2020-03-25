package com.halalrishtey.models

data class ChatListItem(
    val conversationId: String,
    val senderId: String, //Put currentUserId in this
    val displayName: String,
    val photoUrl: String,
    val lastMsg: String,
    val lastMsgTime: Long
)
