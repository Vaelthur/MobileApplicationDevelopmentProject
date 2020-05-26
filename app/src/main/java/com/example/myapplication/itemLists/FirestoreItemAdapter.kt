package com.example.myapplication.itemLists

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.data.FireItem
import com.example.myapplication.main.MainActivity
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import kotlinx.android.extensions.LayoutContainer
import java.io.Serializable


class FirestoreItemAdapter(options : FirestoreRecyclerOptions<FireItem>, filterMyItems : Boolean = false)
    :  FirestoreRecyclerAdapter<FireItem, FirestoreItemAdapter.ItemInfoViewHolder>(options) {

    var userUid : String =     (options.owner as MainActivity).getAuth().currentUser!!.uid

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemInfoViewHolder {
        val view  = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_card_view, parent, false)

        return ItemInfoViewHolder(
            view
        )
    }

    override fun onBindViewHolder(holder: ItemInfoViewHolder, position: Int, item : FireItem) {
        // Get the item
        //val item = items[position]

        //Bind to viewHolder, which sets the view contents
        holder.bind(item)
        holder.setListeners(item)
    }

    // This class gets the values of fields from the view, it is then
    // responsible to bind these values to a data class object
    class ItemInfoViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer{

        fun bind(itemInfo: FireItem){

            val pictureURIView : ImageView = containerView.findViewById(R.id.item_card_picture)
            val title : TextView = containerView.findViewById(R.id.item_card_title)
            val location : TextView = containerView.findViewById(R.id.item_card_location)
            val price : TextView = containerView.findViewById(R.id.item_card_price)

            if (itemInfo.picture_uri == null) {
                pictureURIView.setImageResource(R.drawable.default__item_image)
            }
            else {
                val imageURI : Uri? = Uri.parse(itemInfo.picture_uri)
                Glide.with(containerView.context)
                    .load(imageURI)
                    .centerCrop()
                    .into(containerView.findViewById(R.id.item_card_picture))
                
            }

            title.text = itemInfo.title
            location.text = itemInfo.location
            price.text = itemInfo.price
        }

        fun setListeners(itemInfo : FireItem){
            val itemBundle = Bundle(2)
            itemBundle.putSerializable("item", itemInfo as Serializable?)
            itemBundle.putBoolean("myitems", true)

            containerView.setOnClickListener {

                containerView.findNavController().navigate(R.id.itemDetailsFragment, itemBundle)
            }

            // Navigate to fragment that allows editing of the selected item
            val editButton : ImageButton = containerView.findViewById(R.id.editItemButton)
            editButton.setOnClickListener {
                containerView.findNavController().navigate(R.id.itemDetailsFragment, itemBundle)
                containerView.findNavController().navigate(R.id.ItemEditFragment, itemBundle)
            }
        }
    }
}