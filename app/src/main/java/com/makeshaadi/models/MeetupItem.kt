package com.makeshaadi.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

enum class MeetupStatus {
    Scheduled,
    Rescheduled,
    Done,
    Cancelled
}

@Parcelize
data class MeetupItem(
    var meetupId: String = "",
    val sourceId: String = "",
    val sourcePhoto: String = "",
    val sourceName: String = "",
    val targetId: String = "",
    val targetPhoto: String = "",
    val targetName: String = "",
    val timestamp: Long,
    var status: MeetupStatus = MeetupStatus.Scheduled,
    val date: Long,
    val locLat: Double = 0.0,
    val locLong: Double = 0.0,
    val address: String = "",
    var isApproved: Boolean = false
) : Parcelable