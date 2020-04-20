package com.example.myapplication.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders.*
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.ItemInfoAdapter
import com.example.myapplication.ItemInfoData
import com.example.myapplication.R
import com.example.myapplication.ui.gallery.GalleryViewModel


class ItemListFragment : Fragment() {

    private lateinit var itemListViewModel: ItemListViewModel

    private val defaultItemPic = "android.resource://com.example.lab2/drawable/default_profile_picture"

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
            of(this).get(ItemListViewModel::class.java)
        val root = inflater.inflate(R.layout.content_main, container, false)
        val recyclerView : RecyclerView? = root.findViewById(R.id.nav_host_fragment)

        val item1 = ItemInfoData(defaultItemPic,"Titolo", "torino", "400$")
        val item2 = ItemInfoData(defaultItemPic,"TITLE", "Roma", "400$")
        val items : List<ItemInfoData> = listOf(item1, item2)

        recyclerView?.layoutManager = LinearLayoutManager(context)
        recyclerView?.adapter = ItemInfoAdapter(items)

        return root
    }


}
