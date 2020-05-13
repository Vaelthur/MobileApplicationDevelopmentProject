package com.example.myapplication.itemFragments

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.data.FireItem
import org.json.JSONObject

class ItemDetailsViewModel : ViewModel() {
    val itemInfo = MutableLiveData<FireItem>()
    val tempItemInfo = MutableLiveData<FireItem>()

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
        val itemPic = itemJson["pictureURIString"].toString()
        val id = itemJson["id"].toString()

        return FireItem(itemPic, title, location, price, category, subcategory, expDate, condition, description, id)
    }
}
