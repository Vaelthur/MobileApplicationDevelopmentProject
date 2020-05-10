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
import kotlinx.android.synthetic.main.fragment_show_profile.*


class ShowProfileFragment : Fragment() {

    private val RC_SIGN_IN = 343
    private lateinit var showProfileViewModel: ShowProfileViewModel
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
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
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        // Build a GoogleSignInClient with the options specified by gso.
        val googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);

        val currentUser = auth.currentUser
        val account = GoogleSignIn.getLastSignedInAccount(requireActivity())

        showProfileViewModel = of(requireActivity()).get(ShowProfileViewModel::class.java)
        if(currentUser != null) {
            //updateUI??
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
            firebaseAuthWithGoogle(account?.idToken!!)
            val id = account?.id
            val fullname = account?.givenName+" "+account?.familyName
            val username = account?.givenName?.toLowerCase()+account?.familyName?.toLowerCase()
            val email = account?.email
            val location = ""
            val profilePicture = account?.photoUrl
            val googleAccountInfo = AccountInfo(id,fullname,username,
                    email!!,location,profilePicture.toString())

            //retrieve user from db if exists
            val db = FirebaseFirestore.getInstance()
            val usersRef = db.collection("users")
            val userInDb = usersRef.whereEqualTo("email",email)

            /*userInDb.get().addOnSuccessListener {
                if(it.documents.isEmpty()) {
                    //signup
                    showProfileViewModel.setTempAccountInfo(googleAccountInfo)
                    insertInDb(db,googleAccountInfo) //TODO: da spostare
                    this.activity?.findNavController(R.id.nav_host_fragment)?.navigate(R.id.editProfileFragment)
                }
                else {
                    //signin
                    for(accountSnapshot in it.documents) {
                        val accountInfo = accountSnapshot.toObject(AccountInfo::class.java)
                        this.activity?.findNavController(R.id.nav_host_fragment)?.navigate(R.id.nav_itemList)
                    }
                }

            }*/
            // Signed in successfully, show authenticated UI.
            //updateUI(account)
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("problem", "signInResult:failed code=" + e.statusCode)
            //updateUI(null)
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Helpers.makeSnackbar(requireView(), "Authentication Success.")
                    val user = auth.currentUser
                    val displayName = user?.displayName
                    val i = 0
                    //create accountInfo, put in a bundle, navigate to edit??
                    //updateUI(user)

                } else {
                    Helpers.makeSnackbar(requireView(), "Authentication Failed.")
                    //updateUI(null)
                }
            }
    }

    private fun insertInDb(database : FirebaseFirestore, googleAccountInfo: AccountInfo) {
        val usersRef = database.collection("users")
        usersRef.document(googleAccountInfo.id!!).set(googleAccountInfo)
    }
}
