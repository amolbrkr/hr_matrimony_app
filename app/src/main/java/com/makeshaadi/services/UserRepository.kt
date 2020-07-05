package com.makeshaadi.services

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FieldValue
import com.makeshaadi.CustomUtils
import com.makeshaadi.models.*

object UserRepository {
    private val notifOnCompleteListener = OnCompleteListener { task: Task<Void> ->
        if (task.isSuccessful)
            Log.d(
                "UserRepository",
                "Created new notification!"
            )
        else Log.d(
            "UserRepository",
            "Failed to create notification, error: ${task.exception?.message}"
        )
    }

    private fun sendInterestNotif(senderId: String, senderName: String, targetId: String) {
        DatabaseService.getDbInstance()
            .collection("notifications")
            .document()
            .set(
                mapOf(
                    "notificationType" to "interest",
                    "senderId" to senderId,
                    "senderName" to senderName,
                    "targetId" to targetId,
                    "timestamp" to System.currentTimeMillis()
                )
            ).addOnCompleteListener(notifOnCompleteListener)
    }

    private fun sendChatNotif(
        senderId: String,
        conversationId: String,
        message: String
    ) {
        DatabaseService.getDbInstance()
            .collection("notifications")
            .document()
            .set(
                mapOf(
                    "notificationType" to "chat",
                    "senderId" to senderId,
                    "conversationId" to conversationId,
                    "timestamp" to System.currentTimeMillis(),
                    "message" to message
                )
            ).addOnCompleteListener(notifOnCompleteListener)
    }

    private fun sendMeetupNotif(notifTarget: String, meetup: MeetupItem?, status: MeetupStatus) {
        DatabaseService.getDbInstance()
            .collection("notifications")
            .document()
            .set(
                mapOf(
                    "notificationType" to "meetup",
                    "notifTarget" to notifTarget,
                    "meetupData" to meetup,
                    "status" to status,
                    "timestamp" to System.currentTimeMillis()
                )
            ).addOnCompleteListener(notifOnCompleteListener)
    }

