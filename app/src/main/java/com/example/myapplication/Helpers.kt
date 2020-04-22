package com.example.myapplication

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Patterns
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import androidx.exifinterface.media.ExifInterface
import org.json.JSONObject
import java.io.IOException


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

        fun readJsonFromPreferences(parentActivity: AppCompatActivity): JSONObject? {

            val readFromSharePref = parentActivity.getSharedPreferences("account_info", Context.MODE_PRIVATE)
            val accountInfo = readFromSharePref.getString("account_info", null)

            return accountInfo?.let{ JSONObject(accountInfo) }
        }

        fun someEmptyFields(accountInfo: AccountInfo): Boolean {
            return accountInfo.fullname.isNullOrEmpty() || accountInfo.username.isNullOrEmpty() ||
                    accountInfo.email.isNullOrEmpty() || accountInfo.location.isNullOrEmpty()
        }

        fun isEmailValid(email: CharSequence): Boolean {
            return Patterns.EMAIL_ADDRESS.matcher(email).matches()
        }

        fun readItemJsonFromPreferences(parentActivity: AppCompatActivity): JSONObject? {

            val readFromSharePref = parentActivity.getSharedPreferences("item_info", Context.MODE_PRIVATE)
            val itemInfo = readFromSharePref.getString("item_info", null)

            return itemInfo?.let{ JSONObject(itemInfo) }
        }
    }
}
