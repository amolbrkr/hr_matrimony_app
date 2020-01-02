package com.halalrishtey.models

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.android.parcel.Parcelize
import java.util.*

enum class Gender {
    MALE,
    FEMALE
}

enum class CreatedFor {
    MYSELF,
    RELATIVE,
    SON,
    DAUGHTER,
    BROTHER,
    SISTER
}

enum class Sect {
    SUNNAT,
    TABLEEGHE,
    HADEES,
    SHIA,
    OTHERS
}

enum class MaritalStatus {
    UNMARRIED,
    DIVORCED,
    SEPARATED,
    WIDOWED,
    SECOND
}

@Parcelize
data class User(
    val uid: String,
    val displayName: String,
    val email: String,
    val photoUrl: String = "",
    val phoneNumber: Number = 0,
    val gender: Gender = Gender.FEMALE,
    val createdFor: CreatedFor = CreatedFor.MYSELF,
    val createdAt: Timestamp = Timestamp(0, 0),
    val lastSignInAt: Timestamp = Timestamp(0, 0),
    val isEmailVerified: Boolean = false,
    val age: Int = 25,
    val country: String = "India",
    val state: String = "",
    val city: String = "",
    val pinCode: Int = 0,
    val dateOfBirth: Date? = null,
    val height: Float = 5.7f,
    val education: String = "",
    val occupation: String = "",
    val annualIncome: Number = 0,
    val organizationName: String = "",
    val sect: Sect = Sect.OTHERS,
    val maritalStatus: MaritalStatus = MaritalStatus.UNMARRIED
) : Parcelable