package com.example.myapplication.itemLists

import android.app.Application
import android.view.View
import android.widget.ProgressBar
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.data.FireItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import io.grpc.InternalChannelz.id
import kotlinx.android.synthetic.main.fragment_on_sale_list.*
import com.example.myapplication.R

class ItemListViewModel constructor(application: Application) : AndroidViewModel(application) {

    private var itemListLiveData : MutableLiveData<List<FireItem>> = MutableLiveData<List<FireItem>>()
    private var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    var needRefresh : MutableLiveData<Boolean> = MutableLiveData<Boolean>(true)

    init {
        firestore.firestoreSettings = FirebaseFirestoreSettings.Builder().build()
    }

    fun listenToItems(view : View, instantRefresh : Boolean = true){

        if(instantRefresh)
            refresh(view)

        FirebaseAuth.getInstance().currentUser?.let {

            firestore.collection("items").addSnapshotListener{
                    snapshot, firestoreException ->

                if(snapshot != null || itemListLiveData.value.isNullOrEmpty()){
                    needRefresh.value = true
                }
            }
        }
    }

    fun refresh(view : View){
        FirebaseAuth.getInstance().currentUser?.let {

            val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerItemList)
            recyclerView.visibility = View.GONE

            val spinner = view.findViewById<ProgressBar>(R.id.progressBar)
            spinner.visibility = View.VISIBLE

            firestore.collection("items").get()
                .addOnSuccessListener{ snapshot ->
                val itemList = mutableListOf<FireItem>()

                if (snapshot != null) {

                    for (document in snapshot.documents) {
                        if (document["owner"] != it.uid) {
                            itemList.add(FireItem.fromMapToObj(document.data))
                        }
                    }

                    val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerItemList)
                    val spinner = view.findViewById<ProgressBar>(R.id.progressBar)
                    spinner.visibility = View.GONE
                    itemListLiveData.value = itemList
                    recyclerView.visibility = View.VISIBLE
                    needRefresh.value = false
                }
            }
        }
    }


    internal var liveItems : MutableLiveData<List<FireItem>>
        get()       {   return itemListLiveData }
        set(value)  {   itemListLiveData = value}

}