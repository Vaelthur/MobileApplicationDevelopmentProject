package com.example.myapplication.main

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R

class ItemInfoAdapter(private val items: List<ItemInfoData>) : RecyclerView.Adapter<ItemInfoAdapter.ItemInfoViewHolder>() {

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemInfoViewHolder {
        val view  = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_card_view, parent, false)

        view.setOnClickListener {
            parent.findNavController().navigate(R.id.itemDetailsFragment)
        }

        // Navigate to fragment that allows editing of the selected item
        val editButton : ImageButton = view.findViewById(R.id.editItemButton)
        editButton.setOnClickListener {
            parent.findNavController().navigate(R.id.ItemEditFragment)
        }

        return ItemInfoViewHolder(
            view
        )
    }

    override fun onBindViewHolder(holder: ItemInfoViewHolder, position: Int) {
        // Get the item
        val item = items[position]

        //Bind to viewHolder, which sets the view contents
        holder.bind(item)
    }

    // This class gets the values of fields from the view, it is then
    // responsible to bind these values to a data class object
     class ItemInfoViewHolder(v : View) : RecyclerView.ViewHolder(v){

        private val pictureURIView : ImageView = v.findViewById(R.id.item_card_picture)
        private val title : TextView= v.findViewById(R.id.item_card_title)
        private val location : TextView= v.findViewById(R.id.item_card_location)
        private val price : TextView= v.findViewById(R.id.item_card_price)

        fun bind(itemInfo: ItemInfoData){

             if (itemInfo.pictureURIString == null) {
                 pictureURIView.setImageResource(R.drawable.default__item_image)
             }
             else {
                 val imageURI : Uri? = Uri.parse(itemInfo.pictureURIString)
                 pictureURIView.setImageURI(imageURI)
             }

             title.text = itemInfo.title
             location.text = itemInfo.location
             price.text = itemInfo.price
         }

    }

}