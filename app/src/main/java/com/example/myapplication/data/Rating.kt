package com.example.myapplication.data

import java.io.Serializable

data class Rating(val meanRating : Double, val ratingsReceived : Long) : Serializable {

    companion object RatingFactory{

        fun fromMapToObj(hash : Map<String, Any>?) : Rating {
            val mean = hash?.get("meanRating") as Double
            val totalReviews = hash["ratingsReceived"] as Long

            return Rating(mean, totalReviews)
        }
    }
}