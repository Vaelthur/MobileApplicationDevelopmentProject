package com.example.myapplication.main

import android.app.Application
import androidx.lifecycle.*
import androidx.room.Room
import com.example.myapplication.AccountInfo
import com.example.myapplication.data.Item
import com.example.myapplication.data.ItemDao
import com.example.myapplication.data.ItemRepository
import com.example.myapplication.data.ItemRoomDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ItemListViewModel constructor(application: Application) : AndroidViewModel(application) {

    val itemListLiveData : LiveData<List<Item>>
    private val repository: ItemRepository

    init {
        val itemDao : ItemDao = ItemRoomDatabase.getDatabase(application, viewModelScope).itemDao()
        repository = ItemRepository(itemDao)
        itemListLiveData = repository.allItems
    }

    /// region DB interactions

    fun addItem() = viewModelScope.launch(Dispatchers.IO){

        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val item = Item(
             "null", "MEGA TITOLO",
            "Torino", "100", "divernten","subcaaacca", timeStamp, "makle", "descrivo"
        )
        repository.insertAll(item)
    }

    fun deleteItem(item: Item) = viewModelScope.launch(Dispatchers.IO){

        repository.delete(item)
    }

    fun insertAll(itemToSave: Item) = viewModelScope.launch(Dispatchers.IO){

        repository.insertAll(itemToSave)
    }

    fun updateItem(itemToUpdate: Item) = viewModelScope.launch(Dispatchers.IO){

        repository.updateItem(itemToUpdate)
    }

    /// endregion
}