package com.example.myapplication

import android.content.Context
import android.net.Uri
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.profile.EditProfileFragment
import com.google.firebase.database.Exclude
import kotlinx.android.synthetic.main.fragment_edit_profile.*
import kotlinx.android.synthetic.main.fragment_show_profile.*
import org.json.JSONObject
import java.io.Serializable


data class AccountInfo (
    var id: String? = null,
    var fullname: String? = null,
    var username: String? = null,
    var email: String? = null,
    var location: String? = null,
    var profilePicture: String? = "android.resource://com.example.myapplication/drawable/default_profile_picture")
    : Serializable {

    fun toMap(): Map<String, Any?> {
        return mapOf(
            "fullname" to fullname,
            "username" to username,
            "email" to email,
            "location" to location,
            "profilePicture" to profilePicture
        )
    }
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
                hash?.get("id") as String,
                                hash["fullname"] as String,
                                hash["username"] as String,
                                hash["email"] as String,
                                hash["location"] as String,
                                hash["profilePicture"] as String)
        }

    }
}
