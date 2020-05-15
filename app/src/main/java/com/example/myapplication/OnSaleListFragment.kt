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

    private lateinit var itemListViewModel: ItemListViewModel

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

        class parser : SnapshotParser<FireItem> {
            override fun parseSnapshot(snapshot: DocumentSnapshot): FireItem {
                return FireItem.fromMapToObj(snapshot.data)
            }
        }
        // V2
        FirebaseFirestore.getInstance().collection("items").get()
            .addOnSuccessListener { result ->

                val recyclerView: RecyclerView? = root.findViewById(R.id.recyclerItemList)
                recyclerView?.layoutManager = LinearLayoutManager(context)
                val itemList = mutableListOf<FireItem>()

                for (document in result) {
                    if (document["owner"] != FirebaseAuth.getInstance().currentUser!!.uid) {
                        itemList.add(FireItem.fromMapToObj(document.data))
                    }
                }

                recyclerView?.adapter =
                    ItemInfoAdapter(itemList)
            }

        // Inflate the layout for this fragment
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.search_menu, menu)
        // inflater.inflate(R.menu.logout_menu, menu)
        val manager = requireActivity().getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchItem = menu.findItem(R.id.search)
        val searchView = searchItem?.actionView as SearchView

        searchView.setSearchableInfo(manager.getSearchableInfo(requireActivity().componentName))

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            inner class parser : SnapshotParser<FireItem> {
                override fun parseSnapshot(snapshot: DocumentSnapshot): FireItem {
                    return FireItem.fromMapToObj(snapshot.data)
                }
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                searchView.clearFocus()
                searchView.setQuery("",false)
                searchItem.collapseActionView()
                //search for query in the firestoredb
                //Toast.makeText(requireContext(), "Gnagna", Toast.LENGTH_LONG).show()
                val firebaseQuery = FirebaseFirestore.getInstance().collection("items").whereGreaterThan("title", query!!).whereLessThan("title", query+'\uf8ff')
                //la riga superiore non va
                val builder = FirestoreRecyclerOptions.Builder<FireItem>()
                    .setQuery(firebaseQuery, parser())
                    .setLifecycleOwner(requireActivity())
                    .build()
                recyclerItemList.adapter = FirestoreItemAdapter(builder)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val firebaseQuery = FirebaseFirestore.getInstance().collection("items").whereGreaterThan("title", newText!!).whereLessThan("title", newText!!+'\uf8ff')
                //la riga superiore non va
                val builder = FirestoreRecyclerOptions.Builder<FireItem>()
                    .setQuery(firebaseQuery, parser())
                    .setLifecycleOwner(requireActivity())
                    .build()
                recyclerItemList.adapter = FirestoreItemAdapter(builder)
                return false
            }
        })
    }

    private fun checkEmptyQueryResult(query: Query, root : View) {

        query.get()
            .addOnSuccessListener { listItemDocument ->
                if (listItemDocument.isEmpty) {
                    root.findViewById<TextView>(R.id.empty_list_msg).visibility = View.VISIBLE
                }
                else {
                    root.findViewById<TextView>(R.id.empty_list_msg).visibility = View.GONE
                }
            }
            .addOnFailureListener { Helpers.makeSnackbar(requireView(), "Could not retrieve item info") }
    }
}
