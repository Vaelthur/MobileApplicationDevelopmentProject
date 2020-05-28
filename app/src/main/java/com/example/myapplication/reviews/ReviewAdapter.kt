package com.example.myapplication.reviews

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.data.Review

class ReviewAdapter(private val reviews: List<Review>)
    : RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>(){

    override fun getItemCount(): Int = reviews.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val view  = LayoutInflater.from(parent.context)
            .inflate(R.layout.review_card_view, parent, false)

        return ReviewViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {

        holder.bind(reviews[position])
    }

    class ReviewViewHolder(private val view : View) : RecyclerView.ViewHolder(view) {
        fun bind(review : Review) {

            //Get views
            val doubleRating = review.stars
            val starsView = view.findViewById<RatingBar>(R.id.ratingBar_card_view_stars)
            val meanView = view.findViewById<TextView>(R.id.card_rating_textview)
            val reviewerView = view.findViewById<TextView>(R.id.reviewer_text_view)
            val comment = view.findViewById<TextView>(R.id.review_description)

            //Set values
            starsView.rating = doubleRating.toFloat()
            val stringRating = "  " + doubleRating.toString().substring(0, 3)
            meanView.text = stringRating
            reviewerView.text = review.reviewedByUsername
            comment.text = review.comment
        }

    }

}