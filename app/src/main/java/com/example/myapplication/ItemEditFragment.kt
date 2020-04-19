package com.example.myapplication

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.DatePicker
import android.widget.EditText
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ItemEditFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ItemEditFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val let = arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val eText = R.id.item_expire_date_value as EditText
        eText.setOnClickListener {
            val cal = Calendar.getInstance()
            val y = cal.get(Calendar.YEAR)
            val m = cal.get(Calendar.MONTH)
            val d = cal.get(Calendar.DAY_OF_MONTH)
            // show just datepicker (i hope)
            val dpg = context?.let { it1 -> DatePickerDialog(it1, DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                //TODO: save inserted parameters and show into textedit
            }, y, m , d ) }
            if (dpg != null) {
                dpg.show()
            }
        }
        return inflater.inflate(R.layout.fragment_item_edit, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.item_edit_menu, menu)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ItemEditFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ItemEditFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
