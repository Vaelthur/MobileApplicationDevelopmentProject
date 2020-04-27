package com.example.myapplication

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.Matrix
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
import com.example.myapplication.data.Item
import com.example.myapplication.main.ItemInfoFactory
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import org.json.JSONObject
import java.io.IOException
import java.io.Serializable


class Helpers(){

    companion object Helpers {

        val rotateImage  =  { source: Bitmap, angle: Float ->
            val matrix = Matrix()
            matrix.postRotate(angle)
            Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
        }

        fun updateProfilePicture(context: Context, profilePictureUri: Uri, profile_picture: ImageView) {

            try {
                val imageBitmap =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        val source = ImageDecoder.createSource(context.contentResolver, profilePictureUri)
                        ImageDecoder.decodeBitmap(source)
                    }
                    else {
                        MediaStore.Images.Media.getBitmap(context.contentResolver, profilePictureUri)
                    }

                // Rotate if necessary
                val inputStream = context.contentResolver.openInputStream(profilePictureUri)
                val exif = inputStream?.let { ExifInterface(it) }
                val orientation : Int? = exif?.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED)

                val rotatedBitmap : Bitmap = when(orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(imageBitmap,90F)
                    ExifInterface.ORIENTATION_ROTATE_180 ->  rotateImage(imageBitmap,180F)
                    ExifInterface.ORIENTATION_ROTATE_270 ->  rotateImage(imageBitmap,270F)
                    else ->  imageBitmap
                }

                // Scale bitmap down
                val nh = (rotatedBitmap.height * (512.0 / rotatedBitmap.width)).toInt()
                val scaled = Bitmap.createScaledBitmap(rotatedBitmap, 512, nh, true)

                profile_picture.setImageBitmap(scaled)
            }
            catch(e: IOException) {
                e.printStackTrace()
                Toast.makeText(context, "Could not set profile picture", Toast.LENGTH_SHORT).show()
            }
        }

        fun updateItemPicture(context: Context, itemPictureUri: Uri, item_picture: ImageView) {

            try {
                val imageBitmap =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        val source = ImageDecoder.createSource(context.contentResolver, itemPictureUri)
                        ImageDecoder.decodeBitmap(source)
                    }
                    else {
                        MediaStore.Images.Media.getBitmap(context.contentResolver, itemPictureUri)
                    }

                // Rotate if necessary
                val inputStream = context.contentResolver.openInputStream(itemPictureUri)
                val exif = inputStream?.let { ExifInterface(it) }
                val orientation : Int? = exif?.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED)

                val rotatedBitmap : Bitmap = when(orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(imageBitmap,90F)
                    ExifInterface.ORIENTATION_ROTATE_180 ->  rotateImage(imageBitmap,180F)
                    ExifInterface.ORIENTATION_ROTATE_270 ->  rotateImage(imageBitmap,270F)
                    else ->  imageBitmap
                }

                // Scale bitmap down
                val nh = (rotatedBitmap.height * (512.0 / rotatedBitmap.width)).toInt()
                val scaled = Bitmap.createScaledBitmap(rotatedBitmap, 512, nh, true)

                item_picture.setImageBitmap(scaled)
            }
            catch(e: IOException) {
                e.printStackTrace()
                Toast.makeText(context, "Could not set item picture", Toast.LENGTH_SHORT).show()
            }
        }

        fun readAccountJsonFromPreferences(parentActivity: AppCompatActivity): JSONObject? {

            val readFromSharePref = parentActivity.getSharedPreferences("account_info", Context.MODE_PRIVATE)
            val accountInfo = readFromSharePref.getString("account_info", null)

            return accountInfo?.let{ JSONObject(accountInfo) }
        }

        fun someEmptyAccountFields(accountInfo: AccountInfo): Boolean {
            return accountInfo.fullname.isNullOrEmpty() || accountInfo.username.isNullOrEmpty() ||
                    accountInfo.email.isNullOrEmpty() || accountInfo.location.isNullOrEmpty()
        }

        fun isEmailValid(email: CharSequence): Boolean {
            return Patterns.EMAIL_ADDRESS.matcher(email).matches()
        }

        fun someEmptyItemFields(itemInfo: Item): Boolean {
            return itemInfo.title.isNullOrEmpty() || itemInfo.price.isNullOrEmpty() ||
                    itemInfo.category.isNullOrEmpty() || itemInfo.location.isNullOrEmpty() ||
                    itemInfo.expDate.isNullOrEmpty() || itemInfo.pictureURIString.isNullOrEmpty() ||
                    itemInfo.description.isNullOrEmpty()
        }

        fun readItemJsonFromPreferences(parentActivity: AppCompatActivity): JSONObject? {

            val readFromSharePref = parentActivity.getSharedPreferences("item_info", Context.MODE_PRIVATE)
            val itemInfo = readFromSharePref.getString("item_info", null)

            return itemInfo?.let{ JSONObject(itemInfo) }
        }

        fun setNavHeaderView(headerView: View?, fullname: String, email: String, profilePicture: String) {
            headerView?.findViewById<TextView>(R.id.full_name_navheader)?.text = fullname
            headerView?.findViewById<TextView>(R.id.email_navheader)?.text = email
            headerView?.findViewById<ImageView>(R.id.profile_picture_navheader)?.setImageURI(Uri.parse(profilePicture))
        }

        fun makeSnackbar(view: View, text: CharSequence) {
            Snackbar.make(view, text, Snackbar.LENGTH_LONG).show()
        }

        fun getDefaultItemBundle() : Bundle {

            val itemInfo = Item(
                ItemInfoFactory.defaultItemPhoto, "", "",
                "", "Arts & Crafts", "Painting, Drawing & Art Supplies", "", "", "")
            val itemBundle = Bundle(1)
            itemBundle.putSerializable("item", itemInfo as Serializable?)

            return itemBundle
        }

        fun getDefaultItem() : Item {

            return Item(
                ItemInfoFactory.defaultItemPhoto, "", "",
                "", "Arts & Crafts", "Painting, Drawing & Art Supplies", "", "", "")
        }

        fun titleTooLong(itemToSave: Item): Boolean {
            return itemToSave.title.length > 45
        }
    }
}
