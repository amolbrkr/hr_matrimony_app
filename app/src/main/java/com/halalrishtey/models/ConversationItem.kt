package com.halalrishtey.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ConversationItem(
    val conversationId: String,
    val displayName: String,
    val photoUrl: String,
    val lastMessage: String = "",
    val initialTimestamp: Long
) : Parcelable