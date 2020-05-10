package com.example.myapplication.profile

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders.of
import androidx.navigation.findNavController
import com.example.myapplication.FirestoreViewModel
import com.example.myapplication.Helpers
import com.example.myapplication.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.internal.SignInButtonImpl
import com.google.android.gms.tasks.Task
import kotlinx.android.synthetic.main.fragment_show_profile.*


class ShowProfileFragment : Fragment() {

    private val RC_SIGN_IN = 343
    private lateinit var showProfileViewModel: ShowProfileViewModel
    private lateinit var firestoreViewModel: FirestoreViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        val gso =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()

        // Build a GoogleSignInClient with the options specified by gso.
        val googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        val account = GoogleSignIn.getLastSignedInAccount(requireActivity())
        firestoreViewModel = of(requireActivity()).get(FirestoreViewModel::class.java)
        showProfileViewModel = of(requireActivity()).get(ShowProfileViewModel::class.java)
        if(account != null) {
            return inflater.inflate(R.layout.fragment_show_profile, container, false)
        } else {
            val loginView = inflater.inflate(R.layout.fragment_login, container, false)
            val login : SignInButton = loginView.findViewById(R.id.google_signin_button)
            login.setOnClickListener { view ->
                val signInIntent = googleSignInClient.signInIntent
                startActivityForResult(signInIntent, RC_SIGN_IN)
            }
            return loginView
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val accountInfo = showProfileViewModel.accountInfo

        if(accountInfo.value == null) {
            //readSharedPreferences()
        }

        accountInfo.observe(requireActivity(), Observer {
            textViewFullNameShowProfile.text = it.fullname
            textViewUsernameShowProfile.text = it.username
            textViewUserEmailShowProfile.text = it.email
            textViewUserLocationShowProfile.text = it.location
            Helpers.updatePicture(
                this.requireContext(),
                Uri.parse(it.profilePicture),
                profile_picture
            )
            showProfileViewModel.setTempAccountInfo(it)
        })

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    fun handleSignInResult(completedTask : Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            // Signed in successfully, show authenticated UI.
            //updateUI(account)
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("problem", "signInResult:failed code=" + e.statusCode)
            //updateUI(null)
        }
    }
}
