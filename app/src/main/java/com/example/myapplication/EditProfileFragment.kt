package com.example.myapplication

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_edit_profile.*


class EditProfileFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        savedInstanceState?.let {
            updateAccountInfoView(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_edit_profile, container, false)
        val imageButton = view.findViewById<ImageButton>(R.id.imageButtonChangePic)
        imageButton.setOnClickListener {
            onImageButtonClickEvent(it)
        }
        // Inflate the layout for this fragment
        return view
    }

    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        activity?.menuInflater?.inflate(R.menu.change_pic, menu)
    }

    private fun onImageButtonClickEvent(it: View) {
        registerForContextMenu(it)
        requireActivity().openContextMenu(it)
        unregisterForContextMenu(it)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.edit_profile_menu, menu)
    }

    private fun updateAccountInfoView(savedInstanceState: Bundle?) {

        val savedData: AccountInfo =
            savedInstanceState?.getSerializable("accountInfo") as AccountInfo
        val parentActivity = this.activity as AppCompatActivity

        // Update view
        editViewFullNameEditProfile.setText(savedData.fullname)
        editViewUsernameEditProfile.setText(savedData.username)
        editViewUserEmailEditProfile.setText(savedData.email)
        editViewUserLocationEditProfile.setText(savedData.location)
        Helpers.updateProfilePicture(
            parentActivity,
            Uri.parse(savedData.profilePicture),
            profile_picture
        )

        // Keep profile_picture_editing and view sync
        val writingToSharedPreferences = parentActivity.getPreferences(Context.MODE_PRIVATE)
        with(writingToSharedPreferences.edit()) {
            putString("profile_picture_editing", savedData.profilePicture)
            commit()
        }
    }
}


