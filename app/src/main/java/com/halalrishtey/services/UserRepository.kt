package com.halalrishtey.services

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FieldValue
import com.halalrishtey.CustomUtils
import com.halalrishtey.models.MessageItem
import com.halalrishtey.models.User

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

    fun sendGeneralNotifToUser(
        currentUserId: String,
        targetUserId: String,
        title: String,
        content: String
    ) {
        DatabaseService.getDbInstance()
            .collection("notifications")
            .document()
            .set(
                mapOf(
                    "notificationType" to "general",
                    "senderId" to currentUserId,
                    "targetId" to targetUserId,
                    "title" to title,
                    "content" to content,
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

                    Log.d(
                        "UserRepo",
                        "Succesfully expressed interest to target: $targetUserId for $currentUserId"
                    )
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
                                        "initialTimestamp" to ts,
                                        "lastMessage" to "",
                                        "lastMessageTime" to ts,
                                        "p1" to currentUser.uid,
                                        "p2" to targetUser.uid,
                                        "p1Name" to currentUser.displayName,
                                        "p2Name" to targetUser.displayName,
                                        "p1PhotoUrl" to currentUser.photoUrl,
                                        "p2PhotoUrl" to targetUser.photoUrl,
                                        "messages" to ArrayList<MessageItem>()
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

    fun sendMessage(conversationId: String, senderId: String, message: String) {
        val ts = System.currentTimeMillis()
        val msg = MessageItem(senderId, message, System.currentTimeMillis())

        DatabaseService.getDbInstance()
            .collection("conversations")
            .document(conversationId)
            .update(
                mapOf(
                    "lastMessage" to msg.content,
                    "lastMessageTime" to ts,
                    "messages" to FieldValue.arrayUnion(msg)
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

    fun blockUser(currentUserId: String, targetUserId: String): MutableLiveData<String> {
        val res = MutableLiveData<String>()
        val cur = DatabaseService.getDbInstance()
            .collection("users")
            .document(currentUserId)
        val target = DatabaseService.getDbInstance()
            .collection("users")
            .document(targetUserId)

        DatabaseService.getDbInstance()
            .runBatch {
                it.update(cur, "blockList", FieldValue.arrayUnion(targetUserId))
                it.update(target, "blockList", FieldValue.arrayUnion(currentUserId))
            }.addOnCompleteListener {
                if (it.isSuccessful) {
                    res.value = "Successfully blocked this user!"
                    Log.d("UserRepository", "$currentUserId has blocked user $targetUserId")
                } else res.value = it.exception?.message
            }
        return res
    }

    fun reportUser(currentUser: User, targetUser: User, reason: String) {
        DatabaseService.getDbInstance()
            .collection("reports")
            .document()
            .set(
                mapOf(
                    "reportedBy" to mapOf(
                        "name" to currentUser.displayName,
                        "uid" to currentUser.uid
                    ),
                    "target" to mapOf(
                        "name" to targetUser.displayName,
                        "uid" to targetUser.uid
                    ),
                    "timestamp" to System.currentTimeMillis(),
                    "reason" to reason
                )
            )
    }
}