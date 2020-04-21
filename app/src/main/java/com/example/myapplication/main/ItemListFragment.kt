package com.example.myapplication.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders.of
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar

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

        val item1 = ItemInfoData(
            null,
            "Titolo",
            "torino",
            "400$"
        )
        val item2 = ItemInfoData(
            null,
            "TITLE",
            "Roma",
            "400$"
        )
        val item3 = ItemInfoData(
            null,
            "TITLE",
            "Roma",
            "400$"
        )
        val item4 = ItemInfoData(
            null,
            "TITLE",
            "Roma",
            "400$"
        )
        val item5 = ItemInfoData(
            null,
            "TITLE",
            "Roma",
            "400$"
        )
        val item6 = ItemInfoData(
            null,
            "TITLE",
            "Roma",
            "400$"
        )
        val item7 = ItemInfoData(
            null,
            "TITLE",
            "Roma",
            "400$"
        )
        val item8 = ItemInfoData(
            null,
            "TITLE",
            "Roma",
            "400$"
        )
        val items : List<ItemInfoData> = listOf(item1, item2, item3, item4, item5, item6, item7, item8)



        recyclerView?.layoutManager = LinearLayoutManager(context)
        recyclerView?.adapter =
            ItemInfoAdapter(items)

        val fab: FloatingActionButton = root.findViewById(R.id.fabAddItem)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Create new item", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        return root
    }


}
