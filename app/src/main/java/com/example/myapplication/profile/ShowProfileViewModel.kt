package com.example.myapplication.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.AccountInfo
import com.google.firebase.database.FirebaseDatabase
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