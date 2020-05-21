package com.example.myapplication.data

enum class ITEMSTATUS {
    AVAILABLE, SOLD, BLOCKED
}

class ItemStatus{

    companion object ItemStatus{

        fun getStatus(statusEnum : ITEMSTATUS) : String{
            val statusStr : String = when(statusEnum){
                ITEMSTATUS.AVAILABLE -> "Available"
                ITEMSTATUS.BLOCKED -> "Blocked"
                ITEMSTATUS.SOLD -> "Sold"
                else -> "Undefined"
            }

            return statusStr
        }

    }

}