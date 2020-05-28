package com.example.myapplication.data

import java.io.Serializable

data class Review(val reviewedBy : String, val reviewedByUsername : String, val stars : Double, val comment: String? = null) :
    Serializable {

    companion object RatingFactory{

        fun fromMapToObj(hash : Map<String, Any>?) : Review {
            val reviewedBy = hash?.get("reviewedBy") as String
            val reviewedByUsername = hash["reviewedByUsername"] as String
            val rating = hash["stars"] as Double
            val comment = hash["comment"] as String?

            return Review(reviewedBy, reviewedByUsername, rating, comment)
        }
    }
}