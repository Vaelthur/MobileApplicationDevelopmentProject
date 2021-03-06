package com.example.myapplication.profile

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.text.TextUtils
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders.of
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.example.myapplication.map.CustomMapView
import com.example.myapplication.R
import com.example.myapplication.data.AccountInfo
import com.example.myapplication.data.AccountInfoFactory
import com.example.myapplication.main.Helpers
import com.example.myapplication.main.MainActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_edit_profile.*
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class EditProfileFragment : Fragment(), OnMapReadyCallback {

    //requests and permissions codes
    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_IMAGE_GALLERY = 2
    private val PERMISSION_CODE_CAMERA = 1000
    private val PERMISSION_CODE_GALLERY = 1001

    private val PERMISSION_CODE_LOC = 100
    lateinit private var fusedLocationProviderClient: FusedLocationProviderClient
    var coordGP: GeoPoint = GeoPoint(0.0, 0.0)

    private var userAddress = "City"

    private lateinit var showProfileViewModel: ShowProfileViewModel

    ///region create/destroy functions

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
        showProfileViewModel.tempAccountInfo.value?.let {
            coordGP = it.coord!!
        }
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity() as MainActivity)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // check location permission

        if(ContextCompat.checkSelfPermission(requireActivity() as MainActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), PERMISSION_CODE_LOC)
        }

        // notify user to enable location services only if he granted permission but has LS disabled
        if (!isLocationEnabled(requireContext()) && ContextCompat.checkSelfPermission(requireActivity() as MainActivity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            Helpers.makeSnackbar(view, "Please enable location services")

        if(showProfileViewModel.tempAccountInfo.value == null) {
            setShowProfileViewModel()
        }

        showProfileViewModel.tempAccountInfo.observe(requireActivity(), Observer {
            editViewFullNameEditProfile.setText(it.fullname)
            editViewUsernameEditProfile.setText(it.username)
            editViewUserEmailEditProfile.setText(it.email)
            editViewUserLocationEditProfile.setText(it.location)
            Glide.with(requireContext())
                .load(it.profilePicture!!)
                .error(R.drawable.default_profile_picture)
                .centerCrop()
                .circleCrop()
                .into(profile_picture)
            val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
            if (sharedPref != null) {
                with (sharedPref.edit()) {
                    putString("profile_picture_editing", it.profilePicture.toString())
                    apply()
                }
            }
        })

        imageButtonChangePic.setOnClickListener {
            onImageButtonClickEvent(it)
        }

        buttonLocation.setOnClickListener {

            if(ContextCompat.checkSelfPermission(requireActivity() as MainActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), PERMISSION_CODE_LOC)
            }

            fusedLocationProviderClient.lastLocation.addOnSuccessListener {
                if (it != null) {
                    val geoCoder = Geocoder(requireContext(), Locale.getDefault())
                    val address: List<Address> = geoCoder.getFromLocation(it.latitude, it.longitude, 1)
                    val  userAddress = address[0].locality.toString() //This is the city
                    // set value in viewModel
                    coordGP = GeoPoint(it.latitude, it.longitude)
                    showProfileViewModel.tempAccountInfo.value?.coord = GeoPoint(it.latitude, it.longitude)
                    showProfileViewModel.tempAccountInfo.value?.location = userAddress
                    val mapView = view.findViewById<CustomMapView>(R.id.userLocation)
                    mapView.getMapAsync(this)

                } else {
                    if (!isLocationEnabled(requireContext()))
                        Helpers.makeSnackbar(view, "Please enable location services")
                    else Helpers.makeSnackbar(view, "It was not possible to retrieve your location. Please try again later")
                }
            }. addOnCompleteListener {
                editViewUserLocationEditProfile.setText(showProfileViewModel.tempAccountInfo.value?.location)

            }. addOnFailureListener {
                if (!isLocationEnabled(requireContext()))
                    Helpers.makeSnackbar(view, "Please enable location services")
                else Helpers.makeSnackbar(view, "It was not possible to retrieve your location. Please try again later")
            }
        }

        val mapView = view.findViewById<CustomMapView>(R.id.userLocation)
        mapView.onCreate(savedInstanceState)
        mapView.onResume()
        mapView.getMapAsync(this)
    }

    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        activity?.menuInflater?.inflate(R.menu.change_pic, menu)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.edit_profile_menu, menu)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        showProfileViewModel.tempAccountInfo.value = AccountInfoFactory.getAccountInfoFromTextEdit(this)
        showProfileViewModel.tempAccountInfo.removeObservers(requireActivity())
    }

