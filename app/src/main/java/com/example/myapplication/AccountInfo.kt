package com.example.myapplication

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.myapplication.profile.EditProfileFragment
import com.google.android.gms.auth.api.signin.internal.Storage
import com.google.firebase.database.Exclude
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_edit_profile.*
import kotlinx.android.synthetic.main.fragment_show_profile.*
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.Serializable
import java.util.*
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.storage.FirebaseStorage


data class AccountInfo (
    var id: String? = null,
    var fullname: String? = null,
    var username: String? = null,
    var email: String? = null,
    var location: String? = null,
    var profilePicture: String? = "android.resource://com.example.myapplication/drawable/default_profile_picture")
    : Serializable {

//    fun toMap(): Map<String, Any?> {
//        return mapOf(
//            "fullname" to fullname,
//            "username" to username,
//            "email" to email,
//            "location" to location,
//            "profilePicture" to profilePicture
//        )
//    }
}

class AccountInfoFactory(){

    companion object AccountInfoFactory {

        const val defaultProfilePic = "android.resource://com.example.myapplication/drawable/default_profile_picture"


        fun getAccountInfoFromTextEdit(editProfileFragment: EditProfileFragment) : AccountInfo {


            val getEditViewText =
                { id: EditText -> id.text }

            val getProfilePicturePath : (editProfileFragment : EditProfileFragment) -> Uri =
                {
                    val readFromPref = it.requireActivity().getPreferences(Context.MODE_PRIVATE)
                    val tempProfilePicture = readFromPref.getString("profile_picture_editing", null)

                    if(tempProfilePicture == null) {
                        val readFromSharePref = it.requireActivity().getSharedPreferences("account_info", Context.MODE_PRIVATE)
                        val accountInfo = readFromSharePref.getString("account_info", null)

                        if(!accountInfo.isNullOrBlank()) {
                            val accountJson = JSONObject(accountInfo)
                            Uri.parse(accountJson["profilePicture"].toString())
                        }
                        else {
                            Uri.parse(defaultProfilePic)
                        }
                    }
                    else {
                        Uri.parse(tempProfilePicture)
                    }
                }

            val fullname = StringBuffer(getEditViewText(editProfileFragment.editViewFullNameEditProfile)).toString()
            val username = StringBuffer(getEditViewText(editProfileFragment.editViewUsernameEditProfile)).toString()
            val email = StringBuffer(getEditViewText(editProfileFragment.editViewUserEmailEditProfile)).toString()
            val location = StringBuffer(getEditViewText(editProfileFragment.editViewUserLocationEditProfile)).toString()
            val profilePicture = getProfilePicturePath(editProfileFragment).toString()
            val id = (editProfileFragment.requireActivity() as MainActivity).getAuth().currentUser?.uid


            return AccountInfo(
                id,
                fullname,
                username,
                email,
                location,
                profilePicture)
        }

        fun fromMapToObj(hash : Map<String, Any>?) : AccountInfo {
            return AccountInfo(
                hash?.get("id") as String?,
                                hash?.get("fullname") as String?,
                                hash?.get("username") as String?,
                                hash?.get("email") as String?,
                                hash?.get("location") as String?,
                                hash?.get("profilePicture") as String?)
        }

    }
}
