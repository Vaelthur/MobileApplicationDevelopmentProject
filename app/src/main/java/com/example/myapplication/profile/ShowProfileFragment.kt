package com.example.myapplication.profile

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders.of
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.example.myapplication.AccountInfoFactory
import com.example.myapplication.Helpers
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_show_profile.*


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

        if(showProfileViewModel.accountInfo.value == null){
            //Set showProfileViewModel with db info
            setShowProfileViewModel()
        }

        return inflater.inflate(R.layout.fragment_show_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

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

    private fun setShowProfileViewModel() {

        val auth = (activity as MainActivity).getAuth()
        //Search for user in database to determine if signup or signin
        val db = FirebaseFirestore.getInstance()
        val accountInfoQueryResult = db.collection("users").document(auth.currentUser!!.uid)
        accountInfoQueryResult.get()
            .addOnSuccessListener { accountDocument ->
                if (accountDocument.data != null) {
                    val accountInfo = AccountInfoFactory.fromMapToObj(accountDocument.data)
                    showProfileViewModel.accountInfo.value = accountInfo
                    showProfileViewModel.tempAccountInfo.value = accountInfo
                }
                else {
                    Helpers.makeSnackbar(requireView(), "Could not retrieve user info")
                }
            }
            .addOnFailureListener { Helpers.makeSnackbar(requireView(), "Could not retrieve user info") }
    }
}
