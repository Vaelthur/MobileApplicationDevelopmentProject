package com.example.myapplication.itemFragments

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.AccountInfo
import com.example.myapplication.AccountInfoFactory
import com.example.myapplication.data.FireItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import org.json.JSONObject

class ItemDetailsViewModel : ViewModel() {
    var itemInfo = MutableLiveData<FireItem>()
    var tempItemInfo = MutableLiveData<FireItem>()
    var interestedLiveData = MutableLiveData<List<AccountInfo>>()

    fun setItemInfo(itemJson: JSONObject) {
        itemInfo.value = createItemInfoFromJSON(itemJson)
    }

    fun setItemInfo(item: FireItem){
        itemInfo.value = item
    }

    fun setTempItemInfo(item: FireItem){
        this.tempItemInfo.value = item
    }

    private fun createItemInfoFromJSON(itemJson: JSONObject): FireItem {

        val title = itemJson["title"].toString()
        val price = itemJson["price"].toString()
        val location = itemJson["location"].toString()
        val condition = itemJson["condition"].toString()
        val description = itemJson["description"].toString()
        val category = itemJson["category"].toString()
        val subcategory = itemJson["subcategory"].toString()
        val expDate = itemJson["expDate"].toString()
        val itemPic = itemJson["picture_uri"].toString()
        val id = itemJson["id"].toString()
        val owner: String = itemJson["owner"].toString()
        val status: String = itemJson["status"].toString()

        return FireItem(itemPic, title, location, price, category, subcategory, expDate, condition, description, id, owner, status)
    }

    fun interestedUsers(itemId : String){

        FirebaseAuth.getInstance().currentUser?.let {

            FirebaseFirestore.getInstance().collection("items").document(itemId).addSnapshotListener {
                    snapshot, firestoreException ->

                if(firestoreException != null){
                    //Make toasst
                    val a  =  1 + 2
                }

                if(snapshot != null){
                    val interested = mutableListOf<AccountInfo>()
//RIMANE SOLO UN UTENTE ALLA VOLTA
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
}
