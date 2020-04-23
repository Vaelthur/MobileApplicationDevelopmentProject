package com.example.myapplication.profile

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.example.myapplication.AccountInfo
import com.example.myapplication.AccountInfoFactory
import com.example.myapplication.Helpers
import com.example.myapplication.R
import androidx.lifecycle.ViewModelProviders.of
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.fragment_show_profile.*

class ShowProfileFragment : Fragment() {

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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_show_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val accountInfo = showProfileViewModel.accountInfo
        if(accountInfo?.value == null) {
            readSharedPreferences()
        }
        accountInfo.observe(requireActivity(), Observer {
            textViewFullNameShowProfile.text = it.fullname
            textViewUsernameShowProfile.text = it.username
            textViewUserEmailShowProfile.text = it.email
            textViewUserLocationShowProfile.text = it.location
            Helpers.updateProfilePicture(
                this.requireContext(),
                Uri.parse(it.profilePicture),
                profile_picture
            )
            showProfileViewModel.setTempAccountInfo(it)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        showProfileViewModel.accountInfo?.removeObservers(requireActivity())
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
            Helpers.readJsonFromPreferences(
                parentActivity
            )

        accountJson?. let {
            showProfileViewModel.setAccountInfo(accountJson)
            showProfileViewModel.setTempAccountInfo(accountJson)
        }
    }
    /// endregion

}
