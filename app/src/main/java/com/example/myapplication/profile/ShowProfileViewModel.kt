package com.example.myapplication.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.data.AccountInfo

class ShowProfileViewModel : ViewModel() {
    val accountInfo = MutableLiveData<AccountInfo>()
    val tempAccountInfo = MutableLiveData<AccountInfo>()

    fun setAccountInfo(accountInfo: AccountInfo) {
        this.accountInfo.value = accountInfo
    }

    fun setTempAccountInfo(tempAccountInfo: AccountInfo) {
        this.tempAccountInfo.value = tempAccountInfo
    }
}