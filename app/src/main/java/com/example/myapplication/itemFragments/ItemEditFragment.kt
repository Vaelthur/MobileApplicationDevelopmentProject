package com.example.myapplication

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.myapplication.main.ItemDetailsInfoData
import java.util.*
import com.example.myapplication.main.ItemInfoFactory
import kotlinx.android.synthetic.main.fragment_item_edit.*

class ItemEditFragment : Fragment() {

    // views
    val titleV = view?.findViewById<EditText>(R.id.item_title_edit)
    val locationV = view?.findViewById<EditText>(R.id.item_location_value)
    val priceV = view?.findViewById<EditText>(R.id.item_price_edit)
    val categoryV = view?.findViewById<Spinner>(R.id.category_spinner)
    val expDateV = view?.findViewById<TextView>(R.id.item_expire_date_value)
    val conditionV = view?.findViewById<EditText>(R.id.item_condition_value)
    val descriptionV = view?.findViewById<EditText>(R.id.item_picture_description_edit)
    val itemPhotoV = view?.findViewById<ImageView>(R.id.imageView)

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

        // click listener on Imagebutton
        view.findViewById<ImageButton>(R.id.imageButtonChangePhoto).setOnClickListener { onImageButtonClickEvent(it) }

        // random array to see if spinner works + spinner init
        val rndm = arrayOf("Arts", "Sports", "Baby", "Woman", "Man", "Electronics", "Games", "Automotive")
        val spinner = view.findViewById<Spinner>(R.id.category_spinner)
        val ad = ArrayAdapter<String>(view.context, android.R.layout.simple_spinner_item, rndm)
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
        if (savedInstanceState != null) {
            updateItemInfoView(savedInstanceState)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (savedInstanceState != null) {
            // Restore Instance state
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Save instance state - all the views i need to save data from
        // TODO: put in outstate values / into
        val itemInfo = ItemInfoFactory.getItemInfoFromTextEdit(this.activity as AppCompatActivity)
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

    // Helpers
    private fun updateItemInfoView(savedInstanceState: Bundle?){
        val savedData:ItemDetailsInfoData = savedInstanceState?.getSerializable("itemInfo") as ItemDetailsInfoData

        titleV?.setText(savedData.title)
        locationV?.setText(savedData.location)
        priceV?.setText(savedData.price)
        expDateV?.text = savedData.expDate
        descriptionV?.setText(savedData.description)
        conditionV?.setText(savedData.condition)
        categoryV?.setSelection(savedData.category)
    }
}
