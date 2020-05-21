package com.example.myapplication.data


class ItemStatusCreator{

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