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
    val accountInfo = MutableLiveData<AccountInfo>()
    val tempAccountInfo = MutableLiveData<AccountInfo>()

    fun setAccountInfo(accountJson: JSONObject) {
        accountInfo.value = createAccountInfoFromJSON(accountJson)
    }

    fun setAccountInfo(accountInfo: AccountInfo) {
        this.accountInfo.value = accountInfo
    }

    fun setTempAccountInfo(tempAccountInfo: AccountInfo) {
        this.tempAccountInfo.value = tempAccountInfo
    }

    fun setTempAccountInfo(accountJson: JSONObject) {
        tempAccountInfo.value = createAccountInfoFromJSON(accountJson)
    }

    private fun createAccountInfoFromJSON(accountJson: JSONObject): AccountInfo {
        val fullname = accountJson["fullname"].toString()
        val username = accountJson["username"].toString()
        val email = accountJson["email"].toString()
        val location = accountJson["location"].toString()
        val profile_picture = accountJson["profilePicture"].toString()
        return AccountInfo(fullname,username,email,location,profile_picture)
    }
}