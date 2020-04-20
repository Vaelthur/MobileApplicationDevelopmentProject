package com.example.myapplication

import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
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
        val accountJson = Helpers.readJsonFromPreferences(parentActivity)

        accountJson?. let {
            textViewFullNameShowProfile.text = it["fullname"].toString()
            textViewUsernameShowProfile.text = it["username"].toString()
            textViewUserEmailShowProfile.text = it["email"].toString()
            textViewUserLocationShowProfile.text = it["location"].toString()
            Helpers.updateProfilePicture(this.requireContext(),
                Uri.parse(it["profilePicture"].toString()),
                profile_picture)
        }
    }
    /// endregion

}
