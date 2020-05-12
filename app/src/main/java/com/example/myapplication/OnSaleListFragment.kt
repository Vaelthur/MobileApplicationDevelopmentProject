package com.example.myapplication

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.itemFragments.ItemDetailsViewModel
import com.example.myapplication.main.ItemInfoAdapter
import com.example.myapplication.main.ItemListViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton


class OnSaleListFragment : Fragment() {

    private lateinit var itemListViewModel: ItemListViewModel

    private lateinit var itemDetailsViewModel: ItemDetailsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        itemListViewModel =
            ViewModelProviders.of(requireActivity())
                .get(ItemListViewModel(requireActivity().application)::class.java)

        itemDetailsViewModel =
            ViewModelProviders.of(requireActivity()).get(ItemDetailsViewModel::class.java)

        val root = inflater.inflate(R.layout.fragment_itemlist, container, false)

        itemListViewModel.itemListLiveData.observe(requireActivity(), Observer {itemList ->
            val recyclerView : RecyclerView? = root.findViewById(R.id.recyclerItemList)
            recyclerView?.layoutManager = LinearLayoutManager(context)
            recyclerView?.adapter = ItemInfoAdapter(itemList)
            //la riga sopra:
            // 1. da sostituire con lettura da firestore
            // 2. controllo se l'item appartiene all'utente loggato (se c'è, in tal caso inflatare card_view_star

            if(itemList.isEmpty()) {
                root.findViewById<TextView>(R.id.empty_list_msg).visibility = View.VISIBLE
            } else {
                root.findViewById<TextView>(R.id.empty_list_msg).visibility = View.GONE
            }
        })

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_on_sale_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.search_menu, menu)
        // inflater.inflate(R.menu.logout_menu, menu)
    }
}
