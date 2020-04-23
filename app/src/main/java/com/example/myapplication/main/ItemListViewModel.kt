package com.example.myapplication.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.AccountInfo

class ItemListViewModel : ViewModel() {

    val itemListLiveData = MutableLiveData<ArrayList<ItemInfoData>>()

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
    }

    fun addItem() {
        val item = ItemInfoData(
            null,
            "Titolo",
            "Torino",
            "400$")
        itemListLiveData.value?.add(item)
        itemListLiveData.value = itemListLiveData.value
    }





}