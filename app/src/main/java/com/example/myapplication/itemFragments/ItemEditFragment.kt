package com.example.myapplication

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.myapplication.itemFragments.ItemDetailsViewModel
import com.example.myapplication.main.ItemCategories
import com.example.myapplication.main.ItemDetailsInfoData
import java.util.*
import com.example.myapplication.main.ItemInfoFactory
import androidx.lifecycle.ViewModelProviders.of
import androidx.navigation.findNavController
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_item_edit.*

class ItemEditFragment : Fragment() {

    private lateinit var  viewModel: ItemDetailsViewModel

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

        // click listener on Imagebutton
        view.findViewById<ImageButton>(R.id.imageButtonChangePhoto).setOnClickListener { onImageButtonClickEvent(it) }

        val spinner = view.findViewById<Spinner>(R.id.category_spinner)
        val ad = ArrayAdapter<String>(view.context, android.R.layout.simple_spinner_item, ItemCategories().getMainCategories())
        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = ad

        // open datepicker and set text into textview
        val BtnDate = view.findViewById<Button>(R.id.button_edit_date)
        BtnDate.setOnClickListener {
            val cal = Calendar.getInstance()
            val y = cal.get(Calendar.YEAR)
            val m = cal.get(Calendar.MONTH)
            val d = cal.get(Calendar.DAY_OF_MONTH)

            val dpd = DatePickerDialog(requireContext(), DatePickerDialog.OnDateSetListener { view1, year, month, dayOfMonth ->
                val t = "$dayOfMonth/$month/$year"
                view.findViewById<TextView>(R.id.item_expire_date_value).text = t
            }, y, m, d )
            dpd.show()
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // update gui
        viewModel.tempItemInfo.observe(requireActivity(), androidx.lifecycle.Observer {
            view.findViewById<EditText>(R.id.item_title_edit)?.setText(it.title)
            view.findViewById<EditText>(R.id.item_location_value)?.setText(it.location)
            view.findViewById<EditText>(R.id.item_price_edit)?.setText(it.price)
            view.findViewById<TextView>(R.id.item_expire_date_value)?.text = it.expDate
            view.findViewById<EditText>(R.id.item_picture_description_edit)?.setText(it.description)
            view.findViewById<EditText>(R.id.item_condition_value)?.setText(it.condition)
            view.findViewById<Spinner>(R.id.category_spinner)?.setSelection(ItemCategories().getPosFromValue(it.category))
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        val tempiteminfo = ItemInfoFactory.getItemInfoFromTextEdit(this)
        viewModel.setTempItemInfo(tempiteminfo)
        viewModel.tempItemInfo.removeObservers(requireActivity())
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Save instance state - all the views i need to save data from
        // TODO: put in outstate values / into
        val itemInfo = ItemInfoFactory.getItemInfoFromTextEdit(this)
        outState.putSerializable("itemInfo", itemInfo)
    }

    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        activity?.menuInflater?.inflate(R.menu.change_pic, menu)
    }

    private fun onImageButtonClickEvent(it: View) {
        registerForContextMenu(it)
        requireActivity().openContextMenu(it)
        unregisterForContextMenu(it)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.item_edit_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.save_action -> {
                saveEdits()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun saveEdits(){
        hideSoftKeyboard(requireActivity())
        val iteminfo = ItemInfoFactory.getItemInfoFromTextEdit(this)

        val jsonString = Gson().toJson(iteminfo)
        val sharedPref = (this.activity as AppCompatActivity).getSharedPreferences("item_info",  Context.MODE_PRIVATE) ?: return
        with (sharedPref.edit()) {
            putString("item_info", jsonString)
            commit()
        }

        viewModel.setItemInfo(iteminfo)
        this.activity?.findNavController(R.id.nav_host_fragment)?.popBackStack()
    }

    // Helpers
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
}
