package com.example.myapplication.itemFragments

import android.net.Uri
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders.of
import com.example.myapplication.Helpers
import com.example.myapplication.R
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
        viewModel = of(requireActivity()).get(ItemDetailsViewModel::class.java)
        return inflater.inflate(R.layout.item_details_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val itemInfo = viewModel.itemInfo
        if(itemInfo?.value == null){
            readSharedPreferences()
        }
        itemInfo.observe(requireActivity(), Observer {
            item_title.text = it.title
            item_price.text = it.price
            item_location.text = it.location
            item_category_value.text = it.category
            item_expire_date_value.text = it.expDate
            item_location_value.text = it.location
            item_condition_value.text =it.condition
            item_description_value.text = it.description
            Helpers.updateItemPicture(this.requireContext(),
                Uri.parse(it.pictureURIString),
                item_picture)

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

    /// regionHelpers
    private fun readSharedPreferences() {

        val parentActivity = this.requireActivity() as AppCompatActivity
        val accountJson =
            Helpers.readJsonFromPreferences(
                parentActivity
            )

        accountJson?. let {
            viewModel.setItemInfo(accountJson)
            viewModel.setTempItemInfo(accountJson)
        }
    }

}
