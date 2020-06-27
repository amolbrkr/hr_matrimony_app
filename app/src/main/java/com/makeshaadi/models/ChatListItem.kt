package com.makeshaadi.models

data class ChatListItem(
    val conversationId: String,
    val readStatus: Boolean,
    val currentId: String,
    val targetId: String,//Put currentUserId in this
    val displayName: String,
    val photoUrl: String,
    val lastMsg: String,
    val lastMsgTime: Long
)