    fun initInterest(
        currentUserId: String,
        currentUserName: String,
        targetUserId: String
    ): MutableLiveData<String> {
        val result = MutableLiveData<String>()
        val ref = DatabaseService
            .getDbInstance()
            .collection("users")
            .document(currentUserId)

        ref.update("interestedProfiles", FieldValue.arrayUnion(targetUserId))
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    //Send notifcation
                    sendInterestNotif(currentUserId, currentUserName, targetUserId)
                    result.value = "We'll let them know that you're interested!"
                } else result.value = it.exception?.message
            }

        return result
    }

    fun removeInterest(currentUserId: String, targetUserId: String): MutableLiveData<String> {
        val res = MutableLiveData<String>()

        val ref = DatabaseService
            .getDbInstance()
            .collection("users")
            .document(currentUserId)

        ref.update("interestedProfiles", FieldValue.arrayRemove(targetUserId))
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d(
                        "UserRepo",
                        "Succesfully removed interest target: $targetUserId for $currentUserId"
                    )
                    res.value = "You're no longer interested in this user, got it!"
                } else res.value = it.exception?.message
            }

        return res
    }

    fun initConversation(currentUser: User, targetUser: User): MutableLiveData<String> {
        val r = MutableLiveData("")
        val ts = System.currentTimeMillis()
        val idHash = CustomUtils.genCombinedHash(currentUser.uid!!, targetUser.uid!!)

        DatabaseService.getDbInstance().collection("conversations")
            .document(idHash)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val doc = task.result
                    if (doc != null && doc.exists()) {
                        //Conversation already exists, return it
                        Log.d("UserRepository", "Conversation already existed: ${doc.id}")
                        r.value = doc.id
                    } else {
                        //Decrement message count by 1
                        decMsgCount(currentUser.uid!!)

                        //Create a new conversation between the 2 users
                        val convoRef = DatabaseService.getDbInstance()
                            .collection("conversations")
                            .document(idHash)

                        val currentRef = DatabaseService.getDbInstance()
                            .collection("users")
                            .document(currentUser.uid!!)

                        val targetRef = DatabaseService.getDbInstance()
                            .collection("users")
                            .document(targetUser.uid!!)

                        DatabaseService.getDbInstance()
                            .runBatch {
                                it.update(
                                    currentRef, "conversations", FieldValue.arrayUnion(convoRef.id)
                                )
                                it.update(
                                    targetRef, "conversations", FieldValue.arrayUnion(convoRef.id)
                                )

                                //This will be stored in conversations collections
                                it.set(
                                    convoRef,
                                    mapOf(
                                        "id" to convoRef.id,
                                        "initialTimestamp" to ts,
                                        "lastMessage" to "",
                                        "lastMessageTime" to ts,
                                        "p1" to currentUser.uid,
                                        "p2" to targetUser.uid,
                                        "p1Name" to currentUser.displayName,
                                        "p2Name" to targetUser.displayName,
                                        "p1PhotoUrl" to currentUser.photoUrl,
                                        "p2PhotoUrl" to targetUser.photoUrl,
                                        "messages" to ArrayList<MessageItem>(),
                                        "readStatus" to mapOf(
                                            currentUser.uid to false,
                                            targetUser.uid to false
                                        )
                                    )
                                )
                            }.addOnCompleteListener { task1 ->
                                if (task.isSuccessful) {
                                    r.value = convoRef.id
                                    Log.d(
                                        "UserRepository",
                                        "Successfully initialized conversation with ${targetUser.displayName}"
                                    )
                                } else {
                                    r.value = ""
                                    Log.d(
                                        "UserRepository",
                                        "Failed to initialize conversation, Error: ${task1.exception?.message}"
                                    )
                                }
                            }
                    }
                }
            }
        return r
    }

    fun observeConversation(conversationId: String): MutableLiveData<Map<String, Any>> {
        val res = MutableLiveData<Map<String, Any>>()

        DatabaseService.getDbInstance()
            .collection("conversations")
            .document(conversationId)
            .addSnapshotListener { snap, e ->
                if (snap != null && snap.exists()) {
                    val t = snap.data
                    res.value = t
                } else {
                    res.value = null
                }

                if (e != null) {
                    Log.d("UserRepository", "Error observing conversation: $e")
                }
            }

        return res
    }

    fun sendMessage(conversationId: String, senderId: String, recieverId: String, message: String) {
        val ts = System.currentTimeMillis()
        val msg = MessageItem(senderId, message, System.currentTimeMillis())

        DatabaseService.getDbInstance()
            .collection("conversations")
            .document(conversationId)
            .update(
                mapOf(
                    "lastMessage" to msg.content,
                    "lastMessageTime" to ts,
                    "messages" to FieldValue.arrayUnion(msg),
                    "readStatus" to mapOf(
                        senderId to false,
                        recieverId to false
                    )
                )
            )
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    sendChatNotif(senderId, conversationId, message)
                    Log.d(
                        "UserRepository",
                        "Successfully sent message! conversationId: $conversationId"
                    )
                } else {
                    Log.d(
                        "UserRepository",
                        "Failed to send message to $conversationId, error: ${it.exception?.message}"
                    )
                }
            }
    }

    fun updateReadStatus(convoId: String, currentUserId: String) {
        DatabaseService.getDbInstance()
            .collection("conversations")
            .document(convoId)
            .update("readStatus", mapOf(currentUserId to true))
            .addOnCompleteListener {
                if (it.isSuccessful) Log.d(
                    "UserRepository",
                    "Successfully updated read status for conversation: $convoId"
                )
                else Log.d(
                    "UserRepository",
                    "Failed to update read status, error: ${it.exception?.message}"
                )
            }
    }

    fun getConversationsByIds(listOfIds: ArrayList<String>): MutableLiveData<ArrayList<Map<String, Any>>> {
        val res = MutableLiveData<ArrayList<Map<String, Any>>>()
        val t = ArrayList<Map<String, Any>>()

        listOfIds.forEach { id ->
            DatabaseService.getDbInstance()
                .collection("conversations")
                .document(id)
                .get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        it.result?.data?.let { it1 -> t.add(it1); res.value = t }
                    } else {
                        Log.d(
                            "UserRepository",
                            "Cannot get conversation $id, error: ${it.exception?.message}"
                        )
                    }
                }
        }

        return res
    }

    fun getProfilesByIds(listOfIds: ArrayList<String>): MutableLiveData<ArrayList<User>> {
        val res = MutableLiveData<ArrayList<User>>()
        val temp = ArrayList<User>()

        listOfIds.forEach { uid ->
            DatabaseService.getDbInstance()
                .collection("users")
                .document(uid)
                .get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        temp.add(CustomUtils.convertToUser(it.result!!))
                        res.value = temp
                    } else {
                        Log.d(
                            "UserRepository",
                            "Cannot get user data for id $uid, err: ${it.exception?.message}"
                        )
                    }
                }
        }

        return res
    }

    fun observeUserProfile(userId: String): MutableLiveData<User> {
        val res = MutableLiveData<User>()

        DatabaseService.getDbInstance()
            .collection("users")
            .document(userId)
            .addSnapshotListener { snap, e ->
                if (e != null) {
                    Log.d("UserRepository", "Error while observing $userId, error: $e")
                }

                if (snap != null && snap.exists()) {
                    res.value = CustomUtils.convertToUser(snap)
                    Log.d("UserRepo", "Fetched user: ${res.value}")
                } else res.value = null
            }

        return res
    }

    fun getProfileOfUser(userId: String): MutableLiveData<User> {
        val fetchedUser = MutableLiveData<User>()

        DatabaseService
            .getDbInstance()
            .collection("users")
            .document(userId)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    fetchedUser.value = CustomUtils.convertToUser(task.result!!)
                } else {
                    fetchedUser.value = null
                    Log.d("UserRepository", "Couldn't get profile of $userId")
                }
            }
        return fetchedUser
    }

    fun getAllUserProfiles(): MutableLiveData<ArrayList<User>> {
        val listOfUsers = MutableLiveData<ArrayList<User>>()
        DatabaseService
            .getDbInstance()
            .collection("users")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val tempList = ArrayList<User>()

                    task.result?.documents?.forEach {
                        //Skipped date of birth below
                        tempList.add(CustomUtils.convertToUser(it))
                    }
                    listOfUsers.value = tempList
                } else {
                    Log.d("UserRepository", "Error: ${task.exception?.message}")
                }
            }
        return listOfUsers
    }

    fun updateUserData(uid: String, newUserData: User): MutableLiveData<String> {
        val res = MutableLiveData<String>(null)
        DatabaseService.getDbInstance()
            .collection("users")
            .document(uid)
            .set(newUserData)
            .addOnCompleteListener {
                if (it.isSuccessful)
                    res.value = "Successfully updated profile data!"
                else
                    res.value = "Error: ${it.exception?.message}"
            }
        return res
    }

    fun updateUserData(uid: String, newData: Map<String, Any>): MutableLiveData<String> {
        val res = MutableLiveData<String>()
        DatabaseService.getDbInstance()
            .collection("users")
            .document(uid)
            .update(newData)
            .addOnCompleteListener {
                if (it.isSuccessful)
                    res.value = "Successfully updated profile data!"
                else res.value = "Error: ${it.exception?.message}"
            }
        return res
    }

    fun unblockUser(currentUserId: String, targetUserId: String): MutableLiveData<String> {
        val res = MutableLiveData<String>()
        val cur = DatabaseService.getDbInstance()
            .collection("users")
            .document(currentUserId)
        val target = DatabaseService.getDbInstance()
            .collection("users")
            .document(targetUserId)

        DatabaseService.getDbInstance().runBatch {
            it.update(
                cur,
                mapOf("blockList" to FieldValue.arrayRemove(targetUserId))
            )

            it.update(
                target,
                mapOf("blockList" to FieldValue.arrayRemove(currentUserId))
            )
        }.addOnCompleteListener {
            if (it.isSuccessful)
                res.value = "User Unblocked!"
            else res.value = "Error: ${it.exception?.message}"
        }
        return res
    }

    fun blockUser(currentUserId: String, targetUserId: String): MutableLiveData<String> {
        val res = MutableLiveData<String>()
        val cur = DatabaseService.getDbInstance()
            .collection("users")
            .document(currentUserId)
        val target = DatabaseService.getDbInstance()
            .collection("users")
            .document(targetUserId)

        val convoId = CustomUtils.genCombinedHash(currentUserId, targetUserId)

        val convoRef = DatabaseService.getDbInstance().collection("conversations").document(convoId)

        DatabaseService.getDbInstance()
            .runBatch {
                it.update(
                    cur,
                    mapOf(
                        "blockList" to FieldValue.arrayUnion(targetUserId),
                        "conversations" to FieldValue.arrayRemove(convoId),
                        "interestedProfiles" to FieldValue.arrayRemove(targetUserId)
                    )
                )
                it.update(
                    target,
                    mapOf(
                        "blockList" to FieldValue.arrayUnion(currentUserId),
                        "conversations" to FieldValue.arrayRemove(convoId),
                        "interestedProfiles" to FieldValue.arrayRemove(currentUserId)
                    )
                )
                it.delete(convoRef)
            }.addOnCompleteListener {
                if (it.isSuccessful) {
                    res.value = "Successfully blocked this user!"
                    Log.d("UserRepository", "$currentUserId has blocked user $targetUserId")
                } else res.value = it.exception?.message
            }
        return res
    }

    fun reportUser(currentUserId: String, targetUserId: String, reason: String) {
        DatabaseService.getDbInstance()
            .collection("reports")
            .document()
            .set(
                mapOf(
                    "reportedBy" to currentUserId,
                    "reportTarget" to targetUserId,
                    "timestamp" to System.currentTimeMillis(),
                    "reason" to reason
                )
            )
    }

    fun schedMeetup(meetup: MeetupItem): MutableLiveData<String> {
        val r = MutableLiveData<String>()
        val ref = DatabaseService.getDbInstance()
            .collection("meetups")
            .document()
        val currentRef = DatabaseService.getDbInstance()
            .collection("users")
            .document(meetup.sourceId)

        val targetRef = DatabaseService.getDbInstance()
            .collection("users")
            .document(meetup.targetId)

        meetup.meetupId = ref.id

        //Decrease meetup count by 1
        decMeetupCount(meetup.sourceId)

        DatabaseService.getDbInstance()
            .runBatch {
                ref.set(meetup)
                currentRef.update("meetupList", FieldValue.arrayUnion(meetup.meetupId))
                targetRef.update("meetupList", FieldValue.arrayUnion(meetup.meetupId))
            }.addOnCompleteListener {
                if (it.isSuccessful) {
                    r.value = "Meetup scheduled with ${meetup.targetName}"
                    sendMeetupNotif(targetRef.id, meetup, meetup.status)
                } else "Error: ${it.exception?.message}"
            }
        return r
    }

    fun updateMeetupStatus(meetupId: String, newStatus: MeetupStatus): MutableLiveData<String> {
        val r = MutableLiveData<String>(null)
        DatabaseService.getDbInstance()
            .collection("meetups")
            .document(meetupId)
            .update("status", newStatus)
            .addOnCompleteListener {
                r.value = if (it.isSuccessful) "Meetup status updated!"
                else "Error: ${it.exception?.message}"
            }
        return r
    }

    fun updateMeetupData(meetupId: String, data: Map<String, Any>): MutableLiveData<String> {
        val r = MutableLiveData<String>()
        DatabaseService.getDbInstance()
            .collection("meetups")
            .document(meetupId)
            .update(data)
            .addOnCompleteListener {
                if (it.isSuccessful)
                    r.value = "Successfully Rescheduled Meetup!"
                else r.value = "Error: ${it.exception?.message}"
            }
        return r
    }

    fun getMeetupsFromIds(meetupIds: ArrayList<String>): MutableLiveData<ArrayList<MeetupItem>> {
        val r = MutableLiveData<ArrayList<MeetupItem>>()
        val t = ArrayList<MeetupItem>()

        meetupIds.forEach { id ->
            DatabaseService.getDbInstance()
                .collection("meetups")
                .document(id)
                .get()
                .addOnSuccessListener {
                    val d = it.data!!
                    val m = MeetupItem(
                        meetupId = d["meetupId"].toString(),
                        sourceId = d["sourceId"].toString(),
                        sourcePhoto = d["sourcePhoto"].toString(),
                        sourceName = d["sourcePhoto"].toString(),
                        targetName = d["targetName"].toString(),
                        targetId = d["targetId"].toString(),
                        targetPhoto = d["targetPhoto"].toString(),
                        timestamp = d["timestamp"].toString().toLong(),
                        address = d["address"].toString(),
                        isApproved = d["isApproved"].toString().toBoolean(),
                        date = d["date"].toString().toLong(),
                        locLat = d["locLat"].toString().toDouble(),
                        locLong = d["locLong"].toString().toDouble(),
                        status = enumValueOf(d["status"].toString())
                    )
                    if (m.status == MeetupStatus.Scheduled)
                        t.add(m)
                    r.value = t
                }
        }
        return r
    }

    fun meetupFeedback(meetupId: String, feedback: String): MutableLiveData<String> {
        val r = MutableLiveData<String>()
        DatabaseService.getDbInstance()
            .collection("meetups")
            .document(meetupId)
            .update(mapOf(feedback to feedback))
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d("MeetupFragment", "Successfully added feedback!")
                }
            }
        return r;
    }

    fun getFreePlan(): MutableLiveData<PlanItem> {
        val res = MutableLiveData<PlanItem>()

        DatabaseService.getDbInstance()
            .collection("plans")
            .whereEqualTo("name", "Free Plan")
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful && it.result!!.documents.size > 0)
                    res.value = CustomUtils.convertToPlan(it.result!!.documents[0])
                else res.value = null
            }
        return res
    }

    fun getPlan(planId: String): MutableLiveData<PlanItem> {
        val res = MutableLiveData<PlanItem>()

        DatabaseService.getDbInstance()
            .collection("plans")
            .document(planId)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    res.value = CustomUtils.convertToPlan(it.result!!)
                } else {
                    res.value = null
                }
            }
        return res
    }

    fun getAllPlans(): MutableLiveData<ArrayList<PlanItem>> {
        val r = MutableLiveData<ArrayList<PlanItem>>()

        DatabaseService.getDbInstance()
            .collection("plans")
            .get()
            .addOnCompleteListener { it ->
                if (it.isSuccessful) {
                    val temp = ArrayList<PlanItem>()
                    it.result?.documents?.forEach { doc ->
                        temp.add(CustomUtils.convertToPlan(doc))
                        r.value = temp
                    }
                }
            }

        return r;
    }

    fun decMsgCount(userId: String) {
        DatabaseService.getDbInstance()
            .collection("users")
            .document(userId)
            .update(mapOf("currentPlan.chatCount" to FieldValue.increment(-1)))
            .addOnCompleteListener {
                if (it.isSuccessful)
                    Log.d("UserRepo", "Dreceased message count!")
            }
    }

    fun decDcCount(userId: String) {
        DatabaseService.getDbInstance()
            .collection("users")
            .document(userId)
            .update(mapOf("currentPlan.dcCount" to FieldValue.increment(-1)))
            .addOnCompleteListener {
                if (it.isSuccessful)
                    Log.d("UserRepo", "Decreased direct contact count!")
            }

    }

    fun decMeetupCount(userId: String) {
        DatabaseService.getDbInstance()
            .collection("users")
            .document(userId)
            .update(mapOf("currentPlan.meetupCount" to FieldValue.increment(-1)))
            .addOnCompleteListener {
                if (it.isSuccessful)
                    Log.d("UserRepo", "Decreased meetup count!")
            }
    }

    fun createPayment(userId: String, planId: String, paymentId: String) {
        DatabaseService.getDbInstance()
            .collection("payments")
            .document()
            .set(
                mapOf(
                    "userId" to userId,
                    "planId" to planId,
                    "receiptId" to "${userId}_${planId}_${System.currentTimeMillis()}",
                    "timestamp" to System.currentTimeMillis(),
                    "paymentId" to paymentId
                )
            ).addOnCompleteListener {
                if (it.isSuccessful)
                    Log.d("UserRepo", "Succesfully created order!")
            }

    }

    fun recordDirectContact(currentUserId: String, targetUserId: String) {
        DatabaseService.getDbInstance()
            .collection("users")
            .document(currentUserId)
            .update(mapOf("directContacts" to FieldValue.arrayUnion(targetUserId)))
            .addOnSuccessListener {
                Log.d("UserRepo", "Direct Contact Recorded: $targetUserId")
            }
    }
}