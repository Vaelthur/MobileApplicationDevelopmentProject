package com.example.myapplication.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ItemListViewModel constructor(application: Application) : AndroidViewModel(application) {

    var itemListLiveData : MutableLiveData<List<Item>>? = null
    private val repository: FirestoreRepository = FirestoreRepository()


//    fun getAll() = viewModelScope.launch(Dispatchers.IO){
//        repository.getAll()
//        itemListLiveData = repository.allItems
//    }

//    fun insertAll(itemToSave: Item) = viewModelScope.launch(Dispatchers.IO){
//
//        repository.insertAll(itemToSave)
//    }
//
//    fun updateItem(itemToUpdate: Item) = viewModelScope.launch(Dispatchers.IO){
//
//        repository.updateItem(itemToUpdate)
//    }

    /// endregion
}