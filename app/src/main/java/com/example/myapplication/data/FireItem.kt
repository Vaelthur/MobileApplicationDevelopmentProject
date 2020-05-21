package com.example.myapplication.data

import java.io.Serializable

data class FireItem(
    val picture_uri: String?,
    val title: String,
    val location: String?,
    val price: String,
    val category: String,
    val subCategory: String,
    val expDate: String,
    val condition: String,
    val description: String,
    val id: String,
    val owner: String,
    val status: String = "Available"
)  : Serializable {

    companion object ItemFactory {

        fun fromMapToObj(hash: Map<String, Any>?): FireItem {

            var status : String? = null
            if(hash?.get("status") is String){
                status =  hash?.get("status") as String
            }
            else {
                status =  "Available"
            }

            return FireItem(
                hash?.get("picture_uri") as String,
                hash["title"] as String,
                hash["location"] as String,
                hash["price"].toString(),
                hash["category"] as String,
                hash["subCategory"].toString(),
                hash["expDate"].toString(),
                hash["condition"] as String,
                hash["description"] as String,
                hash["id"] as String,
                hash["owner"] as String,
                status
            )
        }
    }
}