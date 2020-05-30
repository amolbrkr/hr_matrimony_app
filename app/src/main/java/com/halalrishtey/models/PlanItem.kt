package com.halalrishtey.models

data class PlanItem(
    val id: String,
    val actualPrice: String,
    val chatCount: Int,
    val dcCount: Int,
    val discountPrice: Int,
    val meetupCount: Int,
    val name: String,
    val validity: Int
)