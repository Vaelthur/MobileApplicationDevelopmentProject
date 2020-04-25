package com.example.myapplication.itemFragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.data.Item
import org.json.JSONObject

class ItemDetailsViewModel : ViewModel() {
    val itemInfo = MutableLiveData<Item>()
    val tempItemInfo = MutableLiveData<Item>()

    fun setItemInfo(itemJson: JSONObject) {
        itemInfo.value = createItemInfoFromJSON(itemJson)
    }

    fun setItemInfo(item: Item){
        itemInfo.value = item
    }

    fun setTempItemInfo(itemJson: JSONObject){
        tempItemInfo.value = createItemInfoFromJSON(itemJson)
    }

    fun setTempItemInfo(item: Item){
        this.tempItemInfo.value = item
    }

    private fun createItemInfoFromJSON(itemJson: JSONObject): Item {

        val title = itemJson["title"].toString()
        val price = itemJson["price"].toString()
        val location = itemJson["location"].toString()
        val condition = itemJson["condition"].toString()
        val description = itemJson["description"].toString()
        val category = itemJson["category"].toString()
        val subcategory = itemJson["subcategory"].toString()
        val expDate = itemJson["expDate"].toString()
        val itemPic = itemJson["pictureURIString"].toString()

        return Item(itemPic, title, location, price, category, subcategory, expDate, condition, description)
    }
}
