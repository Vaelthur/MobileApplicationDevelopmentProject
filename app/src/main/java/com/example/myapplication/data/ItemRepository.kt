package com.example.myapplication.data

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData

class ItemRepository(private val itemDao: ItemDao) {

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    val allItems: LiveData<List<Item>> = itemDao.getAllItems()

    // You must call this on a non-UI thread or your app will crash. So we're making this a
    // suspend function so the caller methods know this.
    // Like this, Room ensures that you're not doing any long running operations on the main
    // thread, blocking the UI.
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertAll(item: Item) {
        itemDao.insertAll(item)
    }

    @WorkerThread
    suspend fun delete(item : Item){
        itemDao.delete(item)
    }
}