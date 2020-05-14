package com.example.myapplication.itemFragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders.of
import androidx.navigation.findNavController
import com.example.myapplication.Helpers
import com.example.myapplication.R
import com.example.myapplication.data.FireItem
import kotlinx.android.synthetic.main.item_details_fragment.*


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

            Helpers.updatePicture(this.requireContext(),
                Uri.parse(it.picture_uri.toString()),
                item_picture)

            viewModel.setTempItemInfo(it)
        })

        this.requireActivity().getPreferences(Context.MODE_PRIVATE).edit().remove("item_picture_editing").apply()
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
