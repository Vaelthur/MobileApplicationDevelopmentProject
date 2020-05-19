package com.example.myapplication

import android.accounts.Account
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.example.myapplication.profile.ShowProfileViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import java.util.*

class SignInFragment : Fragment() {

    private val RC_SIGN_IN = 343

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_in, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val gso =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        // Build a GoogleSignInClient with the options specified by gso.
        val googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
        val login = view.findViewById<SignInButton>(R.id.google_signin_button)
        login.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    //this is called only if currentUser is null because otherwise we navigate to other views 8BUT IT IS Gà NULL BECAUSE THE CONTROL OF IT IS IN THE MAIN ACTIVITY!)
    fun handleSignInResult(completedTask : Task<GoogleSignInAccount>) {

        try {
            val account = completedTask.getResult(ApiException::class.java)
            firebaseAuthWithGoogle(account)
            //i put the user in the db just for check if everything is alright
            //check if user is present in db, if not, add it

        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("problem", "signInResult:failed code=" + e.statusCode)
            //updateUI(null)
        }
    }

    private fun firebaseAuthWithGoogle(accountGoogle : GoogleSignInAccount?) {

        val credential = GoogleAuthProvider.getCredential(accountGoogle?.idToken, null)
        val auth = (activity as MainActivity).getAuth()

        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    val googleAccountInfo = getGoogleSignInInfo(auth.currentUser!!)
                    // Nav header update
                    val navView: NavigationView = requireActivity().findViewById(R.id.nav_view)
                    changeNavHeader(navView, auth.currentUser!!)

                    //Search for user in database to determine if signup or signin
                    val db = FirebaseFirestore.getInstance()
                    val accountInfoQueryResult = db.collection("users").document(auth.currentUser!!.uid)
                    accountInfoQueryResult.get()
                        .addOnSuccessListener { accountDocument ->
                            if(accountDocument.data == null) {
                                //no user found => signup
                                signUp(db, googleAccountInfo)
                            }
                            else {
                                //user found => sign in
                                signIn(accountDocument)
                            }
                        }
                        .addOnFailureListener {  Helpers.makeSnackbar(requireView(), "Authentication Failed.") }
                }
                else {
                    Helpers.makeSnackbar(requireView(), "Authentication Failed.")
                }
            }
    }

    private fun signIn(accountDocument: DocumentSnapshot) {

        val data = accountDocument.data
        val accountInfo = AccountInfoFactory.fromMapToObj(data)

        val showProfileViewModel = ViewModelProviders.of(requireActivity()).get(ShowProfileViewModel::class.java)
        showProfileViewModel.accountInfo.value = accountInfo
        showProfileViewModel.tempAccountInfo.value = accountInfo

        val myBundle = Bundle()
        myBundle.putBoolean("myprofile", true)

        this.activity?.findNavController(R.id.nav_host_fragment)?.popBackStack()
        this.activity?.findNavController(R.id.nav_host_fragment)?.navigate(R.id.showProfileFragment, myBundle)
    }


    private fun signUp(db: FirebaseFirestore, googleAccountInfo: AccountInfo) {

        val accountBundle = Bundle(2)
        insertInDb(db, googleAccountInfo)
        //navigate to editProfile with bundle
        accountBundle.putSerializable("account_info", googleAccountInfo)
        accountBundle.putBoolean("myprofile", true)
        this.activity?.findNavController(R.id.nav_host_fragment)?.popBackStack()
        this.activity?.findNavController(R.id.nav_host_fragment)?.navigate(R.id.showProfileFragment, accountBundle)
        this.activity?.findNavController(R.id.nav_host_fragment)?.navigate(R.id.editProfileFragment, accountBundle)
    }

    private fun changeNavHeader(navView: NavigationView, account: FirebaseUser) {
        navView.menu.clear()
        navView.inflateMenu(R.menu.activity_main_drawer)
        navView.menu.findItem(R.id.logout_action).setOnMenuItemClickListener {
            // TODO: fix logout crash, activity is not attached to signin fragment
            val activity = getActivity()
            if (isAdded && activity != null) {
                (activity as MainActivity).logout(navView)
            } else false
        }

        //updateHeader
        Helpers.setNavHeaderView(
            navView.getHeaderView(0),
            account.displayName!!,
            account.email!!,
            account.photoUrl!!.toString())
    }

    private fun getGoogleSignInInfo(currentUser: FirebaseUser): AccountInfo {
        val id = currentUser.uid
        val fullname = currentUser.displayName
        val username = currentUser.displayName
        val email = currentUser.email
        val location = "City"
        val profilePicture = currentUser.photoUrl
        return AccountInfo(id,fullname,username,
            email!!,location,profilePicture.toString())
    }

    private fun insertInDb(database : FirebaseFirestore, googleAccountInfo: AccountInfo) {
        val usersRef = database.collection("users")
        usersRef.document(googleAccountInfo.id!!).set(googleAccountInfo)
    }

}
