package com.makeshaadi.models

import android.os.Parcelable
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.parcel.Parcelize

enum class Gender {
    Male,
    Female
}

enum class ProfilePicVisibility {
    OnlyMe,
    Public,
    Interests
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
    var createdFor: String = "",     //done
    var createdAt: Long = System.currentTimeMillis(),
    var lastSignInAt: Long = System.currentTimeMillis(),
    var address: String = "",       //done
    var dateOfBirth: Long = 0,      //done
    var height: String = "",            //done
    var qualification: String = "",         //done
    var qualDetails: String = "",
    var workLocation: String = "",        //done
    var annualIncome: String = "",       //done
    var organizationName: String = "",      //done
    var sect: String = "",      //done
    var dargah: String = "",        //done
    var maritalStatus: String = "",      //done
    var locationLat: Double = 0.0,      //done
    var locationLong: Double = 0.0,     //done
    var countryCode: String = "IN",     //done
    var pincode: String = "",        //done
    var isOTPVerified: Boolean = false,     //done
    var isIdProofVerified: Boolean = false,
    var countryCallingCode: String = "",     //done
    var interestedProfiles: ArrayList<String> = ArrayList(),
    var registrationToken: String = "",
    var conversations: ArrayList<String> = ArrayList(),
    var blockList: ArrayList<String> = ArrayList(),
    var meetupList: ArrayList<String> = ArrayList(),
    var bio: String = "",
    var numBrothers: String = "",
    var isSuspended: Boolean = false,
    var numSisters: String = "",
    var fathersName: String = "",
    var fathersJob: String = "",
    var profilePicVisibility: ProfilePicVisibility = ProfilePicVisibility.Public,
    var photoList: ArrayList<String> = ArrayList(),
    var currentPlan: PlanItem? = null,
    var planStart: Long = 0,
    var directContacts: ArrayList<String> = ArrayList()
) : Parcelable {
    constructor(firebaseUser: FirebaseUser) :
            this(uid = firebaseUser.uid, email = firebaseUser.email!!)

    override fun toString(): String {
        return "id: $uid, email: $email, displayName: $displayName, plan: $currentPlan"
    }
}