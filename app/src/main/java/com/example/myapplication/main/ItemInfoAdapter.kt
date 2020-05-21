package com.example.myapplication.main

import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageButton
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.data.FireItem
import com.example.myapplication.data.ITEMSTATUS
import com.example.myapplication.data.ItemStatusCreator
import com.example.myapplication.notifications.NOTIFICATION_TYPE
import com.example.myapplication.notifications.NotificationStore
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.item_card_view_star.view.*
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
        FirebaseFirestore.getInstance().collection("users").whereEqualTo("id", item.owner)
            .get()
            .addOnSuccessListener { documents ->
                val usernameOwner = documents.first()["username"] as String?

                //Bind to viewHolder, which sets the view contents
                holder.bind(item, usernameOwner)
                holder.setListeners(item)
            }
    }


    // This class gets the values of fields from the view, it is then
    // responsible to bind these values to a data class object
     class ItemInfoViewHolder(private val v : View) : RecyclerView.ViewHolder(v){

        fun bind(itemInfo: FireItem, usernameOwner: String?){

            val pictureURIView : ImageView = v.findViewById(R.id.item_card_picture)
            val title : TextView= v.findViewById(R.id.item_card_title)
            val location : TextView= v.findViewById(R.id.item_card_location)
            val price : TextView= v.findViewById(R.id.item_card_price)
            val seller : TextView = v.findViewById(R.id.Owner)

            if (itemInfo.picture_uri == null) {
                pictureURIView.setImageResource(R.drawable.default__item_image)
            }
            else {
                val imageURI : Uri? = Uri.parse(itemInfo.picture_uri)
                Glide.with(v.context)
                    .load(imageURI)
                    .centerCrop()
                    .into(v.findViewById(R.id.item_card_picture))
                // pictureURIView.setImageURI(imageURI)
            }

            title.text = itemInfo.title
            location.text = itemInfo.location
            price.text = itemInfo.price
            seller.text = "Sold by: " +  usernameOwner

            if(itemInfo.status == ItemStatusCreator.getStatus(ITEMSTATUS.SOLD) || itemInfo.status == ItemStatusCreator.getStatus(ITEMSTATUS.BLOCKED)){
                val starBtn : ImageButton = v.findViewById(R.id.starItemButton)
                starBtn.isClickable = false
                starBtn.backgroundTintList = ColorStateList.valueOf(Color.GRAY)
            }
         }


        fun setListeners(itemInfo : FireItem){
            val itemBundle = Bundle(2)
            itemBundle.putSerializable("item", itemInfo as Serializable?)
            itemBundle.putBoolean("myitems", false)

            v.setOnClickListener {
                v.findNavController().navigate(R.id.itemDetailsFragment, itemBundle)
            }

            // Navigate to fragment that allows editing of the selected item
            val saveButton : ImageButton = v.findViewById(R.id.starItemButton)

            if(itemInfo.status == ItemStatusCreator.getStatus(ITEMSTATUS.SOLD) || itemInfo.status == ItemStatusCreator.getStatus(ITEMSTATUS.BLOCKED)){
                saveButton.setOnClickListener{
                    Snackbar.make(v, "Item is already sold or blocked", Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .show()
                }
                return
            }


            saveButton.setOnClickListener {

                //Make item not clickable anymore
                saveButton.isClickable = false
                saveButton.backgroundTintList = ColorStateList.valueOf(Color.GRAY)

                //insert favorite in user
                FirebaseFirestore.getInstance().collection("users")
                    .document(FirebaseAuth.getInstance().currentUser!!.uid)
                    .update("favorites", FieldValue.arrayUnion(itemInfo.id))

                //insert favorite in item (for other queries)
                FirebaseFirestore.getInstance().collection("items").document(itemInfo.id)
                    .update("users_favorites", FieldValue.arrayUnion(FirebaseAuth.getInstance().currentUser!!.uid))

                //Notify owner
                val notificationStore : NotificationStore =
                    NotificationStore()
                notificationStore.apply {
                    postNotification(itemInfo.title, itemInfo.owner, NOTIFICATION_TYPE.INTERESTED)
                }

                Snackbar.make(v, "Item Added to Favourites", Snackbar.LENGTH_LONG)
                    .setAction("Action", null)
                    .show()
            }
        }
    }

}