package com.makeshaadi.viewmodels

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.makeshaadi.CustomUtils
import com.makeshaadi.models.MeetupItem
import com.makeshaadi.models.MeetupStatus
import com.makeshaadi.models.User
import com.makeshaadi.services.UserRepository

class UserViewModel(application: Application) : AndroidViewModel(application) {

    val currentUid = MutableLiveData<String>()
    var currentUser = MutableLiveData<User>()

    init {
        val uid = application.getSharedPreferences("halalrishtey", Context.MODE_PRIVATE)
            .getString("user_uid", null)

        if (uid != null) {
            currentUid.value = uid
            currentUser = observeUser(uid)
        }
    }

    fun getUser(uid: String): MutableLiveData<User> {
        return if (uid == currentUid.value) {
            if (currentUser.value == null) {
                Log.d("UserViewModel", "Made repo call to get user!")
                currentUser = UserRepository.getProfileOfUser(uid)
                currentUser
            } else currentUser
        } else UserRepository.getProfileOfUser(uid)
    }

    fun observeUser(userId: String) = UserRepository.observeUserProfile(userId)

    fun getProfilesByIds(listOfIds: ArrayList<String>) = UserRepository.getProfilesByIds(listOfIds)

    fun initInterest(
        currentUserId: String,
        currentUserName: String,
        targetUserId: String
    ): MutableLiveData<String> {
        val t = currentUser.value!!
        if (!t.interestedProfiles.contains(targetUserId)) t.interestedProfiles.add(targetUserId)
        currentUser.value = t
        return UserRepository.initInterest(currentUserId, currentUserName, targetUserId)
    }

    fun removeInterest(currentUserId: String, targetUserId: String): MutableLiveData<String> {
        val t = currentUser.value!!
        t.interestedProfiles.remove(targetUserId)
        currentUser.value = t
        return UserRepository.removeInterest(currentUserId, targetUserId)
    }

    fun initConversation(current: User, target: User): MutableLiveData<String> {
        val t = currentUser.value!!
        val h = CustomUtils.genCombinedHash(current.uid!!, target.uid!!)
        if (!t.conversations.contains(h)) t.conversations.add(h)
        currentUser.value = t
        return UserRepository.initConversation(current, target)
    }

    fun getConversationsByIds(listOfIds: ArrayList<String>) =
        UserRepository.getConversationsByIds(listOfIds)

    fun observeConversation(conversationId: String) =
        UserRepository.observeConversation(conversationId)

    fun updateReadStatus(convoId: String, currentUserId: String) =
        UserRepository.updateReadStatus(convoId, currentUserId)

    fun sendMessage(conversationId: String, senderId: String, recieverId: String, text: String) =
        UserRepository.sendMessage(conversationId, senderId, recieverId, text)

    fun updateUserData(userId: String, newUserData: User): MutableLiveData<String> {
        currentUser.value = newUserData
        return UserRepository.updateUserData(userId, newUserData)
    }

    fun updateUserData(userId: String, newData: Map<String, Any>) =
        UserRepository.updateUserData(userId, newData)

    fun blockUser(currentId: String, targetId: String): MutableLiveData<String> {
        val t = currentUser.value!!
        t.blockList.add(targetId)

        //Remove from interest section
        if (t.interestedProfiles.contains(targetId))
            t.interestedProfiles.remove(targetId)

        //Remove from Chat list
        val convoId = CustomUtils.genCombinedHash(currentId, targetId)
        if (t.conversations.contains(convoId))
            t.conversations.remove(convoId)

        currentUser.value = t
        return UserRepository.blockUser(currentId, targetId)
    }

    fun unblockUser(current: String, target: String): MutableLiveData<String> {
        val t = currentUser.value!!
        if (t.blockList.contains(target))
            t.blockList.remove(target)

        currentUser.value = t

        return UserRepository.unblockUser(current, target)
    }

    fun reportUser(current: String, target: String, reason: String) =
        UserRepository.reportUser(current, target, reason)

    fun schedMeetup(meetup: MeetupItem): MutableLiveData<String> {
        val t = currentUser.value!!
        t.meetupList.add(meetup.meetupId)
        currentUser.value = t
        return UserRepository.schedMeetup(meetup)
    }

    fun updateMeetup(meetupId: String, data: Map<String, Any>) =
        UserRepository.updateMeetupData(meetupId, data)

    fun getMeetupsFromIds(meetupIds: ArrayList<String>) =
        UserRepository.getMeetupsFromIds(meetupIds)

    fun updateMeetupStatus(meetupId: String, newStatus: MeetupStatus) =
        UserRepository.updateMeetupStatus(meetupId, newStatus)

    fun getAllPlans() = UserRepository.getAllPlans()

    fun getPlan(planId: String) = UserRepository.getPlan(planId)

    fun getFreePlan() = UserRepository.getFreePlan()

    fun decDcCount(userId: String) {
        val t = currentUser.value!!
        t.currentPlan!!.dcCount -= 1
        currentUser.value = t

        UserRepository.decDcCount(userId)
    }

    fun decMeetupCount(userId: String) {
        val t = currentUser.value!!
        t.currentPlan!!.meetupCount -= 1
        currentUser.value = t

        UserRepository.decMeetupCount(userId)
    }

    fun createPayment(userId: String, planId: String, paymentId: String) =
        UserRepository.createPayment(userId, planId, paymentId)

    fun recordDirectContact(currentId: String, targetId: String) =
        UserRepository.recordDirectContact(currentId, targetId)
}
