package com.example.myapplication

import android.content.Context
import android.net.Uri
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.profile.EditProfileFragment
import kotlinx.android.synthetic.main.fragment_edit_profile.*
import kotlinx.android.synthetic.main.fragment_show_profile.*
import org.json.JSONObject
import java.io.Serializable


data class AccountInfo (val fullname: String, val username: String,
                        val email: String, val location: String, val profilePicture: String)
    : Serializable {}

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

            return AccountInfo(
                fullname,
                username,
                email,
                location,
                profilePicture)
        }


        fun getAccountInfoFromTextView(parentActivity: AppCompatActivity)
                : AccountInfo {

            val getTextViewText =
                { id: TextView -> id.text }

            val getProfilePicturePath : () -> Uri =
                {
                    val readFromSharePref = parentActivity.getSharedPreferences("account_info", Context.MODE_PRIVATE)
                    val accountInfo = readFromSharePref.getString("account_info", null)

                    if(!accountInfo.isNullOrBlank()) {
                        val accountJson = JSONObject(accountInfo)
                        Uri.parse(accountJson["profilePicture"].toString())
                    }
                    else {
                        Uri.parse(defaultProfilePic)
                    }
                }

            val fullname = StringBuffer(getTextViewText(parentActivity.textViewFullNameShowProfile)).toString()
            val username = StringBuffer(getTextViewText(parentActivity.textViewUsernameShowProfile)).toString()
            val email = StringBuffer(getTextViewText(parentActivity.textViewUserEmailShowProfile)).toString()
            val location = StringBuffer(getTextViewText(parentActivity.textViewUserLocationShowProfile)).toString()
            val profilePicture = getProfilePicturePath().toString()

            return AccountInfo(
                fullname,
                username,
                email,
                location,
                profilePicture)
        }
    }
}
