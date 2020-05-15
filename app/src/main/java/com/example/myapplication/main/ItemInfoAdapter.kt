package com.example.myapplication.main

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
import com.example.myapplication.R
import com.example.myapplication.data.FireItem
import java.io.Serializable

class ItemInfoAdapter(private val items: List<FireItem>)
    : RecyclerView.Adapter<ItemInfoAdapter.ItemInfoViewHolder>() {

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemInfoViewHolder {
        val view  = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_card_view_star, parent, false)

        return ItemInfoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemInfoViewHolder, position: Int) {
        // Get the item
        val item = items[position]

        //Bind to viewHolder, which sets the view contents
        holder.bind(item)
        holder.setListeners(item)
    }


    // This class gets the values of fields from the view, it is then
    // responsible to bind these values to a data class object
     class ItemInfoViewHolder(private val v : View) : RecyclerView.ViewHolder(v){

        fun bind(itemInfo: FireItem){

            val pictureURIView : ImageView = v.findViewById(R.id.item_card_picture)
            val title : TextView= v.findViewById(R.id.item_card_title)
            val location : TextView= v.findViewById(R.id.item_card_location)
            val price : TextView= v.findViewById(R.id.item_card_price)

            if (itemInfo.picture_uri == null) {
                pictureURIView.setImageResource(R.drawable.default__item_image)
            }
            else {
                val imageURI : Uri? = Uri.parse(itemInfo.picture_uri)
                pictureURIView.setImageURI(imageURI)
            }

            title.text = itemInfo.title
            location.text = itemInfo.location
            price.text = itemInfo.price
         }

        fun setListeners(itemInfo : FireItem){
            val itemBundle = Bundle(1)
            itemBundle.putSerializable("item", itemInfo as Serializable?)

            v.setOnClickListener {
                v.findNavController().navigate(R.id.itemDetailsFragment, itemBundle)
            }

            // Navigate to fragment that allows editing of the selected item
            val editButton : ImageButton = v.findViewById(R.id.editItemButton)
            editButton.setOnClickListener {
                v.findNavController().navigate(R.id.itemDetailsFragment, itemBundle)
                v.findNavController().navigate(R.id.ItemEditFragment, itemBundle)
            }
        }
    }

}