package com.example.myapplication.itemLists

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.lifecycle.*
import com.example.myapplication.data.*
import com.example.myapplication.main.MainActivity
import com.google.common.collect.Iterables.removeAll
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings

class ItemListViewModel constructor(application: Application) : AndroidViewModel(application) {

    private var itemListLiveData : MutableLiveData<List<FireItem>> = MutableLiveData<List<FireItem>>()
    private var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    var needRefresh : MutableLiveData<Boolean> = MutableLiveData<Boolean>(true)

    init {
        firestore.firestoreSettings = FirebaseFirestoreSettings.Builder().build()
    }

    fun listenToItems(instantRefresh : Boolean = true){

        if(instantRefresh)
            refresh()

        FirebaseAuth.getInstance().currentUser?.let {

            firestore.collection("items").addSnapshotListener{
                    snapshot, firestoreException ->

                if(snapshot != null || itemListLiveData.value.isNullOrEmpty()){
                    needRefresh.value = true
                }
            }
        }
    }

    fun refresh(){
        FirebaseAuth.getInstance().currentUser?.let {

            firestore.collection("items").get()
                .addOnSuccessListener{ snapshot ->
                val itemList = mutableListOf<FireItem>()

                if (snapshot != null) {

                    for (document in snapshot.documents) {
                        if (document["owner"] != it.uid) {
                            itemList.add(FireItem.fromMapToObj(document.data))
                        }
                    }

                    itemListLiveData.value = itemList
                    needRefresh.value = false
                }
            }
        }
    }


    internal var liveItems : MutableLiveData<List<FireItem>>
        get()       {   return itemListLiveData }
        set(value)  {   itemListLiveData = value}

}