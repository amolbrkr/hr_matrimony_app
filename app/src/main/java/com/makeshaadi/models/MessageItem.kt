package com.makeshaadi.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MessageItem(
    val senderId: String,
    val content: String,
    val timestamp: Long
) : Parcelable