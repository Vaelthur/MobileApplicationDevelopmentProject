package com.example.myapplication.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders.of
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.Helpers
import com.example.myapplication.R
import com.example.myapplication.data.Item
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.Serializable

class ItemListFragment : Fragment() {

    private lateinit var itemListViewModel: ItemListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @Suppress("DEPRECATION")
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        itemListViewModel =
            of(requireActivity()).get(ItemListViewModel(requireActivity().application)::class.java)

        val root = inflater.inflate(R.layout.fragment_itemlist, container, false)

        itemListViewModel.itemListLiveData.observe(requireActivity(), Observer {itemList ->
            val recyclerView : RecyclerView? = root.findViewById(R.id.recyclerItemList)
            recyclerView?.layoutManager = LinearLayoutManager(context)
            recyclerView?.adapter =
                ItemInfoAdapter(itemList)
            if(itemList.isEmpty()) {
                root.findViewById<TextView>(R.id.empty_list_msg).visibility = View.VISIBLE
                root.findViewById<TextView>(R.id.new_item_button).visibility = View.VISIBLE
                root.findViewById<FloatingActionButton>(R.id.fabAddItem).visibility = View.GONE
            } else {
                root.findViewById<TextView>(R.id.empty_list_msg).visibility = View.GONE
                root.findViewById<TextView>(R.id.new_item_button).visibility = View.GONE
                root.findViewById<FloatingActionButton>(R.id.fabAddItem).visibility = View.VISIBLE
            }

        })

        val fab: FloatingActionButton = root.findViewById(R.id.fabAddItem)
        fab.setOnClickListener { view ->
            defaultItemEdit()
        }

        val newItem: Button = root.findViewById(R.id.new_item_button)
        newItem.setOnClickListener { view ->
            defaultItemEdit()
        }

        return root
    }

    private fun defaultItemEdit() {
        val itemBundle = Helpers.getDefaultItemBundle()
        requireActivity().findNavController(R.id.nav_host_fragment).navigate(R.id.itemDetailsFragment, itemBundle)
        requireActivity().findNavController(R.id.nav_host_fragment).navigate(R.id.ItemEditFragment, itemBundle)
    }
}
