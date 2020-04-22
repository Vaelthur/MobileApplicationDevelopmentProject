package com.example.myapplication.profile

import android.app.Application
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.AccountInfo
import com.example.myapplication.AccountInfoFactory
import com.google.gson.JsonObject
import org.json.JSONObject


class ShowProfileViewModel : ViewModel() {
    private val accountInfo = MutableLiveData<AccountInfo>()
    private val tempAccountInfo = MutableLiveData<AccountInfo>()

    fun getAccountInfo(): LiveData<AccountInfo>? {
        return accountInfo
    }

    fun setAccountInfo(accountJson: JSONObject) {
        val fullname = accountJson["fullname"].toString()
        val username = accountJson["username"].toString()
        val email = accountJson["email"].toString()
        val location = accountJson["location"].toString()
        val profile_picture = accountJson["profilePicture"].toString()
        accountInfo.value = AccountInfo(fullname,username,email,location,profile_picture)
    }

    fun setAccountInfo(accountInfo: AccountInfo) {
        this.accountInfo.value = accountInfo
    }

    fun getTempAccountInfo(): LiveData<AccountInfo>? {
        return tempAccountInfo
    }

    fun setTempAccountInfo(accountJson: JSONObject) {
        val fullname = accountJson["fullname"].toString()
        val username = accountJson["username"].toString()
        val email = accountJson["email"].toString()
        val location = accountJson["location"].toString()
        val profile_picture = accountJson["profilePicture"].toString()
        tempAccountInfo.value = AccountInfo(fullname,username,email,location,profile_picture)
    }

    fun setTempAccountInfo(tempAccountInfo: AccountInfo) {
        this.tempAccountInfo.value = tempAccountInfo
    }
}