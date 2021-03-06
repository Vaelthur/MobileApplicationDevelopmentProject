package com.example.myapplication

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
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
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders.of
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.example.myapplication.data.FireItem
import com.example.myapplication.itemFragments.ItemDetailsViewModel
import com.example.myapplication.data.ItemCategories
import com.example.myapplication.data.ItemInfoFactory
import com.example.myapplication.main.Helpers
import com.example.myapplication.main.MainActivity
import com.example.myapplication.map.CustomMapView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_item_edit.*
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.IOException
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ItemEditFragment : Fragment(), OnMapReadyCallback {
    //requests and permissions codes
    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_IMAGE_GALLERY = 2
    private val PERMISSION_CODE_CAMERA = 1000
    private val PERMISSION_CODE_GALLERY = 1001

    private val PERMISSION_CODE_LOC = 100
    // var needed to differentiate change on spinners: when first entering the fragment end when changing inside the fragment
    private var pos=0
    var coordGPItem: GeoPoint = GeoPoint(0.0, 0.0)

    private lateinit var  viewModel: ItemDetailsViewModel
    lateinit private var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var itemMap : GoogleMap

    ///region create/destroy

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // view necessary to access gui elements
        val view = inflater.inflate(R.layout.fragment_item_edit, container, false)

        viewModel = of(requireActivity()).get(ItemDetailsViewModel::class.java)
        viewModel.tempItemInfo.value?.let {
            coordGPItem = GeoPoint(it.lat!!, it.lon!!)
        }
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity() as MainActivity)

        arguments?. let {
            val incomingItem : FireItem = it.getSerializable("item") as FireItem
            viewModel.setItemInfo(incomingItem)
            if(viewModel.tempItemInfo.value == null) {
                viewModel.setTempItemInfo(incomingItem)
                coordGPItem = GeoPoint(viewModel.tempItemInfo.value?.lat!!, viewModel.tempItemInfo.value?.lon!!)
            }
        }

        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        setSpinners(view)
        setDatePicker(view)

        viewModel.tempItemInfo.observe(requireActivity(), Observer{

            item_title_edit.setText(it.title)
            item_location_value.setText(it.location)
            item_price_edit.setText(it.price)
            item_expire_date_value.text = it.expDate
            item_picture_description_edit.setText(it.description)
            item_condition_value.setText(it.condition)
            category_spinner.setSelection(
                ItemCategories().getPosFromValue(it.category))
            subcategory_spinner.setSelection(
                ItemCategories()
                    .getSubPosFrom(it.subCategory, it.category))
            this.pos = ItemCategories().getPosFromValue(it.category)
            Glide.with(requireContext())
                .load(it.picture_uri)
                .centerCrop()
                .into(item_picture)
        })

        setLocateButtonListener(view)



        // Listener to change profile pic
        imageButtonChangePhoto.setOnClickListener {  onImageButtonClickEvent(it) }

        //update map
        val mapView = view.findViewById<CustomMapView>(R.id.itemLocationMap)
        mapView.onCreate(savedInstanceState)
        mapView.onResume()
        mapView.getMapAsync(this)
    }

    private fun setLocateButtonListener(view: View) {
        LocateButton.setOnClickListener {

            if(ContextCompat.checkSelfPermission(requireActivity() as MainActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), PERMISSION_CODE_LOC)
            }

            fusedLocationProviderClient.lastLocation.addOnSuccessListener {
                if (it != null) {
                    val myPos = LatLng(it.latitude, it.longitude)
                    updateMapLocation(itemMap, myPos)

                } else {
                    if (!isLocationEnabled(requireContext()))
                        Helpers.makeSnackbar(view, "Please enable location services")
                    else Helpers.makeSnackbar(view, "It was not possible to retrieve your location. Please try again later")
                }
            }. addOnCompleteListener {
                item_location_value.setText(viewModel.tempItemInfo.value?.location)

            }. addOnFailureListener {
                if (!isLocationEnabled(requireContext()))
                    Helpers.makeSnackbar(view, "Please enable location services")
                else Helpers.makeSnackbar(view, "It was not possible to retrieve your location. Please try again later")
            }
        }
    }

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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.item_edit_menu, menu)
    }

    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?) {
        activity?.menuInflater?.inflate(R.menu.change_pic, menu)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        hideSoftKeyboard(requireActivity())
        val tempItemInfo : FireItem= ItemInfoFactory.getItemInfoFromTextEdit(this,
            viewModel.tempItemInfo.value?.id)
        viewModel.setTempItemInfo(tempItemInfo)
        viewModel.tempItemInfo.removeObservers(requireActivity())
    }

    ///endregion functions



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

    ///region Camera functions

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
            putString("item_picture_editing", photoUri.toString())
            commit()
        }

        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
    }

    ///endregion

    ///region Gallery functions

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

    ///endregion

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

    private fun imageCaptureHandler() {
        viewModel.tempItemInfo.value = ItemInfoFactory.getItemInfoFromTextEdit(this, viewModel.tempItemInfo.value?.id)
    }

    private fun imageGalleryHandler(itemPictureUri : Uri?) {
        itemPictureUri?.let {
            this.activity?.grantUriPermission(this.activity?.packageName, it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            this.activity?.contentResolver?.takePersistableUriPermission(it,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )


            val readFromSharePref = (this.activity as AppCompatActivity).getPreferences(Context.MODE_PRIVATE)
            with(readFromSharePref.edit()) {
                putString("item_picture_editing", itemPictureUri.toString())
                commit()
            }

            viewModel.tempItemInfo.value = ItemInfoFactory.getItemInfoFromTextEdit(this, viewModel.tempItemInfo.value?.id)

        }
    }

    ///endregion

    ///region Save functions

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.save_action -> {
                saveEdits()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    @ExperimentalUnsignedTypes
    private fun saveEdits(){

        val itemID = viewModel.tempItemInfo.value?.id
        val itemLat= viewModel.tempItemInfo.value?.lat
        val itemLon = viewModel.tempItemInfo.value?.lon

        val itemToSave = if(itemID.equals("0")) {
            ItemInfoFactory.getItemInfoFromTextEdit(this, null)
        } else {
            ItemInfoFactory.getItemInfoFromTextEdit(this, itemID)
        }

        viewModel.itemInfo.value  = itemToSave
        viewModel.tempItemInfo.value  = itemToSave

        if(Helpers.someEmptyItemFields(itemToSave)) {
            Helpers.makeSnackbar(this.requireView(), "Fill all the fields")
            return
        }

        if(Helpers.titleTooLong(itemToSave)) {
            Helpers.makeSnackbar(this.requireView(), "Max 45 characters for the title")
            item_title_edit.requestFocus()
            return
        }

        hideSoftKeyboard(requireActivity())
        requireView().findViewById<ConstraintLayout>(R.id.constraintLayout).visibility = View.GONE
        requireView().findViewById<ImageView>(R.id.item_picture).visibility = View.GONE
        requireView().findViewById<ImageButton>(R.id.imageButtonChangePhoto).visibility = View.GONE
        requireView().findViewById<ProgressBar>(R.id.progressBar).visibility = View.VISIBLE

        // Save to DB or update item on DB
        val collectionRef = FirebaseFirestore.getInstance().collection("items")

        //update item picture

        runBlocking {
            val itemPictureUri = itemToSave.picture_uri?.toUri()
            val storageReference = FirebaseStorage.getInstance().reference
            val imageRef = storageReference.child("itempic/"+itemToSave.id)

            val uploadTask = itemPictureUri?.let { imageRef.putFile(it) }
            uploadTask?.let {
                it.addOnSuccessListener {
                    val downloadUri = imageRef.downloadUrl
                    downloadUri.addOnSuccessListener {
                        val finalItem = FireItem(it.toString(),
                            itemToSave.title,
                            itemToSave.location,
                            itemToSave.price,
                            itemToSave.category,
                            itemToSave.subCategory,
                            itemToSave.expDate,
                            itemToSave.condition,
                            itemToSave.description,
                            itemToSave.id,
                            itemToSave.owner,
                            itemLat,
                            itemLon,
                            itemToSave.status)
                        collectionRef.document(itemToSave.id).set(finalItem)
                        viewModel.setItemInfo(finalItem)
                        endingSave(finalItem)
                    }
                }
                    .addOnFailureListener {
                        collectionRef.document(itemToSave.id).set(itemToSave)
                        viewModel.itemInfo.value = itemToSave
                        endingSave(itemToSave)
                    }
            }
        }
    }

    private fun endingSave(finalItem: FireItem) {
        //save item in lista di item dell'utente che sta modificando l'item
        val userID = FirebaseAuth.getInstance().currentUser!!.uid
        FirebaseFirestore.getInstance().collection("users").document(userID).update("items", FieldValue.arrayUnion(finalItem.id))

        val itemBundle = Bundle(2)
        itemBundle.putSerializable("item", finalItem as Serializable?)
        itemBundle.putBoolean("myitems", true)
        this.activity?.findNavController(R.id.nav_host_fragment)?.popBackStack()
        this.activity?.findNavController(R.id.nav_host_fragment)?.popBackStack()
        this.activity?.findNavController(R.id.nav_host_fragment)?.navigate(R.id.itemDetailsFragment, itemBundle)
    }

    ///endregion

    ///region Helpers

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

    private fun createImageFile(): File {

        lateinit var file : File

        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        try {
            val storageDir: File? = activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            file = File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
        }
        catch (e: IOException){
            e.printStackTrace()
            Helpers.makeSnackbar(requireView(), "Could not set item picture")
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


    private fun setDatePicker(view : View) {

        // open datepicker and set text into textview
        val BtnDate = view.findViewById<Button>(R.id.button_edit_date)
        BtnDate.setOnClickListener {
            val cal = Calendar.getInstance()
            val y = cal.get(Calendar.YEAR)
            val m = cal.get(Calendar.MONTH)
            val d = cal.get(Calendar.DAY_OF_MONTH)

            val dpd = DatePickerDialog(requireContext(), DatePickerDialog.OnDateSetListener { view1, year, month, dayOfMonth ->
                val monthFinal = month+1
                // var needed to datecheck
                val today = Calendar.getInstance().time
                val buffer = Calendar.getInstance()
                buffer.set(Calendar.YEAR, year)
                buffer.set(Calendar.MONTH, month)
                buffer.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                if (buffer.time <= today)
                    Helpers.makeSnackbar(requireView(), "Cannot set a date before today as expire date.")
                else {
                    val selectedDate = "$dayOfMonth/$monthFinal/$year"
                    view.findViewById<TextView>(R.id.item_expire_date_value).text = selectedDate
                }
            }, y, m, d )
             dpd.show()
        }
    }

    private fun setSpinners(view: View) {

        val spinner = view.findViewById<Spinner>(R.id.category_spinner)
        val ad = ArrayAdapter<String>(view.context, android.R.layout.simple_spinner_item, ItemCategories()
            .getMainCategories())
        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = ad

        val subspinner = view.findViewById<Spinner>(R.id.subcategory_spinner)

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view1: View?,
                position: Int,
                id: Long
            ) {
                val tempsubcat = ArrayAdapter<String>(view.context, android.R.layout.simple_spinner_item, ItemCategories()
                    .getSubCategoriesFromMain(spinner.selectedItem.toString()))
                tempsubcat.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                subspinner.adapter = tempsubcat

                if( pos == spinner.selectedItemPosition ) {
                    subspinner.setSelection(
                        ItemCategories()
                            .getSubPosFrom(viewModel.tempItemInfo.value!!.subCategory,viewModel.tempItemInfo.value!!.category ))
                }
            }
        }
    }

    ///endregion

    override fun onMapReady(map: GoogleMap?) {

        itemMap = map!!
        //First tray to retrieve location from db
        val itemPos = LatLng(coordGPItem.latitude, coordGPItem.longitude)
        map.clear()
        map.addMarker(MarkerOptions().position(itemPos))
        Helpers.moveToCurrentLocation(map, itemPos)
        map.setOnMapClickListener {
            val newPos = LatLng(it.latitude, it.longitude)
            updateMapLocation(map, newPos)
        }
    }

    fun updateMapLocation(map : GoogleMap, newPos : LatLng) {
        map.clear()
        map.addMarker(MarkerOptions().position(newPos).title("Item position"))
        val geoCoder = Geocoder(requireContext(), Locale.getDefault())
        val address: List<Address> = geoCoder.getFromLocation(newPos.latitude, newPos.longitude, 1)
        if(address.isNotEmpty()) {
            if(!address[0].locality.isNullOrEmpty()) {
                val itemAddress = address[0].locality.toString()
                viewModel.tempItemInfo.value?.location = itemAddress
                item_location_value.setText(itemAddress)
            }
        }
        coordGPItem = GeoPoint(newPos.latitude, newPos.longitude)
        viewModel.tempItemInfo.value?.lat = newPos.latitude
        viewModel.tempItemInfo.value?.lon = newPos.longitude
        //move camera with style
        Helpers.moveToCurrentLocation(map,newPos)
    }
}
