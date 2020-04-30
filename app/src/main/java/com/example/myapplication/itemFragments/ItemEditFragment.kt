package com.example.myapplication

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders.of
import androidx.navigation.findNavController
import com.example.myapplication.data.Item
import com.example.myapplication.itemFragments.ItemDetailsViewModel
import com.example.myapplication.main.ItemCategories
import com.example.myapplication.main.ItemInfoFactory
import com.example.myapplication.main.ItemListViewModel
import kotlinx.android.synthetic.main.fragment_item_edit.*
import java.io.File
import java.io.IOException
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random

class ItemEditFragment : Fragment() {
    //requests and permissions codes
    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_IMAGE_GALLERY = 2
    private val PERMISSION_CODE_CAMERA = 1000
    private val PERMISSION_CODE_GALLERY = 1001

    // var needed to differentiate change on spinners: when first entering the fragment end when changing inside the fragment
    private var pos=0

    private lateinit var  viewModel: ItemDetailsViewModel


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

        arguments?. let {
            val incomingItem : Item = it.getSerializable("item") as Item
            viewModel.setItemInfo(incomingItem)
            if(viewModel.tempItemInfo.value == null) {
                viewModel.setTempItemInfo(incomingItem)
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
            category_spinner.setSelection(ItemCategories().getPosFromValue(it.category))
            subcategory_spinner.setSelection(ItemCategories().getSubPosFrom(it.subCategory, it.category))
            this.pos = ItemCategories().getPosFromValue(it.category)
            Helpers.updatePicture(this.requireContext(),
                Uri.parse(it.pictureURIString),
                item_picture)
        })

        // Listener to change profile pic
        imageButtonChangePhoto.setOnClickListener {  onImageButtonClickEvent(it) }
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
        val tempItemInfo = ItemInfoFactory.getItemInfoFromTextEdit(this,
            viewModel.tempItemInfo.value?.itemId)
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
        viewModel.tempItemInfo.value = ItemInfoFactory.getItemInfoFromTextEdit(this, viewModel.tempItemInfo.value?.itemId)
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

            viewModel.tempItemInfo.value = ItemInfoFactory.getItemInfoFromTextEdit(this, viewModel.tempItemInfo.value?.itemId)

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

        hideSoftKeyboard(requireActivity())
        val itemID = viewModel.tempItemInfo.value?.itemId
        val newItemID = Random.Default.nextInt(0, Int.MAX_VALUE)
        val itemToSave = if(itemID == 0) {
            ItemInfoFactory.getItemInfoFromTextEdit(this, newItemID)
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

        // Save to DB or update item on DB
        val itemListViewModel =
            of(requireActivity()).get(ItemListViewModel(requireActivity().application)::class.java)

        if(itemID == 0)
            itemListViewModel.insertAll(itemToSave)
        else
            itemListViewModel.updateItem(itemToSave)

        val itemBundle = Bundle(1)
        itemBundle.putSerializable("item", itemToSave as Serializable?)
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
        val ad = ArrayAdapter<String>(view.context, android.R.layout.simple_spinner_item, ItemCategories().getMainCategories())
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
                val tempsubcat = ArrayAdapter<String>(view.context, android.R.layout.simple_spinner_item, ItemCategories().getSubCategoriesFromMain(spinner.selectedItem.toString()))
                tempsubcat.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                subspinner.adapter = tempsubcat

                if( pos == spinner.selectedItemPosition ) {
                    subspinner.setSelection(ItemCategories().getSubPosFrom(viewModel.tempItemInfo.value!!.subCategory,viewModel.tempItemInfo.value!!.category ))
                }
            }
        }
    }

    ///endregion
}
