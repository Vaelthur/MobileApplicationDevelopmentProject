package com.example.myapplication.itemFragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders.of
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.Helpers
import com.example.myapplication.R
import com.example.myapplication.data.FireItem
import com.example.myapplication.main.UsersListAdapter
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


class ItemDetailsFragment : Fragment() {

    private lateinit var viewModel: ItemDetailsViewModel
    private var myItems : Boolean? = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myItems = arguments?.getBoolean("myitems")
        if(myItems!!) {
            setHasOptionsMenu(true)
        } else {
            setHasOptionsMenu(false)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return if(myItems!!)
                    inflater.inflate(R.layout.item_details_fragment, container, false)
                else
                    inflater.inflate(R.layout.item_details_buy_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        viewModel = of(requireActivity()).get(ItemDetailsViewModel::class.java)

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

        if(myItems!!){
            //Here personal items

            //get users interested

            viewModel.interestedUsers(itemInfo.value!!.id)

            viewModel.interestedLiveData.observe(requireActivity(), Observer {
                val recyclerView : RecyclerView? = view.findViewById(R.id.fav_users)
                recyclerView?.layoutManager = LinearLayoutManager(context)
                recyclerView?.adapter = UsersListAdapter(it)
            })

            //set listener block button
            blockButton.setOnClickListener { v ->
                val blockedItem = FireItem(viewModel.itemInfo.value!!.picture_uri, viewModel.itemInfo.value!!.title,
                    viewModel.itemInfo.value!!.location, viewModel.itemInfo.value!!.price, viewModel.itemInfo.value!!.category,
                    viewModel.itemInfo.value!!.subCategory, viewModel.itemInfo.value!!.expDate, viewModel.itemInfo.value!!.condition,
                    viewModel.itemInfo.value!!.description, viewModel.itemInfo.value!!.id, viewModel.itemInfo.value!!.owner, "Blocked")
                viewModel.setItemInfo(blockedItem)
                item_status.text = "Blocked"
                FirebaseFirestore.getInstance().collection("items").document(viewModel.itemInfo.value!!.id).set(blockedItem)
            }

            viewModel.itemInfo.observe(requireActivity(), Observer {
                item_title.text = it.title
                item_price.text = it.price
                item_location.text = it.location
                item_category_value.text = it.category
                item_subcategory_value.text = it.subCategory
                item_expire_date_value.text = it.expDate
                item_location_value.text = it.location
                item_condition_value.text = it.condition
                item_description_value.text = it.description
                item_status.text = it.status

                Glide.with(requireContext())
                    .load(it.picture_uri)
                    .centerCrop()
                    .into(item_picture)
//                Helpers.updatePicture(this.requireContext(),
//                    Uri.parse(it.picture_uri.toString()),
//                    item_picture)

                viewModel.setTempItemInfo(it)
            })
        }
        else {

            //set listeners
            val fab: View = requireActivity().findViewById(R.id.fab_star)
            fab.setOnClickListener { view ->

                //insert favorite in user
                FirebaseFirestore.getInstance().collection("users")
                    .document(FirebaseAuth.getInstance().currentUser!!.uid)
                    .update("favorites", FieldValue.arrayUnion(viewModel.itemInfo.value?.id))

                //insert favorite in item (for other queries)
                FirebaseFirestore.getInstance().collection("items").document(viewModel.itemInfo.value!!.id)
                    .update("users_favorites", FieldValue.arrayUnion(FirebaseAuth.getInstance().currentUser!!.uid))

                Snackbar.make(view, "Item Added to Favourites", Snackbar.LENGTH_LONG)
                    .setAction("Action", null)
                    .show()
            }

            buyButton.setOnClickListener { v ->
                // TEMPORARY
                //TODO: handle Sold status: if sold other cannot like it anymore
                val soldItem = FireItem(viewModel.itemInfo.value!!.picture_uri, viewModel.itemInfo.value!!.title,
                    viewModel.itemInfo.value!!.location, viewModel.itemInfo.value!!.price, viewModel.itemInfo.value!!.category,
                    viewModel.itemInfo.value!!.subCategory, viewModel.itemInfo.value!!.expDate, viewModel.itemInfo.value!!.condition,
                    viewModel.itemInfo.value!!.description, viewModel.itemInfo.value!!.id, viewModel.itemInfo.value!!.owner, "Sold")
                viewModel.setItemInfo(soldItem)
                item_status_buy.text = "Sold"
                FirebaseFirestore.getInstance().collection("items").document(viewModel.itemInfo.value!!.id).set(soldItem)
            }
            //Other person items
            viewModel.itemInfo.observe(requireActivity(), Observer {
                item_title.text = it.title
                item_price.text = it.price
                item_location.text = it.location
                item_category_value.text = it.category
                item_subcategory_value.text = it.subCategory
                item_expire_date_value.text = it.expDate
                item_location_value.text = it.location
                item_condition_value.text = it.condition
                item_description_value.text = it.description
                item_status_buy.text = it.status

                queryOwnerName(it.owner)

                Glide.with(requireContext())
                    .load(it.picture_uri)
                    .centerCrop()
                    .into(item_picture)

//                Helpers.updatePicture(this.requireContext(),
//                    Uri.parse(it.picture_uri.toString()),
//                    item_picture)

                viewModel.setTempItemInfo(it)
            })
        }

        this.requireActivity().getPreferences(Context.MODE_PRIVATE).edit().remove("item_picture_editing").apply()
    }

    private fun queryOwnerName(ownerId: String) {

        FirebaseFirestore.getInstance().collection("users").whereEqualTo("id", ownerId)
            .get()
            .addOnSuccessListener { documents ->  seller_usr.text =  documents.first()["username"] as String? }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.itemInfo.removeObservers(requireActivity())
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.item_details_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.editItemIcon -> {
                this.activity?.findNavController(R.id.nav_host_fragment)?.navigate(R.id.ItemEditFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}
