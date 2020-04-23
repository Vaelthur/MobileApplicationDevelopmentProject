package com.example.myapplication.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.AccountInfo

class ItemListViewModel : ViewModel() {

    val itemListLiveData = MutableLiveData<ArrayList<ItemInfoData>>()
    var itemList : ArrayList<ItemInfoData> = arrayListOf()

    init {
        populate()
    }

    private fun populate(){
        val item = ItemInfoData(
            null,
            "Titolo",
            "Torino",
            "400$")
        itemListLiveData.value = arrayListOf(item)
        itemList = arrayListOf(item)
    }

    fun addItem() {
        val item = ItemInfoData(
            null,
            "Titolo",
            "Torino",
            "400$")
        itemListLiveData.value?.add(item)
        itemList.add(item)
        itemListLiveData.value = itemListLiveData.value
    }




}