package com.example.myapplication.itemLists

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.myapplication.data.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings

class ItemListViewModel constructor(application: Application) : AndroidViewModel(application) {

    private var itemListLiveData : MutableLiveData<List<FireItem>> = MutableLiveData<List<FireItem>>()
    private var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    init {
        firestore.firestoreSettings = FirebaseFirestoreSettings.Builder().build()
    }

    fun listenToItems(){

        FirebaseAuth.getInstance().currentUser?.let {

            firestore.collection("items").addSnapshotListener{
                snapshot, firestoreException ->

                if(snapshot != null){
                    val itemList = mutableListOf<FireItem>()

                    for (document in snapshot.documents) {
                        if (document["owner"] != it.uid) {
                            itemList.add(FireItem.fromMapToObj(document.data))
                        }
                    }

                    itemListLiveData.value = itemList
                }
            }

        }
    }

    internal var liveItems : MutableLiveData<List<FireItem>>
        get()       {   return itemListLiveData }
        set(value)  {   itemListLiveData = value}

}