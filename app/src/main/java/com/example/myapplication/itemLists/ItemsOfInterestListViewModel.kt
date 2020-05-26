package com.example.myapplication.itemLists

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.data.FireItem
import com.example.myapplication.data.ItemInfoFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings

class ItemsOfInterestListViewModel : ViewModel() {
    private var itemFavListLiveData : MutableLiveData<List<FireItem>> = MutableLiveData<List<FireItem>>()
    private var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    init {
        firestore.firestoreSettings = FirebaseFirestoreSettings.Builder().build()
    }

    fun listenToFavItems(){

        FirebaseAuth.getInstance().currentUser?.let { it ->

            firestore.collection("users").document(it.uid).addSnapshotListener{
                    snapshot, firestoreException ->

                if(snapshot != null){
                    val itemFavList = mutableListOf<FireItem>()

                    if(snapshot["favorites"] != null) {
                        val itemsID = (snapshot.data?.get("favorites")) as ArrayList<String>
                        for(item in itemsID) {
                            firestore.collection("items").document(item)
                                .get().addOnCompleteListener { it ->
                                    val itemInfo = FireItem.fromMapToObj(it.result?.data)
                                    itemFavList.add(itemInfo)
                                    itemFavListLiveData.value = itemFavList
                                }
                        }
                    }
                }
            }

        }
    }

    internal var liveFavItems : MutableLiveData<List<FireItem>>
        get()       {   return itemFavListLiveData }
        set(value)  {   itemFavListLiveData = value}

}
