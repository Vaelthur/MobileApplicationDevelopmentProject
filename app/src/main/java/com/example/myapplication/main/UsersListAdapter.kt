package com.example.myapplication.main

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.AccountInfo
import com.example.myapplication.R
import de.hdodenhof.circleimageview.CircleImageView
import java.io.Serializable

class UsersListAdapter(private val users: List<AccountInfo>)
    : RecyclerView.Adapter<UsersListAdapter.AccountInfoViewHolder>() {

    override fun getItemCount() = users.size

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AccountInfoViewHolder {
        val view  = LayoutInflater.from(parent.context).inflate(R.layout.profile_card_view, parent, false)

        return AccountInfoViewHolder(view)
    }

    override fun onBindViewHolder(holder: AccountInfoViewHolder, position: Int) {
        // Get the item
        val user = users[position]

        //Bind to viewHolder, which sets the view contents
        holder.bind(user)
        holder.setListeners(user)
    }


    // This class gets the values of fields from the view, it is then
    // responsible to bind these values to a data class object
    class AccountInfoViewHolder(private val v : View) : RecyclerView.ViewHolder(v){

        fun bind(accountInfo: AccountInfo){

            val pictureURIView : CircleImageView = v.findViewById(R.id.profile_picture_card)
            val username : TextView = v.findViewById(R.id.username_card)

            if (accountInfo.profilePicture == null) {
                pictureURIView.setImageResource(R.drawable.default_profile_picture)
            }
            else {
                val imageURI : Uri? = Uri.parse(accountInfo.profilePicture)
                // pictureURIView.setImageURI(imageURI) //fa crashare le mie cose questa causa permessi
            }

            username.text = accountInfo.username
        }

        fun setListeners(accountInfo : AccountInfo){
            val accountBundle = Bundle(2)
            accountBundle.putSerializable("account_info", accountInfo as Serializable?)
            accountBundle.putBoolean("myprofile", false)

            v.setOnClickListener {
                v.findNavController().navigate(R.id.showProfileFragment, accountBundle)
            }
        }
    }

}