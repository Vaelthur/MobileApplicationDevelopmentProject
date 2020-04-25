package com.example.myapplication.main

import android.content.Context
import android.net.Uri
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.AccountInfoFactory
import com.example.myapplication.ItemEditFragment
import com.example.myapplication.R
import com.example.myapplication.data.Item
import com.example.myapplication.profile.EditProfileFragment
import kotlinx.android.synthetic.main.fragment_item_edit.*
import org.json.JSONObject
import java.io.Serializable
import java.net.URI
import java.util.*
import java.util.concurrent.locks.Condition

data class ItemInfoData(val pictureURIString : String?, val title : String, val location : String, val price : String)

data class ItemDetailsInfoData(val pictureURIString: String, val title: String,
                               val location: String, val price: String, val category: String, val subCategory: String,
                               val expDate: String, val condition: String, val description: String)
    :Serializable{}

class ItemInfoFactory(){

    companion object ItemInfoFactory{

        const val defaultItemPhoto = "android.resource://com.example.myapplication/drawable/default__item_image"

        fun getItemInfoFromTextEdit(editFrag: ItemEditFragment): Item {
            val getEditViewText =
                {
                        id: EditText -> id.text
                }

            val getItemPicturePath : (editFrag : ItemEditFragment) -> Uri =
                {
                    val readFromPref = it.requireActivity().getPreferences(Context.MODE_PRIVATE)
                    val tempProfilePicture = readFromPref.getString("item_picture_editing", null)

                    if(tempProfilePicture == null){
                            Uri.parse(ItemInfoFactory.defaultItemPhoto)
                    }
                    else {
                        Uri.parse(tempProfilePicture)
                    }
                }

            val title = StringBuffer(getEditViewText(editFrag.item_title_edit)).toString()
            val location = StringBuffer(getEditViewText(editFrag.item_location_value)).toString()
            val price = StringBuffer(getEditViewText(editFrag.item_price_edit)).toString()
            val category = editFrag.category_spinner.selectedItem.toString()
            val subcategory = editFrag.subcategory_spinner.selectedItem.toString()
            val expDate = editFrag.item_expire_date_value.text.toString()
            val condition = StringBuffer(getEditViewText(editFrag.item_condition_value)).toString()
            val description = StringBuffer(getEditViewText(editFrag.item_picture_description_edit)).toString()
            val itemPic = getItemPicturePath(editFrag).toString()

            return Item(itemPic, title, location, price, category, subcategory, expDate, condition, description)
        }
    }


}