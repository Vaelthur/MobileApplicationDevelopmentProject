package com.example.myapplication.profile

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.myapplication.AccountInfo
import com.example.myapplication.AccountInfoFactory
import com.example.myapplication.Helpers
import com.example.myapplication.R
import kotlinx.android.synthetic.main.fragment_show_profile.*

class ShowProfileFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        if(savedInstanceState == null){
            readSharedPreferences()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_show_profile, container, false)
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.show_profile_menu, menu)
    }

    /// region Helpers
    private fun readSharedPreferences() {

        val parentActivity = this.requireActivity() as AppCompatActivity
        val accountJson =
            Helpers.readJsonFromPreferences(
                parentActivity
            )

        accountJson?. let {
            textViewFullNameShowProfile.text = it["fullname"].toString()
            textViewUsernameShowProfile.text = it["username"].toString()
            textViewUserEmailShowProfile.text = it["email"].toString()
            textViewUserLocationShowProfile.text = it["location"].toString()
            Helpers.updateProfilePicture(
                this.requireContext(),
                Uri.parse(it["profilePicture"].toString()),
                profile_picture
            )
        }
    }
    /// endregion

    private fun updateAccountInfoView(extras: Bundle?) {

        val savedData: AccountInfo = extras?.getSerializable("accountInfo") as AccountInfo
        textViewFullNameShowProfile.text = savedData.fullname
        textViewUsernameShowProfile.text = savedData.username
        textViewUserEmailShowProfile.text = savedData.email
        textViewUserLocationShowProfile.text = savedData.location
        Helpers.updateProfilePicture(this.activity as Context, Uri.parse(savedData.profilePicture), profile_picture) //THIS WORKS SMOOTHLY
    }

}