///endregion

    fun isLocationEnabled(context: Context): Boolean {
        var locationMode = 0
        val locationProviders: String
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            locationMode = try {
                Settings.Secure.getInt(
                    context.contentResolver,
                    Settings.Secure.LOCATION_MODE
                )
            } catch (e: Settings.SettingNotFoundException) {
                e.printStackTrace()
                return false
            }
            locationMode != Settings.Secure.LOCATION_MODE_OFF
        } else {
            locationProviders = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.LOCATION_PROVIDERS_ALLOWED
            )
            !TextUtils.isEmpty(locationProviders)
        }
    }

    private fun onImageButtonClickEvent(it: View) {
        registerForContextMenu(it)
        requireActivity().openContextMenu(it)
        unregisterForContextMenu(it)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {

            R.id.takePicture -> {
                takePictureHandler()
                true
            }

            R.id.selectFromGallery -> {
                selectFromGalleryHandler()
                true
            }
            // Default case
            else -> super.onContextItemSelected(item)
        }
    }

    /// region Camera functions

    private fun takePictureHandler(){

        val permissions: Array<String> = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        if (checkCamera()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                openCamera(permissions)
            } else {
                //permission already granted or version in below marshmallow
                takePicture()
            }
        } else {
            Helpers.makeSnackbar(requireView(), "Camera not found")
        }
    }

    private fun checkCamera() : Boolean{
        return context?.packageManager?.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)!!
    }

    private fun openCamera(permissions: Array<String>? = null) {

        permissions?.let{
            // Take picture is already called in function onRequestPermissionsResult(), so we return
            checkAndAskPermissions(permissions, this.PERMISSION_CODE_CAMERA)
            return
        }

        takePicture()
    }

    private fun takePicture(){

        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val photoFile : File = createImageFile()
        val photoUri = FileProvider.getUriForFile(requireContext(), "MAD.shout.lab2.provider", photoFile)

        // Add photo_uri string to shared preferences
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        with (sharedPref.edit()) {
            putString("profile_picture_editing", photoUri.toString())
            commit()
        }

        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
    }

    /// endregion

    /// region Gallery functions

    private fun selectFromGalleryHandler(){

        val permissions: Array<String> = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            openGallery(permissions)
        }
        else {
            //permission already granted or version in below marshmallow
            takeFromGallery()
        }
    }

    private fun openGallery(permissions: Array<String>? = null) {

        permissions?.let{
            // Take picture is already called in function onRequestPermissionsResult(), so we return
            checkAndAskPermissions(permissions, this.PERMISSION_CODE_GALLERY)
            return
        }

        takeFromGallery()
    }

    private fun takeFromGallery() {

        val galleryIntent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        galleryIntent.type = "image/*"
        val mimeTypes = arrayOf("image/jpeg", "image/png")
        galleryIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        startActivityForResult(galleryIntent,  REQUEST_IMAGE_GALLERY)
    }

    /// endregion

    ///region ImageHandler functions

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Update view while editing profile picture
        if(resultCode == Activity.RESULT_OK) {
            when(requestCode) {
                REQUEST_IMAGE_CAPTURE -> imageCaptureHandler()
                REQUEST_IMAGE_GALLERY -> imageGalleryHandler(data?.data)
            }
        }
    }

    private fun imageGalleryHandler(profilePictureUri : Uri?) {
        profilePictureUri?.let {
            this.activity?.grantUriPermission(this.activity?.packageName, it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            this.activity?.contentResolver?.takePersistableUriPermission(it,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )

            //showProfileViewModel.tempAccountInfo.value?.profilePicture = profilePictureUri.toString()
            val readFromSharePref = (this.activity as AppCompatActivity).getPreferences(Context.MODE_PRIVATE)
            with(readFromSharePref.edit()) {
                putString("profile_picture_editing", profilePictureUri.toString())
                commit()
            }
            showProfileViewModel.tempAccountInfo.value = AccountInfoFactory.getAccountInfoFromTextEdit(this)
        }
    }

    private fun imageCaptureHandler() {
        showProfileViewModel.tempAccountInfo.value = AccountInfoFactory.getAccountInfoFromTextEdit(this)
    }

    ///endregion

    ///region Save functions

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.saveIconProfile -> {
                saveProfile()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun saveProfile() {

        hideSoftKeyboard(this.activity)

        //val accountInfo = AccountInfoFactory.getAccountInfoFromTextEdit(this)
        val accountInfo = showProfileViewModel.tempAccountInfo.value!!

        if(Helpers.someEmptyAccountFields(accountInfo)) {
            Helpers.makeSnackbar(this.requireView(), "Fill all the fields")
            return
        }

        if(!Helpers.isEmailValid(accountInfo.email!!)) {
            Helpers.makeSnackbar(this.requireView(), "Email format not valid")
            editViewUserEmailEditProfile.requestFocus()
            return
        }

        //Show progress bar while saving
        showProgressAndHide(requireView())

        //Save content to FireStore Database
        val db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val usersRef = db.collection("users")

        // V2 - very bad and ugly solution with polling
        runBlocking {
            val propicUri = accountInfo.profilePicture?.toUri()
            val storageReference = FirebaseStorage.getInstance().reference
            val imageRef = storageReference.child("propic/"+auth.currentUser?.uid!!)

            val uploadTask = propicUri?.let { imageRef.putFile(it) }
            if (uploadTask != null) {
                uploadTask.addOnSuccessListener {
                    val downloadUrl = imageRef.downloadUrl
                    downloadUrl.addOnSuccessListener {
                        val finalAC = AccountInfo(
                            accountInfo.id,
                            accountInfo.fullname,
                            accountInfo.username,
                            accountInfo.email,
                            accountInfo.location,
                            accountInfo.coord,
                            it.toString()
                        )
                        usersRef.document(auth.currentUser?.uid!!).set(finalAC)
                        showProfileViewModel.setAccountInfo(finalAC)
                    }
                }
                    .addOnFailureListener {
                        //if picture is not changed
                        usersRef.document(auth.currentUser?.uid!!).set(accountInfo)
                        showProfileViewModel.setAccountInfo(accountInfo)
                    }
                while(!uploadTask.isComplete){continue}
            }
        }

        this.requireActivity().getPreferences(Context.MODE_PRIVATE).edit().remove("profile_picture_editing").apply()

        //Return to ShowProfileActivity
        this.activity?.findNavController(R.id.nav_host_fragment)?.navigateUp()
        Helpers.makeSnackbar(requireView(), "Profile changed correctly")
    }

    private fun showProgressAndHide(view: View) {

        view.findViewById<ProgressBar>(R.id.progressBar).visibility = View.VISIBLE
        //view.findViewById<TextView>(R.id.saving_text).visibility = View.VISIBLE
        view.findViewById<CircleImageView>(R.id.profile_picture).visibility = View.GONE
        //fullname
        view.findViewById<TextView>(R.id.textViewFullNameEditProfile).visibility = View.GONE
        view.findViewById<TextView>(R.id.editViewFullNameEditProfile).visibility = View.GONE
        //location
        view.findViewById<TextView>(R.id.textViewUserLocationEditProfile).visibility = View.GONE
        view.findViewById<TextView>(R.id.editViewUserLocationEditProfile).visibility = View.GONE
        //username
        view.findViewById<TextView>(R.id.editViewUsernameEditProfile).visibility = View.GONE
        view.findViewById<TextView>(R.id.textViewUsernameEditProfile).visibility = View.GONE

        view.findViewById<Button>(R.id.buttonLocation).visibility = View.GONE
        view.findViewById<CustomMapView>(R.id.userLocation).visibility = View.GONE
        view.findViewById<ImageButton>(R.id.imageButtonChangePic).visibility = View.GONE
        //email
        view.findViewById<TextView>(R.id.editViewUserEmailEditProfile).visibility = View.GONE
        view.findViewById<TextView>(R.id.textViewUserEmailEditProfile).visibility = View.GONE
    }

    ///endregion

    /// region Helpers

    private fun createImageFile(): File {

        lateinit var file : File

        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        try {
            val storageDir: File? = activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            file = File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
        }
        catch (e: IOException){
            e.printStackTrace()
            Helpers.makeSnackbar(requireView(), "Could not set profile picture")
        }

        return file
    }

    @SuppressLint("NewApi")
    private fun checkAndAskPermissions(permissions: Array<String>, permissionCode : Int){

        val permissionsToRequest = ArrayList<String>()
        val createJavaArray = {index: Int -> permissionsToRequest[index] }

        for( permission in permissions){
            if(ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_DENIED){
                permissionsToRequest.add(permission)
            }
        }

        if(permissionsToRequest.isNotEmpty()) {
            this.requestPermissions(Array<String>(permissionsToRequest.size, createJavaArray),
                permissionCode)
        }
        else {
            when(permissionCode) {
                PERMISSION_CODE_CAMERA -> takePicture()
                PERMISSION_CODE_GALLERY -> takeFromGallery()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        //called when user presses ALLOW or DENY from Permission Request Popup

        for (result in grantResults){
            if(result != PackageManager.PERMISSION_GRANTED) {
                Helpers.makeSnackbar(requireView(), "Permission denied")
                return
            }
        }

        when(requestCode){
            PERMISSION_CODE_CAMERA -> takePicture()
            PERMISSION_CODE_GALLERY -> takeFromGallery()
        }
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
                    coordGP = showProfileViewModel.tempAccountInfo.value?.coord!!
                }
                else {
                    Helpers.makeSnackbar(requireView(), "Could not retrieve user info")
                }
            }
            .addOnFailureListener { Helpers.makeSnackbar(requireView(), "Could not retrieve user info") }
    }

    /// endregion

    override fun onMapReady(map: GoogleMap?) {


        // shows effective location of user
        val myPos = LatLng(coordGP.latitude, coordGP.longitude)
        map?.clear()
        map!!.addMarker(MarkerOptions().position(myPos))
        Helpers.moveToCurrentLocation(map,myPos)

        map!!.setOnMapClickListener {
            val newPos = LatLng(it.latitude, it.longitude)
            map.clear()
            map.addMarker(MarkerOptions().position(newPos))

            val geoCoder = Geocoder(requireContext(), Locale.getDefault())
            val address: List<Address> = geoCoder.getFromLocation(it.latitude, it.longitude, 1)
            if(address.isNotEmpty()) {
                if(!address[0].locality.isNullOrEmpty()) {
                    val userAddress = address[0].locality.toString()
                    showProfileViewModel.tempAccountInfo.value?.location = userAddress
                    editViewUserLocationEditProfile.setText(showProfileViewModel.tempAccountInfo.value?.location)
                }
            }
            showProfileViewModel.tempAccountInfo.value?.coord = GeoPoint(it.latitude,it.longitude)
            coordGP = GeoPoint(it.latitude, it.longitude)
            //move camera with style
            Helpers.moveToCurrentLocation(map,newPos)
        }
    }
}


