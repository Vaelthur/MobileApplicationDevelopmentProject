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

        val root = inflater.inflate(R.layout.fragment_itemlist, container, false)

        class parser : SnapshotParser<FireItem> {
            override fun parseSnapshot(snapshot: DocumentSnapshot): FireItem {
                return FireItem.fromMapToObj(snapshot.data)
            }
        }

//        itemListViewModel.itemListLiveData?.observe(requireActivity(), Observer {itemList ->
//            val recyclerView : RecyclerView? = root.findViewById(R.id.recyclerItemList)
//            recyclerView?.layoutManager = LinearLayoutManager(context)
//            recyclerView?.adapter = ItemInfoAdapter(itemList)
//            //la riga sopra:
//            // 1. da sostituire con lettura da firestore
//            // 2. controllo se l'item appartiene all'utente loggato (se c'è, in tal caso inflatare card_view_star
//
//            if(itemList.isEmpty()) {
//                root.findViewById<TextView>(R.id.empty_list_msg).visibility = View.VISIBLE
//            } else {
//                root.findViewById<TextView>(R.id.empty_list_msg).visibility = View.GONE
//            }
//        })

        FirebaseFirestore.getInstance().collection("users").get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    if (document["id"] != FirebaseAuth.getInstance().currentUser!!.uid) {
                        val query = document.reference.collection("items") as Query
                        val recyclerView: RecyclerView? = root.findViewById(R.id.recyclerItemList)
                        recyclerView?.layoutManager = LinearLayoutManager(context)

                        val builder = FirestoreRecyclerOptions.Builder<FireItem>()
                            .setQuery(query, parser())
                            .setLifecycleOwner(requireActivity())
                            .build()

                        recyclerView?.adapter =
                            FirestoreItemAdapter(builder) //ogni volta le recycler view viene rifatta
                        //necessario quidni prendere tutti gli item e metterli dentro qualcosa e POI passarli alla recyclerView

                        checkEmptyQueryResult(query, root)
                    }
                }
            }

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
        val manager = requireActivity().getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchItem = menu.findItem(R.id.search)
        val searchView = searchItem?.actionView as SearchView //maybe from androidx

        searchView.setSearchableInfo(manager.getSearchableInfo(requireActivity().componentName))

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                class parser : SnapshotParser<FireItem> {
                    override fun parseSnapshot(snapshot: DocumentSnapshot): FireItem {
                        return FireItem.fromMapToObj(snapshot.data)
                    }
                }
                searchView.clearFocus()
                searchView.setQuery("",false)
                searchItem.collapseActionView()
                //search for query in the firestoredb
                //Toast.makeText(requireContext(), "Gnagna", Toast.LENGTH_LONG).show()
                val firebaseQuery = FirebaseFirestore.getInstance().collection("items").whereArrayContains("title", query!!)
                //la riga superiore non va
                val builder = FirestoreRecyclerOptions.Builder<FireItem>()
                    .setQuery(firebaseQuery, parser())
                    .setLifecycleOwner(requireActivity())
                    .build()
                recyclerItemList.adapter = FirestoreItemAdapter(builder)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
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
