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
     val id: String
)  : Serializable {

    companion object ItemFactory {

        fun fromMapToObj(hash: Map<String, Any>?): FireItem {

            return FireItem(
                hash?.get("picture_uri") as String,
                hash["title"] as String,
                hash["location"] as String,
                hash["price"].toString(),
                hash["category"] as String,
                hash["sub_category"] as String,
                hash["expDate"].toString(),
                hash["condition"] as String,
                hash["description"] as String,
                hash["id"] as String
            )
        }
    }
}