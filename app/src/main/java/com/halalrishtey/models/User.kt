package com.halalrishtey.models

import android.os.Parcelable
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.parcel.Parcelize
import java.util.*

enum class Gender {
    Male,
    Female
}

@Parcelize
data class User(
        val email: String,      //done //Must be first
        var uid: String? = "",      //done
        var displayName: String = "", //done
        var age: Int? = null,          //done
        var photoUrl: String = "",      //done
        var idProofUrl: String = "",        //done
        var phoneNumber: Number = 0,        //done
        var gender: Gender = Gender.Male, //done
        var createdFor: String = "",
        var createdAt: Long = System.currentTimeMillis(),
        var lastSignInAt: Long = System.currentTimeMillis(),
        var isPhoneVerified: Boolean = false,
        var address: String = "",       //done
        var dateOfBirth: Date? = null,      //done
        var height: String = "",            //done
        var education: String = "",
        var occupation: String = "",        //done
        var workExperience: String = "",        //done
        var annualIncome: Number = 0,       //done
        var organizationName: String = "",      //done
        var sect: String = "",      //done
        var dargah: String = "",        //done
        var maritalStatus: String = "",      //done
        var locationLat: Double = 0.0,      //done
        var locationLong: Double = 0.0,     //done
        var countryCode: String = "IN",     //done
        var pincode: String = ""        //done
) : Parcelable {
    constructor(firebaseUser: FirebaseUser) :
            this(uid = firebaseUser.uid, email = firebaseUser.email!!)

    override fun toString(): String {
        return "id: $uid, email: $email, displayName: $displayName"
    }
}