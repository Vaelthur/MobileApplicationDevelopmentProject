package com.example.myapplication.profile

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders.of
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.example.myapplication.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_edit_profile.*
import kotlinx.android.synthetic.main.fragment_show_profile.*
import kotlinx.android.synthetic.main.fragment_show_profile.profile_picture


class ShowProfileFragment : Fragment() {

    private val RC_SIGN_IN = 343
    private lateinit var showProfileViewModel: ShowProfileViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        showProfileViewModel = of(requireActivity()).get(ShowProfileViewModel::class.java)
        return inflater.inflate(R.layout.fragment_show_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val accountInfo = showProfileViewModel.accountInfo

        if(accountInfo.value == null) {
            //readSharedPreferences()
        }

        showProfileViewModel.accountInfo.observe(requireActivity(), Observer {
            textViewFullNameShowProfile.text = it.fullname
            textViewUsernameShowProfile.text = it.username
            textViewUserEmailShowProfile.text = it.email
            textViewUserLocationShowProfile.text = it.location
            Glide.with(requireContext())
                .load(it.profilePicture)
                .centerCrop()
                .circleCrop()
                .into(profile_picture)
            /*Helpers.updatePicture(
                this.requireContext(),
                Uri.parse(it.profilePicture),
                profile_picture
            )*/
            showProfileViewModel.setTempAccountInfo(it)
        })

/*        arguments?.let {
            showProfileViewModel.setAccountInfo(requireArguments().get("account_info") as AccountInfo)
            arguments = null
        }*/

        this.requireActivity().getPreferences(Context.MODE_PRIVATE).edit().remove("profile_picture_editing").apply()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        showProfileViewModel.accountInfo.removeObservers(requireActivity())
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.show_profile_menu, menu)
        // inflater.inflate(R.menu.logout_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.editProfileIcon -> {
                this.activity?.findNavController(R.id.nav_host_fragment)?.navigate(R.id.editProfileFragment)
                true

            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /// region Helpers
    private fun readSharedPreferences() {

        val parentActivity = this.requireActivity() as AppCompatActivity
        val accountJson =
            Helpers.readAccountJsonFromPreferences(
                parentActivity
            )

        accountJson?. let {
            showProfileViewModel.setAccountInfo(accountJson)
            showProfileViewModel.setTempAccountInfo(accountJson)
        }
    }
    /// endregion

}
