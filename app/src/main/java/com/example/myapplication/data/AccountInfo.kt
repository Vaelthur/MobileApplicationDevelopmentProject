package com.example.myapplication.data

import android.content.Context
import android.net.Uri
import android.widget.EditText
import com.example.myapplication.main.MainActivity
import com.example.myapplication.profile.EditProfileFragment
import com.google.firebase.firestore.GeoPoint
import kotlinx.android.synthetic.main.fragment_edit_profile.*
import org.json.JSONObject
import java.io.Serializable


data class AccountInfo (
    var id: String? = null,
    var fullname: String? = null,
    var username: String? = null,
    var email: String? = null,
    var location: String? = null,
    var coord: GeoPoint? = null,
    var profilePicture: String? = "android.resource://com.example.myapplication/drawable/default_profile_picture")
    : Serializable { }

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
            val coord = GeoPoint(10.0,10.0)
            // TODO: start a getlastlocation to get real coords


            return AccountInfo(
                id,
                fullname,
                username,
                email,
                location,
                coord,
                profilePicture
            )
        }

        fun fromMapToObj(hash : Map<String, Any>?) : AccountInfo {
            return AccountInfo(
                hash?.get("id") as String?,
                hash?.get("fullname") as String?,
                hash?.get("username") as String?,
                hash?.get("email") as String?,
                hash?.get("location") as String?,
                hash?.get("coord") as GeoPoint?,
                hash?.get("profilePicture") as String?
            )
        }
    }
}
