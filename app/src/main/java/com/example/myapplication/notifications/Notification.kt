package com.example.myapplication.notifications

import java.io.Serializable

data class Notification(
    val myToken : String?,
    val ownerToken : String?,
    val body : String?,
    val title : String?
) : Serializable {

    companion object NotificationFactory{

        fun NotificationFactory(myToken: String?,
                                ownerToken: String?,
                                title: String?,
                                senderUsername: String?,
                                type : NOTIFICATION_TYPE): Notification {

            val notificationBody : String? =  when(type) {

                                    NOTIFICATION_TYPE.INTERESTED ->
                                            "$senderUsername is interested in your item: $title"

                                    else -> "Error"
            }

            return Notification(myToken, ownerToken, notificationBody, title)
        }
    }
}