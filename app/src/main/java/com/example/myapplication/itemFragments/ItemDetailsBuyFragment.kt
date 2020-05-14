package com.example.myapplication.itemFragments

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.example.myapplication.Helpers
import com.example.myapplication.R
import com.example.myapplication.data.FireItem
import com.example.myapplication.data.Item
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.item_details_fragment.*

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

        val fab: View = requireActivity().findViewById(R.id.fab_star)
        fab.setOnClickListener { view ->
            // TODO: Insert in list of favourites in db
            Snackbar.make(view, "Item Added to Favourites", Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .show()
        }

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

            Helpers.updatePicture(this.requireContext(),
                Uri.parse(it.picture_uri.toString()),
                item_picture)

            viewModel.setTempItemInfo(it)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.itemInfo.removeObservers(requireActivity())
    }
}