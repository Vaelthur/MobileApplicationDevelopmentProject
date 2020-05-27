package com.example.myapplication.profile

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders.of
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.data.AccountInfo
import com.example.myapplication.data.AccountInfoFactory
import com.example.myapplication.data.Rating
import com.example.myapplication.main.Helpers
import com.example.myapplication.main.MainActivity
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_show_profile.*
import kotlin.concurrent.fixedRateTimer


class ShowProfileFragment : Fragment(), OnMapReadyCallback {

    private val RC_SIGN_IN = 343
    private lateinit var showProfileViewModel: ShowProfileViewModel
    private var myProfile : Boolean? = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        arguments?.let {
            myProfile = it.getBoolean("myprofile")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        showProfileViewModel = of(requireActivity()).get(ShowProfileViewModel::class.java)

        //Set showProfileViewModel with db info
        if(!myProfile!!) {
            val accountInfo = arguments?.get("account_info") as AccountInfo
            showProfileViewModel.accountInfo.value = accountInfo
        }
        else {
            showProfileViewModel.accountInfo.value?.let {
                if(it.id != FirebaseAuth.getInstance().currentUser?.uid) {
                    setShowProfileViewModel()
                }
            } ?: setShowProfileViewModel()
        }


        return inflater.inflate(R.layout.fragment_show_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        if(myProfile!!) {
            textViewFullNameShowProfile.visibility = View.VISIBLE

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
                showProfileViewModel.setTempAccountInfo(it)
                setProfileNavHeaderHandler()
            })
        }
        else {

            //not my profile
            textViewFullNameShowProfile.visibility = View.GONE
            showProfileViewModel.accountInfo.observe(requireActivity(), Observer {
                textViewUsernameShowProfile.text = it.username
                textViewUserEmailShowProfile.text = it.email
                textViewUserLocationShowProfile.text = it.location
                Glide.with(requireContext())
                    .load(it.profilePicture)
                    .centerCrop()
                    .circleCrop()
                    .into(profile_picture)
            })

            //Query for user rating
            val userId = showProfileViewModel.accountInfo.value?.id
            FirebaseFirestore.getInstance().collection("ratings").document(userId!!)
                .get()
                .addOnSuccessListener { doc ->
                    if(doc != null && doc.data != null) {
                        val rating = Rating.fromMapToObj(doc["rating"] as Map<String, Any>?)
                        val floatRating = rating.meanRating.toFloat()
                        userRatingBar.rating = floatRating
                        val stringRating = "  " + floatRating.toString().substring(0, 3)
                        reviews_mean.text = stringRating
                        reviews_cnt.text = rating.ratingsReceived.toString()
                    }
                    else {
                        userRatingBar.rating = 0.0F
                        reviews_mean.text = "0"
                        reviews_cnt.text = "0"
                    }
                }
        }

        val mapView = view.findViewById<MapView>(R.id.userLocation)
        mapView.onCreate(savedInstanceState)
        mapView.onResume()
        mapView.getMapAsync(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        showProfileViewModel.accountInfo.removeObservers(requireActivity())
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        if(myProfile!!) {
            inflater.inflate(R.menu.show_profile_menu, menu)
        }
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


                    this.requireActivity().getPreferences(Context.MODE_PRIVATE).edit().remove("profile_picture_editing").apply()
                }

                else {
                    Helpers.makeSnackbar(requireView(), "Could not retrieve user info")
                }
            }
            .addOnFailureListener { Helpers.makeSnackbar(requireView(), "Could not retrieve user info") }
    }

    private fun setProfileNavHeaderHandler() {
        val navView : NavigationView? = requireActivity().findViewById(R.id.nav_view)
        val headerView : View? = navView?.getHeaderView(0)
        val accountInfo = showProfileViewModel.accountInfo.value
        accountInfo?.let {
            Helpers.setNavHeaderView(headerView, it.fullname!!, it.email!!, it.profilePicture!!)
        }
    }

    override fun onMapReady(map: GoogleMap?) {

        //for fun
        map!!.addMarker(
            MarkerOptions()
                .position(LatLng(0.0, 0.0))
                .title("Marker")
        )
    }
}
