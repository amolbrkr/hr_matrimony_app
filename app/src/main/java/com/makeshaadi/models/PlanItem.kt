package com.makeshaadi.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PlanItem(
    val id: String,
    val actualPrice: String,
    var chatCount: Int,
    var dcCount: Int,
    val discountPrice: Int,
    var meetupCount: Int,
    val name: String,
    val validity: Int
) : Parcelable {
    override fun toString(): String {
        return "Plan #$id, $name, $validity months"
    }
}