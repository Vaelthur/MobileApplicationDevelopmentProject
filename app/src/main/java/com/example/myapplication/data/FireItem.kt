package com.example.myapplication.data

import com.google.firebase.firestore.GeoPoint
import java.io.Serializable

data class FireItem(
    val picture_uri: String?,
    val title: String,
    var location: String?,
    val price: String,
    val category: String,
    val subCategory: String,
    val expDate: String,
    val condition: String,
    val description: String,
    val id: String,
    val owner: String,
    //var coord: GeoPoint? = null,
    var lat: Double?,
    var lon: Double?,
    val status: String = "Available"
)  : Serializable {

    companion object ItemFactory {

        fun fromMapToObj(hash: Map<String, Any>?): FireItem {

            var status : String? = null
            var lat : Double? = null
            var lon: Double? = null
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
                hash["sub_category"].toString(),
                hash["expDate"].toString(),
                hash["condition"] as String,
                hash["description"] as String,
                hash["id"] as String,
                hash["owner"] as String,
                //hash["coord"] as GeoPoint?,
                hash["lat"] as Double,
                hash["lon"] as Double,
                status
            )
        }
    }
}