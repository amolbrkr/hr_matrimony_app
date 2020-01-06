package com.halalrishtey.models

import android.location.Address
import android.os.Parcelable
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.parcel.Parcelize
import java.util.*


data class Height(var feet: Int, var inch: Int)

enum class Gender {
    Male,
    Female
}

enum class CreatedFor {
    Myself,
    Relative,
    Son,
    Daughter,
    Brother,
    Sister;

    private var cFor: String = ""

    private fun CreatedFor(value: String) {
        this.cFor = value
    }

    override fun toString(): String {
        return this.cFor
    }
}

enum class Sect {
    Sunnat,
    Tableeghee,
    Hadees,
    Shia,
    Others;

    private var s: String = ""

    private fun Sect(value: String) {
        this.s = value
    }

    override fun toString(): String {
        return this.s
    }
}

enum class MaritalStatus {
    Unmarried,
    Divorced,
    Seperated,
    Widowed,
    Second
}

@Parcelize
data class User(
    val email: String,
    var uid: String? = "",
    var displayName: String = "",
    var photoUrl: String = "",
    var aadharPhotoUrl: String = "",
    var phoneNumber: Number = 0,
    var gender: Gender = Gender.Male,
    var createdFor: CreatedFor = CreatedFor.Myself,
    var createdAt: Long = System.currentTimeMillis(),
    var lastSignInAt: Long = System.currentTimeMillis(),
    var isEmailVerified: Boolean = false,
    var address: Address? = null,
    var age: Int = 24,
    var pinCode: Int = 0,
    var dateOfBirth: Date? = null,
    var heightFeet: String = "",
    var heightInch: String = "",
    var education: String = "",
    var occupation: String = "",
    var workExperience: String = "",
    var annualIncome: Number = 0,
    var organizationName: String = "",
    var sect: Sect = Sect.Others,
    var maritalStatus: MaritalStatus = MaritalStatus.Unmarried
) : Parcelable {
    constructor(firebaseUser: FirebaseUser) :
            this(firebaseUser.uid, firebaseUser.email)

    override fun toString(): String {
        return "id: $uid, email: $email"
    }
}