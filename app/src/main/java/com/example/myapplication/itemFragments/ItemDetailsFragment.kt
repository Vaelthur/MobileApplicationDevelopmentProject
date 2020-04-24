package com.example.myapplication.itemFragments

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders.of
import androidx.navigation.findNavController
import com.example.myapplication.Helpers
import com.example.myapplication.R
import com.example.myapplication.data.Item
import kotlinx.android.synthetic.main.item_details_fragment.*
import com.example.myapplication.main.ItemDetailsInfoData


class ItemDetailsFragment : Fragment() {

    private lateinit var viewModel: ItemDetailsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.item_details_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // get viewmodel
        viewModel = of(requireActivity()).get(ItemDetailsViewModel::class.java)

        arguments?. let {
            val incomingItem : Item = it.getSerializable("item") as Item
            viewModel.setItemInfo(incomingItem)
            viewModel.setTempItemInfo(incomingItem)
        }

        val itemInfo = viewModel.itemInfo
        if(itemInfo.value == null){
            return
        }

        itemInfo.observe(requireActivity(), Observer {
            item_title.text = it.title
            item_price.text = it.price
            item_location.text = it.location
            item_category_value.text = it.category
            item_subcategory_value.text = it.subCategory
            item_expire_date_value.text = it.expDate
            item_location_value.text = it.location
            item_condition_value.text =it.condition
            item_description_value.text = it.description

            Helpers.updateItemPicture(this.requireContext(),
                Uri.parse(it.pictureURIString.toString()),
                item_picture)

            val readFromSharePref = (this.activity as AppCompatActivity).getPreferences(Context.MODE_PRIVATE)
            with(readFromSharePref.edit()) {
                putString("item_picture_editing", it.pictureURIString)
                commit()
            }

            viewModel.setTempItemInfo(it)
        })
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
