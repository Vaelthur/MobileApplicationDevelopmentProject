package com.example.myapplication.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.data.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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

                if(firestoreException != null){
                    //Make toasst
                }

                if(snapshot != null){
                    val itemList = mutableListOf<FireItem>()

                    for (document in snapshot.documents) {
                        if (document["owner"] != it.uid) {
                            itemList.add(FireItem.fromMapToObj(document.data))
                        }

                        if (document["users_favorites"] != null) {
                            //val favs = arrayOf(document["users_favorites"])
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