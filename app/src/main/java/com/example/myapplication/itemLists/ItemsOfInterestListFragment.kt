package com.example.myapplication.itemLists

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.myapplication.R
import com.example.myapplication.data.FireItem
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.items_of_interest_list_fragment.*

class ItemsOfInterestListFragment : Fragment() {

    private lateinit var itemFavViewModel: ItemsOfInterestListViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.items_of_interest_list_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        FirebaseAuth.getInstance().currentUser?.let {

            itemFavViewModel = ViewModelProviders.of(requireActivity()).get(ItemsOfInterestListViewModel::class.java)
            itemFavViewModel.listenToFavItems()

            itemFavViewModel.liveFavItems.observe(requireActivity(), Observer {
                val recyclerView: RecyclerView? = view.findViewById(R.id.recyclerFavItemList)
                recyclerView?.layoutManager = LinearLayoutManager(context)
                recyclerView?.adapter = ItemInfoAdapter(it, true)

                if(view.isShown)
                    checkEmptyList(it as MutableList<FireItem>)
            })
        }

    }

    private fun checkEmptyList(itemList: MutableList<FireItem>) {
        if(itemList.isEmpty()) {
            empty_fav_list_msg.visibility = View.VISIBLE
        } else {
            empty_fav_list_msg.visibility = View.INVISIBLE
        }
    }

}
