package com.example.myapplication

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.data.FireItem
import com.example.myapplication.itemFragments.ItemDetailsViewModel
import com.example.myapplication.main.FirestoreItemAdapter
import com.example.myapplication.main.ItemInfoAdapter
import com.example.myapplication.main.ItemListViewModel
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.firebase.ui.firestore.SnapshotParser
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.fragment_on_sale_list.*


class OnSaleListFragment : Fragment() {

    private lateinit var itemDetailsViewModel: ItemDetailsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        itemDetailsViewModel =
            ViewModelProviders.of(requireActivity()).get(ItemDetailsViewModel::class.java)

        itemDetailsViewModel.tempItemInfo.value = null

        val root = inflater.inflate(R.layout.fragment_on_sale_list, container, false)

        FirebaseAuth.getInstance().currentUser?.let {
            FirebaseFirestore.getInstance().collection("items").get()
                .addOnSuccessListener { result ->

                    val recyclerView: RecyclerView? = root.findViewById(R.id.recyclerItemList)
                    recyclerView?.layoutManager = LinearLayoutManager(context)

                    val itemList = mutableListOf<FireItem>()

                    for (document in result) {
                        if (document["owner"] != it.uid) {
                            itemList.add(FireItem.fromMapToObj(document.data))
                        }
                    }

                    checkEmptyList(itemList)

                    //Observe this list for live updates here?
                    recyclerView?.adapter =
                        ItemInfoAdapter(itemList)
                }
        }


        // Inflate the layout for this fragment
        return root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.search_menu, menu)

        //set searchView
        val manager = requireActivity().getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchItem = menu.findItem(R.id.search)
        val searchView = searchItem?.actionView as SearchView
        searchView.setSearchableInfo(manager.getSearchableInfo(requireActivity().componentName))

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                searchView.clearFocus()
                searchView.setQuery("",false)
                searchItem.collapseActionView()

                getSaleItems(query!!)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                getSaleItems(newText!!)
                return false
            }
        })
    }

    private fun getSaleItems(query: String) {

        FirebaseFirestore.getInstance()
            .collection("items")
            .whereGreaterThanOrEqualTo("title", query)
            .whereLessThanOrEqualTo("title", query + '\uf8ff')
            .get()
            .addOnSuccessListener { result ->
                val itemList = mutableListOf<FireItem>()
                for (document in result) {
                    if (document["owner"] != FirebaseAuth.getInstance().currentUser!!.uid) {
                        itemList.add(FireItem.fromMapToObj(document.data))
                    }
                }
                checkEmptyList(itemList)
                recyclerItemList.adapter = ItemInfoAdapter(itemList)
            }
    }

    private fun checkEmptyList(itemList: MutableList<FireItem>) {
        if(itemList.isEmpty()) {
            empty_list_msg.visibility = View.VISIBLE
        } else {
            empty_list_msg.visibility = View.INVISIBLE
        }
    }

}
