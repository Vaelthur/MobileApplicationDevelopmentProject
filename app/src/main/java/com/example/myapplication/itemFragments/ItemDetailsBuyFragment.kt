package com.example.myapplication.itemFragments

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.example.myapplication.Helpers
import com.example.myapplication.R
import com.example.myapplication.data.FireItem
import com.example.myapplication.data.Item
import com.example.myapplication.notifications.NOTIFICATION_TYPE
import com.example.myapplication.notifications.NotificationStore
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.item_details_buy_fragment.*
import kotlinx.android.synthetic.main.item_details_fragment.*
import kotlinx.android.synthetic.main.item_details_fragment.item_category_value
import kotlinx.android.synthetic.main.item_details_fragment.item_condition_value
import kotlinx.android.synthetic.main.item_details_fragment.item_description_value
import kotlinx.android.synthetic.main.item_details_fragment.item_expire_date_value
import kotlinx.android.synthetic.main.item_details_fragment.item_location
import kotlinx.android.synthetic.main.item_details_fragment.item_location_value
import kotlinx.android.synthetic.main.item_details_fragment.item_picture
import kotlinx.android.synthetic.main.item_details_fragment.item_price
import kotlinx.android.synthetic.main.item_details_fragment.item_subcategory_value
import kotlinx.android.synthetic.main.item_details_fragment.item_title

class ItemDetailsBuyFragment: Fragment() {

    private lateinit var viewModel: ItemDetailsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.item_details_buy_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProviders.of(requireActivity()).get(ItemDetailsViewModel::class.java)

        arguments?. let {
            val incomingItem : FireItem = it.getSerializable("item") as FireItem
            viewModel.setItemInfo(incomingItem)
            viewModel.setTempItemInfo(incomingItem)
        }

        val itemInfo = viewModel.itemInfo
        if(itemInfo.value == Helpers.getDefaultItem()){
            this.activity?.findNavController(R.id.nav_host_fragment)?.popBackStack()
            return
        }

        val fab: View = requireActivity().findViewById(R.id.fab_star)
        fab.setOnClickListener { view ->
            //insert favorite in user
            FirebaseFirestore.getInstance().collection("users")
                .document(FirebaseAuth.getInstance().currentUser!!.uid)
                .update("favorites", FieldValue.arrayUnion(viewModel.itemInfo.value?.id!!))

            //insert favorite in item (for other queries)
            FirebaseFirestore.getInstance().collection("items").document(viewModel.itemInfo.value?.id!!)
                .update("users_favorites", FieldValue.arrayUnion(FirebaseAuth.getInstance().currentUser!!.uid))

            val notificationStore : NotificationStore =
                NotificationStore()
            notificationStore.apply {
                postNotification(viewModel.itemInfo.value?.title!!, viewModel.itemInfo.value?.owner!!, NOTIFICATION_TYPE.INTERESTED)
            }

            Snackbar.make(view, "Item Added to Favourites", Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .show()
        }

        viewModel.itemInfo.observe(requireActivity(), Observer {
            // also seller field needed
            item_title.text = it.title
            item_price.text = it.price
            item_location.text = it.location
            item_category_value.text = it.category
            item_subcategory_value.text = it.subCategory
            item_expire_date_value.text = it.expDate
            item_location_value.text = it.location
            item_condition_value.text = it.condition
            item_description_value.text = it.description

            Glide.with(requireContext())
                .load(it.picture_uri)
                .centerCrop()
                .into(item_picture)


            viewModel.setTempItemInfo(it)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.itemInfo.removeObservers(requireActivity())
    }
}