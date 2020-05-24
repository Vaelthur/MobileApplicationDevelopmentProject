package com.example.myapplication.data

import android.content.Context
import android.net.Uri
import android.widget.EditText
import androidx.lifecycle.ViewModelProviders
import com.example.myapplication.ItemEditFragment
import com.example.myapplication.itemFragments.ItemDetailsViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_item_edit.*

class ItemInfoFactory(){

    companion object ItemInfoFactory{

        const val defaultItemPhoto = "android.resource://com.example.myapplication/drawable/default__item_image"

        fun getItemInfoFromTextEdit(editFrag: ItemEditFragment, id : String? = null): FireItem {
            val getEditViewText =
                {
                        id: EditText -> id.text
                }

            val getItemPicturePath : (editFrag : ItemEditFragment) -> Uri =
                {
                    val readFromPref = it.requireActivity().getPreferences(Context.MODE_PRIVATE)
                    var tempProfilePicture = readFromPref.getString("item_picture_editing", null)

                    val itemListViewModel =
                        ViewModelProviders.of(editFrag.requireActivity()).get(ItemDetailsViewModel()::class.java)

                    if(tempProfilePicture == null){
                        tempProfilePicture = itemListViewModel.tempItemInfo.value?.picture_uri
                        Uri.parse(tempProfilePicture)
                        //Uri.parse(ItemInfoFactory.defaultItemPhoto)
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
            val owner = FirebaseAuth.getInstance().currentUser!!.uid

            id?.let{
                return FireItem(itemPic, title, location, price, category, subcategory, expDate, condition, description, id, owner)
            }

            val newItemID = FirebaseFirestore.getInstance().collection("items").document().id
            return FireItem(itemPic, title, location, price, category, subcategory, expDate, condition, description, newItemID, owner)
        }
    }


}