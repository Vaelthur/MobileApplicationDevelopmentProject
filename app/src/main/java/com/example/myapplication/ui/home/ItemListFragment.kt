package com.example.myapplication.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders.of
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.ItemInfoAdapter
import com.example.myapplication.ItemInfoData
import com.example.myapplication.R
import kotlinx.android.synthetic.main.fragment_itemlist.*

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
            of(this).get(ItemListViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_itemlist, container, false)
        val recyclerView : RecyclerView? = root.findViewById(R.id.recyclerItemList)

        val item1 = ItemInfoData(null,"Titolo", "torino", "400$")
        val item2 = ItemInfoData(null,"TITLE", "Roma", "400$")
        val items : List<ItemInfoData> = listOf(item1, item2)

        recyclerView?.layoutManager = LinearLayoutManager(context)
        recyclerView?.adapter = ItemInfoAdapter(items)

        return root
    }


}
