package com.example.myapplication

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
import com.example.myapplication.data.FireItem
import com.example.myapplication.main.ItemInfoFactory
import com.google.android.material.snackbar.Snackbar
import org.json.JSONObject
import java.io.IOException
import java.io.Serializable


class Helpers(){

    companion object Helpers {

        ///region Image functions

        val rotateImage  =  { source: Bitmap, angle: Float ->
            val matrix = Matrix()
            matrix.postRotate(angle)
            Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
        }

        fun updatePicture(context: Context, profilePictureUri: Uri, picture: ImageView) {
            
            //var imageBitmap : Bitmap = bitma
            try {
                val imageBitmap =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        val source = ImageDecoder.createSource(context.contentResolver, profilePictureUri)
                        ImageDecoder.decodeBitmap(source)
                    }
                    else {
                        //IT'S AN URL
                        Glide.with(context).asBitmap().load(profilePictureUri).into(object : CustomTarget<Bitmap>() {
                            override fun onResourceReady(
                                resource: Bitmap,
                                transition: Transition<in Bitmap>?
                            ) {
                                //imageBitmap = resource
                            }

                            override fun onLoadCleared(placeholder: Drawable?) {
                                // this is called when imageView is cleared on lifecycle call or for
                                // some other reason.
                                // if you are referencing the bitmap somewhere else too other than this imageView
                                // clear it here as you can no longer have the bitmap
                            }
                        })
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

                picture.setImageBitmap(scaled)
            }
            catch(e: IOException) {
                e.printStackTrace()

                Toast.makeText(context, "Could not set picture", Toast.LENGTH_SHORT).show()
            }
        }

        ///endregion

        ///region defaultItem functions

        fun getDefaultItemBundle() : Bundle {

            val itemInfo = FireItem(
                ItemInfoFactory.defaultItemPhoto, "", "",
                "", "Arts & Crafts", "Painting, Drawing & Art Supplies", "", "", "", "-2")
            val itemBundle = Bundle(1)
            itemBundle.putSerializable("item", itemInfo as Serializable?)

            return itemBundle
        }

        fun getDefaultItem() : FireItem {

            return FireItem(
                ItemInfoFactory.defaultItemPhoto, "", "",
                "", "Arts & Crafts", "Painting, Drawing & Art Supplies", "", "", "", "-1")
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
                    itemInfo.expDate.isEmpty() || itemInfo.pictureURIString.isNullOrEmpty() ||
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

        fun setNavHeaderView(headerView: View?, fullname: String, email: String, profilePicture: String) {
            headerView?.findViewById<TextView>(R.id.full_name_navheader)?.text = fullname
            headerView?.findViewById<TextView>(R.id.email_navheader)?.text = email
            //headerView?.findViewById<ImageView>(R.id.profile_picture_navheader)?.setImageURI(Uri.parse(profilePicture))
            Glide.with(headerView!!).load(profilePicture).into(headerView.findViewById<ImageView>(R.id.profile_picture_navheader))
        }

        fun makeSnackbar(view: View, text: CharSequence) {
            Snackbar.make(view, text, Snackbar.LENGTH_LONG).show()
        }

        ///endregion
    }
}
