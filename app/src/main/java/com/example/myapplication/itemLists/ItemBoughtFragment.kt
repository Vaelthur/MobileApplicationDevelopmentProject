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
import com.example.myapplication.itemFragments.ItemDetailsViewModel
import com.google.firebase.auth.FirebaseAuth

class ItemBoughtFragment : Fragment() {

    private lateinit var itemDetailsViewModel: ItemDetailsViewModel

    @Suppress("DEPRECATION")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        itemDetailsViewModel =
            ViewModelProviders.of(requireActivity()).get(ItemDetailsViewModel::class.java)

        itemDetailsViewModel.tempItemInfo.value = null


        val root = inflater.inflate(R.layout.item_bought_list, container, false)

        itemDetailsViewModel.boughtItems(FirebaseAuth.getInstance().currentUser!!.uid)

        itemDetailsViewModel.boughtLiveData.observe(requireActivity(), Observer {
            val recyclerView: RecyclerView? = root.findViewById(R.id.recyclerBoughtItemList)
            recyclerView?.layoutManager = LinearLayoutManager(context)

            recyclerView?.adapter =
                ItemInfoAdapter(it)
        })

        return root
    }
}