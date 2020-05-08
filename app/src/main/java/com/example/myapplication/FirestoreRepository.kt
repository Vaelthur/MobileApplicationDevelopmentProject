package com.example.myapplication

import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlin.random.Random

class FirestoreRepository {

    var database = Firebase.database.reference

    fun saveAccountInfo(accountInfo: AccountInfo) {
        val key = database.child("users").push().key //new key random
        if(key == null) {
            Log.w("Problem", "Porblem")
            return
        }
        val accountValues = accountInfo.toMap()

        val childUpdates = HashMap<String, Any>()
        childUpdates["/users/$key"] = accountValues

        database.updateChildren(childUpdates)

    }
}