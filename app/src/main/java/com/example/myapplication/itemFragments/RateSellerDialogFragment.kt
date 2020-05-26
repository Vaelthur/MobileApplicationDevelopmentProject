package com.example.myapplication.itemFragments

import androidx.fragment.app.DialogFragment
import android.app.Dialog
import android.app.SearchManager
import android.media.Rating
import android.os.Bundle
import android.widget.EditText
import android.widget.RatingBar
import androidx.appcompat.app.AlertDialog
import com.example.myapplication.R
import com.example.myapplication.data.ItemCategories
import com.example.myapplication.itemLists.FilterItemFragment

class RateSellerDialogFragment(itemDetailsFragment: ItemDetailsFragment, val ownerId : String) : DialogFragment() {

    private val title : String  = "Please rate the seller"
    private var listener: RateSellerListener = itemDetailsFragment

    interface RateSellerListener {
        fun onDialogPositiveClick(dialog: DialogFragment, rating: Float?, ownerId: String, comment: String?)
        fun onDialogNegativeClick(dialog: DialogFragment)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {

            val builder = AlertDialog.Builder(it)
            builder.setTitle(title)
            val ratingView = layoutInflater.inflate(R.layout.rating_view, null)
            builder.setView(ratingView)
            builder.setPositiveButton("OK")
                { _, _ ->
                    val ratingBar = ratingView.findViewById<RatingBar>(R.id.ratingBar)
                    val commentView = ratingView.findViewById<EditText>(R.id.commentText)
                    listener.onDialogPositiveClick(this, ratingBar.rating, ownerId, commentView.text.toString())
                }
                .setNegativeButton("Not now")
                { _, _ ->
                    listener.onDialogNegativeClick(this)
                }
                .setOnCancelListener {
                    listener.onDialogNegativeClick(this)
                }

            builder.setCancelable(true)
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}