package com.example.myapplication.itemLists

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.myapplication.data.ItemCategories

class FilterItemFragment(onSaleListFragment: OnSaleListFragment) : DialogFragment() {

    private var listener: FilterItemListener = onSaleListFragment

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    interface FilterItemListener {
        fun onDialogPositiveClick(dialog: DialogFragment, selectedItems: ArrayList<Int>)
        fun onDialogNegativeClick(dialog: DialogFragment)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val selectedItems = ArrayList<Int>() //Selected categories
            val builder = AlertDialog.Builder(it)
            builder?.setTitle("Filter Items")
                .setMultiChoiceItems(
                    ItemCategories().getMainCategories(), null
                ) { dialog, which, isChecked ->
                    if (isChecked) {
                        // If the user checked the item, add it to the selected items
                        selectedItems.add(which)
                    } else if (selectedItems.contains(which)) {
                        // Else, if the item is already in the array, remove it
                        selectedItems.remove(Integer.valueOf(which))
                    }
                }
                // Set the action buttons
                .setPositiveButton(
                    "OK"
                ) { dialog, id ->
                    listener.onDialogPositiveClick(this, selectedItems)
                }
                .setNegativeButton(
                    "Cancel"
                ) { dialog, id ->
                    listener.onDialogNegativeClick(this)
                }

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

}




