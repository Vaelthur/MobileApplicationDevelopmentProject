package com.example.myapplication.main

import android.net.Uri
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.ItemEditFragment
import com.example.myapplication.R
import kotlinx.android.synthetic.main.fragment_item_edit.*
import java.io.Serializable
import java.net.URI
import java.util.*
import java.util.concurrent.locks.Condition

data class ItemInfoData(val pictureURIString : String?, val title : String, val location : String, val price : String)

data class ItemDetailsInfoData(var pictureURIString: String, val title: String,
                               val location: String, val price: String, val category: String,
                               val expDate: String, val condition: String, val description: String)
    :Serializable{}

class ItemInfoFactory(){

    companion object ItemInfoFactory{
        const val defaultItemPhoto = "android.resource://com.example.myapplication/drawable/default__item_image"

        fun getItemInfoFromTextEdit(editFrag: ItemEditFragment): ItemDetailsInfoData{
            val getEditViewText =
                {
                        id: EditText -> id.text
                }

            val title = StringBuffer(getEditViewText(editFrag.item_title_edit)).toString()
            val location = StringBuffer(getEditViewText(editFrag.item_location_value)).toString()
            val price = StringBuffer(getEditViewText(editFrag.item_price_edit)).toString()
            val category = editFrag.category_spinner.selectedItem.toString()
            val expDate = editFrag.item_expire_date_value.text.toString()
            val condition = StringBuffer(getEditViewText(editFrag.item_condition_value)).toString()
            val description = StringBuffer(getEditViewText(editFrag.item_picture_description_edit)).toString()
            val itemPic = Uri.parse(defaultItemPhoto).toString()

            return ItemDetailsInfoData(itemPic, title, location, price, category, expDate, condition, description)
        }
    }


}