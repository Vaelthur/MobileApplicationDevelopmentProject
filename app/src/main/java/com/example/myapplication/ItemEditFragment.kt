package com.example.myapplication

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.InputType
import android.view.*
import android.widget.EditText
import androidx.fragment.app.Fragment
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
        val view = inflater.inflate(R.layout.fragment_item_edit, container, false)
        val eText = view.findViewById<EditText>(R.id.item_expire_date_value)
        eText.setOnClickListener {
            val cal = Calendar.getInstance()
            val y = cal.get(Calendar.YEAR)
            val m = cal.get(Calendar.MONTH)
            val d = cal.get(Calendar.DAY_OF_MONTH)
            // datepicker doesnt show up
            context?.let { it1 -> DatePickerDialog(it1, DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                val t = "$dayOfMonth/$month/$year"
                eText.setText(t)
            }, y, m , d ) }?.show()
        }
        return inflater.inflate(R.layout.fragment_item_edit, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.item_edit_menu, menu)
    }
}
