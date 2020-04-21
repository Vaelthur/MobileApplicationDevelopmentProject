package com.example.myapplication.profile

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.myapplication.AccountInfo
import com.example.myapplication.Helpers
import com.example.myapplication.R
import kotlinx.android.synthetic.main.fragment_edit_profile.*
import com.example.myapplication.AccountInfoFactory


class EditProfileFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_edit_profile, container, false)
        // Inflate the layout for this fragment
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        arguments?.let {
            updateAccountInfoView(it)
        }
        savedInstanceState?.let {
            updateAccountInfoView(it)
        }
        val imageButton = view.findViewById<ImageButton>(R.id.imageButtonChangePic)
        imageButton.setOnClickListener {
            onImageButtonClickEvent(it)
        }
    }

    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        activity?.menuInflater?.inflate(R.menu.change_pic, menu)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val accountInfo = AccountInfoFactory.getAccountInfoFromTextEdit(this.activity as AppCompatActivity)
        outState.putSerializable("accountInfo", accountInfo)
    }

 /*   override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        savedInstanceState?.let {
            updateAccountInfoView(it)
        }
    }*/

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


