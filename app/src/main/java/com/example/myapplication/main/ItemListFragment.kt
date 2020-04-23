package com.example.myapplication.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders.of
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

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
            of(requireActivity()).get(ItemListViewModel::class.java)

        val root = inflater.inflate(R.layout.fragment_itemlist, container, false)

        itemListViewModel.itemListLiveData.observe(requireActivity(), Observer {
            val recyclerView : RecyclerView? = root.findViewById(R.id.recyclerItemList)
            recyclerView?.layoutManager = LinearLayoutManager(context)
            recyclerView?.adapter =
                ItemInfoAdapter(itemListViewModel.itemListLiveData)
        })

        val fab: FloatingActionButton = root.findViewById(R.id.fabAddItem)
        fab.setOnClickListener { view ->
            itemListViewModel =
                of(requireActivity()).get(ItemListViewModel::class.java)
            itemListViewModel.addItem()
            //requireActivity().findNavController(R.id.nav_host_fragment).navigate(R.id.ItemEditFragment)
        }
        return root
    }


}
