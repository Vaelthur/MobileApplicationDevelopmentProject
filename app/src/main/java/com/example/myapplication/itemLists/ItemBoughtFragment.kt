package com.example.myapplication.itemLists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.data.FireItem
import com.example.myapplication.itemFragments.ItemDetailsViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.item_bought_list.*

class ItemBoughtFragment : Fragment() {

    private lateinit var itemDetailsViewModel: ItemDetailsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.item_bought_list, container, false)
    }

    @Suppress("DEPRECATION")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        itemDetailsViewModel =
            ViewModelProviders.of(requireActivity()).get(ItemDetailsViewModel::class.java)

        itemDetailsViewModel.tempItemInfo.value = null

        itemDetailsViewModel.boughtItems(FirebaseAuth.getInstance().currentUser!!.uid)

        itemDetailsViewModel.boughtLiveData.observe(requireActivity(), Observer {
            val recyclerView: RecyclerView? = view.findViewById(R.id.recyclerBoughtItemList)
            recyclerView?.layoutManager = LinearLayoutManager(context)

            recyclerView?.adapter =
                ItemInfoAdapter(it)

            if(view.isShown)
                checkEmptyList(it as MutableList<FireItem>)
        })
    }

    private fun checkEmptyList(itemList: MutableList<FireItem>) {
        if(itemList.isEmpty()) {
            empty_bought_list_msg.visibility = View.VISIBLE
        } else {
            empty_bought_list_msg.visibility = View.INVISIBLE
        }
    }
}