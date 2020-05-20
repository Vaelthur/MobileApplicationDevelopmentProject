package com.example.myapplication.notifications

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.ktx.Firebase

class NotificationStore() {

    private val userToken : String? = FirebaseInstanceId.getInstance().getToken()
    private val firestore = FirebaseFirestore.getInstance()

    fun postNotification(title: String, owner: String, interested: NOTIFICATION_TYPE) {

        //Query for username, now just taking google display name
        val username = FirebaseAuth.getInstance().currentUser?.displayName

        //Query for owner token
        firestore.collection("tokens").document(owner)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                val ownerToken : String? = documentSnapshot.data?.get("token") as String?
                val notification = Notification.NotificationFactory(userToken, ownerToken, title, username, NOTIFICATION_TYPE.INTERESTED)
                firestore.collection("notifications").document(userToken!!).set(notification)
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