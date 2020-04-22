package com.example.myapplication.profile

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders.of
import androidx.navigation.findNavController
import com.example.myapplication.AccountInfo
import com.example.myapplication.AccountInfoFactory
import com.example.myapplication.Helpers
import com.example.myapplication.R
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_edit_profile.*


class EditProfileFragment : Fragment() {

    private lateinit var showProfileViewModel: ShowProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_edit_profile, container, false)
        showProfileViewModel = of(requireActivity()).get(ShowProfileViewModel::class.java)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        showProfileViewModel.getTempAccountInfo()?.observe(requireActivity(), Observer {
            editViewFullNameEditProfile.setText(it.fullname)
            editViewUsernameEditProfile.setText(it.username)
            editViewUserEmailEditProfile.setText(it.email)
            editViewUserLocationEditProfile.setText(it.location)
            Helpers.updateProfilePicture(
                this.requireContext(),
                Uri.parse(it.profilePicture),
                profile_picture
            )

        })

        val imageButton = view.findViewById<ImageButton>(R.id.imageButtonChangePic)
        imageButton.setOnClickListener {
            onImageButtonClickEvent(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        val tempAccountInfo = AccountInfoFactory.getAccountInfoFromTextEdit(this)
        showProfileViewModel.setTempAccountInfo(tempAccountInfo)
        showProfileViewModel.getTempAccountInfo()?.removeObservers(requireActivity())
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.saveIcon -> {
                saveProfile()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun saveProfile() {

        hideSoftKeyboard(this.activity)
        val accountInfo = AccountInfoFactory.getAccountInfoFromTextEdit(this.activity as AppCompatActivity)


        if(Helpers.someEmptyFields(accountInfo)) {
            Toast.makeText(this.context, "Fill all the fields", Toast.LENGTH_SHORT).show()
            return
        }

        if(!Helpers.isEmailValid(accountInfo.email)) {
            Toast.makeText(this.context, "Email format not valid", Toast.LENGTH_SHORT).show()
            editViewUserEmailEditProfile.requestFocus()
            return
        }

        // Save content to sharedPreferences
        val jsonString = Gson().toJson(accountInfo)
        val sharedPref = (this.activity as AppCompatActivity).getSharedPreferences("account_info",  Context.MODE_PRIVATE) ?: return
        with (sharedPref.edit()) {
            putString("account_info", jsonString)
            commit()
        }

        showProfileViewModel.setAccountInfo(accountInfo)
        //Return to ShowProfileActivity
        /*val startProfileEditIntent = Intent()
        val accountBundle = Bundle()
        accountBundle.putSerializable("accountInfo", accountInfo)
        startProfileEditIntent.putExtras(accountBundle)
        setResult(Activity.RESULT_OK ,startProfileEditIntent)*/
        //(this.activity as AppCompatActivity).onBackPressed()
        this.activity?.findNavController(R.id.nav_host_fragment)?.popBackStack()
    }

    private fun hideSoftKeyboard(activity: Activity?) {
        activity?.let {
            val inputManager =
                it.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if (activity.currentFocus != null) {
                inputManager.hideSoftInputFromWindow(activity.currentFocus!!.windowToken, 0)
                inputManager.hideSoftInputFromInputMethod(
                    activity.currentFocus!!.windowToken,
                    0
                )
            }
        }
    }

}


