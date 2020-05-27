package com.example.myapplication.itemLists

import android.app.SearchManager
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.data.FireItem
import com.example.myapplication.data.ItemCategories
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.fragment_on_sale_list.*


class OnSaleListFragment : Fragment(),
    FilterItemFragment.FilterItemListener {

    private lateinit var itemListViewModel: ItemListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_on_sale_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        // set on click listener for login button - visible when we are logged out
        requireActivity().findViewById<Button>(R.id.btn_list_log).setOnClickListener { v ->
            requireActivity().findNavController(R.id.nav_host_fragment).navigate(
                R.id.signInFragment
            )
        }

        // check for user and if logged button no more needed => make it disappear
        if(FirebaseAuth.getInstance().currentUser != null ) {
            requireActivity().findViewById<Button>(R.id.btn_list_log).visibility = View.GONE

            itemListViewModel =
                ViewModelProviders.of(requireActivity()).get(ItemListViewModel::class.java)

            itemListViewModel.listenToItems()

            itemListViewModel.liveItems.observe(requireActivity(), Observer {
                val recyclerView: RecyclerView? = view.findViewById(R.id.recyclerItemList)
                recyclerView?.layoutManager = LinearLayoutManager(context)
                recyclerView?.adapter =
                    ItemInfoAdapter(it)
                // performs this check only when onsale is visible, otherwise crashes
                if(view.isShown)
                    checkEmptyList(it as MutableList<FireItem>)
            })
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.search_menu, menu)

        itemListViewModel = ViewModelProviders.of(requireActivity()).get(ItemListViewModel::class.java)

        val refreshOption = menu.findItem(R.id.refresh)
        refreshOption.setOnMenuItemClickListener {
            itemListViewModel.refresh()
            true
        }

        itemListViewModel.needRefresh.observe(requireActivity(), Observer {
             if(it){
                refreshOption.isVisible = true
                refreshOption.isEnabled = true
            }
            else{
                refreshOption.isVisible = false
                refreshOption.isEnabled = false
            }
        })
        searchHandler(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.filter -> {
                val filter =
                    FilterItemFragment(this)
                filter.show(this.parentFragmentManager, "filterItems")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun searchHandler(searchMenu: Menu) {
        //set searchView
        val manager = requireActivity().getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchItem = searchMenu.findItem(R.id.search)
        val searchView = searchItem.actionView as SearchView
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

        val queryTitle = FirebaseFirestore.getInstance()
            .collection("items")
            .whereGreaterThanOrEqualTo("title", query)
            .whereLessThanOrEqualTo("title", query + '\uf8ff')

        queryTitle.get()
            .addOnSuccessListener { result ->
                fillAndShowItemList(result)
            }
    }

    private fun checkEmptyList(itemList: MutableList<FireItem>) {
        if(itemList.isEmpty()) {
            empty_list_msg.visibility = View.VISIBLE
        } else {
            empty_list_msg.visibility = View.INVISIBLE
        }
    }

    override fun onDialogPositiveClick(dialog: DialogFragment, selectedCategories: ArrayList<Int>) {
        val categoryList = ArrayList<String>()
        val query = FirebaseFirestore.getInstance().collection("items")
        for(categoryIndex in selectedCategories) {
            val category = ItemCategories().getValueFromNum(categoryIndex)
            categoryList.add(category)
        }
        if(categoryList.isEmpty()) {
            query.get().addOnSuccessListener {result ->
                fillAndShowItemList(result)
            }
        } else {
            query.whereIn("category", categoryList)
                .get().addOnSuccessListener { result ->
                    fillAndShowItemList(result)
                }
        }
    }

    override fun onDialogNegativeClick(dialog: DialogFragment) {
        //show all items in case of cancel
        val query = FirebaseFirestore.getInstance().collection("items")
        query.get().addOnSuccessListener {result ->
            fillAndShowItemList(result)
        }
    }

    private fun fillAndShowItemList(result: QuerySnapshot) {
        val itemList = mutableListOf<FireItem>()
        FirebaseAuth.getInstance().currentUser?.let {
            for(document in result) {
                if (document["owner"] != it.uid) {
                    itemList.add(FireItem.fromMapToObj(document.data))
                }
            }
            checkEmptyList(itemList)
            recyclerItemList.adapter =
                ItemInfoAdapter(itemList)
        }


    }
}
