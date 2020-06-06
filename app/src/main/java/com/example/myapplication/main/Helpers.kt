package com.example.myapplication.main

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Patterns
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.exifinterface.media.ExifInterface
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.myapplication.R
import com.example.myapplication.data.AccountInfo
import com.example.myapplication.data.FireItem
import com.example.myapplication.data.ItemInfoFactory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.GeoPoint
import org.json.JSONObject
import java.io.IOException
import java.io.Serializable


class Helpers(){

    companion object Helpers {

        ///region defaultItem functions

        fun getDefaultItemBundle() : Bundle {

            val own = FirebaseAuth.getInstance().currentUser!!.uid

            val itemInfo = FireItem(
                ItemInfoFactory.defaultItemPhoto, "", "",
                "", "Arts & Crafts", "Painting, Drawing & Art Supplies", "", "", "", "0", owner = own, lat = 0.0, lon = 0.0
            )
            val itemBundle = Bundle(2)
            itemBundle.putBoolean("myitems", true)
            itemBundle.putSerializable("item", itemInfo as Serializable?)

            return itemBundle
        }

        fun getDefaultItem() : FireItem {

            val own = FirebaseAuth.getInstance().currentUser!!.uid

            return FireItem(
                ItemInfoFactory.defaultItemPhoto, "", "",
                "", "Arts & Crafts", "Painting, Drawing & Art Supplies", "", "", "", "0", owner = own, lat = 0.0, lon = 0.0
            )
        }

        ///endregion

        ///region checkers functions

        fun someEmptyAccountFields(accountInfo: AccountInfo): Boolean {
            return accountInfo.fullname!!.isEmpty() || accountInfo.username!!.isEmpty() ||
                    accountInfo.email!!.isEmpty() || accountInfo.location!!.isEmpty()
        }

        fun isEmailValid(email: CharSequence): Boolean {
            return Patterns.EMAIL_ADDRESS.matcher(email).matches()
        }

        fun someEmptyItemFields(itemInfo: FireItem): Boolean {
            return itemInfo.title.isEmpty() || itemInfo.price.isEmpty() ||
                    itemInfo.category.isEmpty() || itemInfo.location.isNullOrEmpty() ||
                    itemInfo.expDate.isEmpty() || itemInfo.picture_uri.isNullOrEmpty() ||
                    itemInfo.description.isEmpty()
        }

        fun titleTooLong(itemToSave: FireItem): Boolean {
            return itemToSave.title.length > 45
        }

        ///endregion

        ///region Miscellaneous functions

        fun readAccountJsonFromPreferences(parentActivity: AppCompatActivity): JSONObject? {

            val readFromSharePref = parentActivity.getSharedPreferences("account_info", Context.MODE_PRIVATE)
            val accountInfo = readFromSharePref.getString("account_info", null)

            return accountInfo?.let{ JSONObject(accountInfo) }
        }

        fun setNavHeaderView(headerView: View?, fullname: String?, email: String?, profilePicture: String?) {
            headerView?.findViewById<TextView>(R.id.full_name_navheader)?.text = fullname
            headerView?.findViewById<TextView>(R.id.email_navheader)?.text = email
            headerView?.let {
                Glide.with(it)
                    .load(profilePicture)
                    .error(R.drawable.default_profile_picture)
                    .into(headerView.findViewById<ImageView>(R.id.profile_picture_navheader))
            }
        }

        fun makeSnackbar(view: View, text: CharSequence) {
            Snackbar.make(view, text, Snackbar.LENGTH_LONG).show()
        }

        fun getResizedUri(pictureUri: String): String {
            val splitted : List<String> = pictureUri.split("?")
            val percentegaSplit = splitted[0].split("%")
            val finalUri = percentegaSplit[0] + "%" + percentegaSplit[1] + "_200x200?" + splitted[1]
            return finalUri
        }

        ///endregion

        ///region map

        fun moveToCurrentLocation(map: GoogleMap, pos: LatLng) {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 15F))
            map.animateCamera(CameraUpdateFactory.zoomIn())
            map.animateCamera(CameraUpdateFactory.zoomTo(15F), 2000, null)
        }
    }
}
