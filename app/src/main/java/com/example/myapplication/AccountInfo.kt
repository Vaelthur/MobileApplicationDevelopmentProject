package com.example.myapplication

import android.content.Context
import android.net.Uri
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
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

        fun getAccountInfoFromTextEdit(parentActivity: AppCompatActivity)
                : AccountInfo {

            val getEditViewText =
                { id: EditText -> id.text }

            val getProfilePicturePath : () -> Uri =
                {   val readFromSharePref = parentActivity.getPreferences(Context.MODE_PRIVATE)
                    Uri.parse(readFromSharePref.getString("profile_picture_editing", defaultProfilePic )) }

            val fullname = StringBuffer(getEditViewText(parentActivity.editViewFullNameEditProfile)).toString()
            val username = StringBuffer(getEditViewText(parentActivity.editViewUsernameEditProfile)).toString()
            val email = StringBuffer(getEditViewText(parentActivity.editViewUserEmailEditProfile)).toString()
            val location = StringBuffer(getEditViewText(parentActivity.editViewUserLocationEditProfile)).toString()
            val profilePicture= getProfilePicturePath().toString()

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
