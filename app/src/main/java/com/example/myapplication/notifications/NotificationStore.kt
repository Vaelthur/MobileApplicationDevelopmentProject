package com.example.myapplication.notifications

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId

class NotificationStore() {

    private val userToken : String? = FirebaseInstanceId.getInstance().getToken()
    private val firestore = FirebaseFirestore.getInstance()

    fun postNotification(title: String, owner: String, notificationType: NOTIFICATION_TYPE) {

        //Query for username, now just taking google display name
        val username = FirebaseAuth.getInstance().currentUser?.displayName

        //Query for owner token
        firestore.collection("tokens").document(owner)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                val ownerToken : String? = documentSnapshot.data?.get("token") as String?
                val notification = Notification.newNotificationFactory(userToken, ownerToken, title, username, notificationType)
                firestore.collection("notifications").document(userToken!!).set(notification)
            }
    }

    fun postNotificationMultipleUsers(title: String, itemID: String, notificationType: NOTIFICATION_TYPE) {

        //Query for username, now just taking google display name
        val username = FirebaseAuth.getInstance().currentUser?.displayName

        //Query for list of interested users
        firestore.collection("items").document(itemID)
            .get()
            .addOnSuccessListener { documentSnapshot ->

                if (documentSnapshot["users_favorites"] != null) {
                    val usersID =
                        (documentSnapshot.data?.get("users_favorites")) as ArrayList<String>

                    // Query for tokens
                    for (user in usersID) {

                        firestore.collection("tokens").document(user)
                            .get()
                            .addOnSuccessListener { documentSnapshot ->
                                val receiverToken: String? =
                                    documentSnapshot.data?.get("token") as String?

                                if(receiverToken != userToken){
                                    val notification = Notification.newNotificationFactory(
                                        userToken,
                                        receiverToken,
                                        title,
                                        username,
                                        notificationType)

                                    firestore.collection("notifications").document(userToken!!)
                                        .set(notification)
                                }
                            }
                    }
                }
            }
    }

    companion object TokenDb{

        fun populateTokenDb(){
            val id = FirebaseAuth.getInstance().currentUser?.uid
            val userToken = FirebaseInstanceId.getInstance().getToken()
            val map = HashMap<String, String>(1)
            map["token"] = userToken!!
            FirebaseFirestore.getInstance().collection("tokens").document(id!!).set(map)
        }
    }
}