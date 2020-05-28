package com.example.myapplication.reviews

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.data.Rating
import com.example.myapplication.data.Review
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_show_profile.*


class ReviewListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.reviews_recycler_view, container, false)
    }

    @Suppress("DEPRECATION")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Set spinner
        val reviewProgressBar = view.findViewById<ProgressBar>(R.id.progressBarReviews)
        reviewProgressBar.visibility = View.VISIBLE

        //Get userID for query
        var userId = arguments?.get("userID")
        if(userId == null){
            this.activity?.findNavController(R.id.nav_host_fragment)?.popBackStack()
            return
        }
        else{
            userId = userId  as String
        }

        //Query for user rating
        FirebaseFirestore.getInstance().collection("ratings").document(userId)
            .get()
            .addOnSuccessListener { doc ->

                val reviews = mutableListOf<Review>()

                if(doc != null && doc.data != null) {

                    val reviewsArray = doc["reviews"] as ArrayList<Map<String, Any>>
                    //Parse results
                    for (reviewToParse in reviewsArray){
                        val review = Review.fromMapToObj(reviewToParse)
                        reviews.add(review)
                    }
                }
                else {
                    val noReviews = view.findViewById<TextView>(R.id.empty_reviews_msg)
                    noReviews.visibility = View.VISIBLE
                }

                reviewProgressBar.visibility = View.GONE
                //Set results in adapter
                val recyclerView: RecyclerView? = view.findViewById(R.id.review_recycler_view)
                recyclerView?.layoutManager = LinearLayoutManager(context)

                recyclerView?.adapter =
                    ReviewAdapter(reviews)
            }
    }
}