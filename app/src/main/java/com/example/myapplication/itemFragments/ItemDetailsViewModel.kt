package com.example.myapplication.itemFragments

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.main.ItemDetailsInfoData
import org.json.JSONObject

class ItemDetailsViewModel : ViewModel() {
    val itemInfo = MutableLiveData<ItemDetailsInfoData>()
    val tempItemInfo = MutableLiveData<ItemDetailsInfoData>()

    fun setItemInfo(itemJson: JSONObject) {
        itemInfo.value = createItemInfoFromJSON(itemJson)
    }

    fun setItemInfo(itemInfoD: ItemDetailsInfoData){
        itemInfo.value = itemInfoD
    }

    fun setTempItemInfo(itemJson: JSONObject){
        tempItemInfo.value = createItemInfoFromJSON(itemJson)
    }

    fun setTempItemInfo(itemInfoD: ItemDetailsInfoData){
        this.tempItemInfo.value = itemInfoD
    }

    private fun createItemInfoFromJSON(itemJson: JSONObject): ItemDetailsInfoData {
        val title = itemJson["title"].toString()
        val price = itemJson["price"].toString()
        val location = itemJson["location"].toString()
        val condition = itemJson["condition"].toString()
        val description = itemJson["description"].toString()
        val category = itemJson["category"].toString()
        val subcategory = itemJson["subcategory"].toString()
        val expDate = itemJson["expDate"].toString()
        val itemPic = itemJson["pictureURIString"].toString()

        return ItemDetailsInfoData(itemPic, title, location, price, category, subcategory, expDate, condition, description)
    }

    fun setItemPicture(itemPicPath: String) {
        tempItemInfo.value?.pictureURIString = itemPicPath
        tempItemInfo.value = tempItemInfo.value
    }
}
