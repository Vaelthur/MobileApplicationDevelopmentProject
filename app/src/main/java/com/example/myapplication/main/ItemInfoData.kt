package com.example.myapplication.main

import android.net.Uri
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import java.io.Serializable
import java.net.URI
import java.util.*
import java.util.concurrent.locks.Condition

data class ItemInfoData(val pictureURIString : String?, val title : String, val location : String, val price : String)

data class ItemDetailsInfoData(val pictureURIString: String?, val title: String,
                               val location: String, val price: String, val category: Int,
                               val expDate: String, val condition: String, val description: String)
    :Serializable{}

class ItemInfoFactory(){

    companion object ItemInfoFactory{
        val defaultItemPhoto = "android.resource://com.example.myapplication/drawable/default__item_image"

        fun getItemInfoFromTextEdit(parentActivity: AppCompatActivity): ItemDetailsInfoData{
            val getEditViewText =
                {
                        id: EditText -> id.text
                }
//        val getItemPicturePath : () -> Uri = {
//            // TODO: implement
//        }

            val title = StringBuffer(getEditViewText(parentActivity.findViewById<EditText>(R.id.item_title_edit))).toString()
            val location = StringBuffer(getEditViewText(parentActivity.findViewById<EditText>(R.id.item_location_value))).toString()
            val price = StringBuffer(getEditViewText(parentActivity.findViewById<EditText>(R.id.item_price_edit))).toString()
            val category = parentActivity.findViewById<Spinner>(R.id.category_spinner).selectedItemPosition
            val expDate = parentActivity.findViewById<TextView>(R.id.item_expire_date_value).text.toString()
            val condition = StringBuffer(getEditViewText(parentActivity.findViewById<EditText>(R.id.item_condition_value))).toString()
            val description = StringBuffer(getEditViewText(parentActivity.findViewById<EditText>(R.id.item_picture_description_edit))).toString()

            return ItemDetailsInfoData(defaultItemPhoto, title, location, price, category, expDate, condition, description)
        }
    }


}