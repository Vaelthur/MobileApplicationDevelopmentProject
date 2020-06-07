package com.example.myapplication.itemFragments

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.data.AccountInfo
import com.example.myapplication.data.AccountInfoFactory
import com.example.myapplication.data.FireItem
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.json.JSONObject

class ItemDetailsViewModel : ViewModel() {
    var itemInfo = MutableLiveData<FireItem>()
    var tempItemInfo = MutableLiveData<FireItem>()
    var interestedLiveData = MutableLiveData<List<AccountInfo>>()
    var boughtLiveData = MutableLiveData<List<FireItem>>()


    fun setItemInfo(item: FireItem){
        itemInfo.value = item
    }

    fun setTempItemInfo(item: FireItem){
        this.tempItemInfo.value = item
    }


    fun interestedUsers(itemId : String){

        FirebaseAuth.getInstance().currentUser?.let {

            FirebaseFirestore.getInstance().collection("items").document(itemId).addSnapshotListener {
                    snapshot, firestoreException ->

                if(snapshot != null && snapshot.data != null){
                    interestedLiveData.value = mutableListOf()
                    val interested = mutableListOf<AccountInfo>()

                    if (snapshot["users_favorites"] != null) {
                        val usersID = (snapshot.data?.get("users_favorites")) as ArrayList<String>
                        for(user in usersID) {
                            FirebaseFirestore.getInstance().collection("users").document(user).get().addOnCompleteListener {
                                val accInfo = AccountInfoFactory.fromMapToObj(it.result?.data)
                                interested.add(accInfo)
                                interestedLiveData.value = interested
                            }
                        }

                    }
                }
            }

        }
    }

    fun boughtItems(userId : String) {
        FirebaseFirestore.getInstance().collection("users").document(userId).addSnapshotListener {
                snapshot, firestoreException ->

            if(snapshot != null && snapshot.data != null){
                boughtLiveData.value = mutableListOf()
                val bought = mutableListOf<FireItem>()

                if (snapshot["bought"] != null) {
                    val itemsID = (snapshot.data?.get("bought")) as ArrayList<String>
                    for(item in itemsID) {
                        FirebaseFirestore.getInstance().collection("items").document(item).get().addOnCompleteListener {
                            val itemInfo = FireItem.fromMapToObj(it.result?.data)
                            bought.add(itemInfo)
                            boughtLiveData.value = bought
                        }
                    }
                }
            }
        }
    }
}
