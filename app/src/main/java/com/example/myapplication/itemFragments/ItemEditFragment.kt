package com.example.myapplication

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.InputType
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_item_edit.*
import java.util.*

class ItemEditFragment : Fragment() {


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
}
