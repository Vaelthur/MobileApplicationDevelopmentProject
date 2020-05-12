package com.example.myapplication.data

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.common.api.Response
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore


class FirestoreRepository {

    var collection  =  FirebaseFirestore.getInstance().collection("items")
    var allItems: MutableLiveData<List<Item>>? = null

    @WorkerThread
    suspend fun getAll() {
        val itemQueryResult = collection.get()
        itemQueryResult
            .addOnSuccessListener { accountDocument ->
                if(accountDocument == null) {

                }
                else {
                    for(docSnap in accountDocument.documents){
                        val item = Item(docSnap.data)
                    }
                }
            }
        //return items
    }

}