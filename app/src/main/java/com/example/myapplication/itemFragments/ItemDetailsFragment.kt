package com.example.myapplication.itemFragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.*
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders.of
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.CustomMapView
import com.example.myapplication.main.Helpers
import com.example.myapplication.R
import com.example.myapplication.data.*
import com.example.myapplication.notifications.NOTIFICATION_TYPE
import com.example.myapplication.notifications.NotificationStore
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.item_details_buy_fragment.*
import kotlinx.android.synthetic.main.item_details_fragment.*
import kotlinx.android.synthetic.main.item_details_fragment.item_category_value
import kotlinx.android.synthetic.main.item_details_fragment.item_condition_value
import kotlinx.android.synthetic.main.item_details_fragment.item_description_value
import kotlinx.android.synthetic.main.item_details_fragment.item_expire_date_value
import kotlinx.android.synthetic.main.item_details_fragment.item_location
import kotlinx.android.synthetic.main.item_details_fragment.item_location_value
import kotlinx.android.synthetic.main.item_details_fragment.item_picture
import kotlinx.android.synthetic.main.item_details_fragment.item_price
import kotlinx.android.synthetic.main.item_details_fragment.item_subcategory_value
import kotlinx.android.synthetic.main.item_details_fragment.item_title
import org.w3c.dom.Text
import java.io.Serializable


class ItemDetailsFragment : Fragment(), RateSellerDialogFragment.RateSellerListener, OnMapReadyCallback {

    private lateinit var viewModel: ItemDetailsViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var myItems : Boolean? = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myItems = arguments?.getBoolean("myitems")
        if(myItems!!) {
            setHasOptionsMenu(true)
        } else {
            setHasOptionsMenu(false)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return if(myItems!!)
                    inflater.inflate(R.layout.item_details_fragment, container, false)
                else
                    inflater.inflate(R.layout.item_details_buy_fragment, container, false)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        viewModel = of(requireActivity()).get(ItemDetailsViewModel::class.java)

        arguments?. let {
            val incomingItem : FireItem = it.getSerializable("item") as FireItem
            viewModel.setItemInfo(incomingItem)
            viewModel.setTempItemInfo(incomingItem)
        }

        val itemInfo = viewModel.itemInfo
        if(itemInfo.value == Helpers.getDefaultItem()){
            this.activity?.findNavController(R.id.nav_host_fragment)?.popBackStack()
            return
        }

        val status = viewModel.itemInfo.value?.status
        val isItemSoldOrBlocked : Boolean = (status == ItemStatusCreator.getStatus(ITEMSTATUS.SOLD)) ||
                (status == ItemStatusCreator.getStatus(ITEMSTATUS.BLOCKED))

        if(myItems!!) {
            //Here personal item
            //get users interested
            viewModel.interestedUsers(itemInfo.value!!.id)

            viewModel.interestedLiveData.observe(requireActivity(), Observer {
                val recyclerView: RecyclerView? = view.findViewById(R.id.fav_users)
                recyclerView?.layoutManager = LinearLayoutManager(context)
                recyclerView?.adapter =
                    UsersListAdapter(it)
            })

            if (isItemSoldOrBlocked) {
                blockButton.isClickable = false
                blockButton.backgroundTintList = ColorStateList.valueOf(Color.GRAY)
            } else {
                //set listener block button
                blockButton.setOnClickListener { v ->

                    val blockItem = FireItem(
                        viewModel.itemInfo.value?.picture_uri,
                        viewModel.itemInfo.value!!.title,
                        viewModel.itemInfo.value?.location,
                        viewModel.itemInfo.value!!.price,
                        viewModel.itemInfo.value!!.category,
                        viewModel.itemInfo.value!!.subCategory,
                        viewModel.itemInfo.value!!.expDate,
                        viewModel.itemInfo.value!!.condition,
                        viewModel.itemInfo.value!!.description,
                        viewModel.itemInfo.value!!.id,
                        viewModel.itemInfo.value!!.owner,
                        ItemStatusCreator.getStatus(ITEMSTATUS.BLOCKED)
                    )

                    viewModel.setItemInfo(blockItem)

                    val updatedStatus = HashMap<String, Any>()
                    updatedStatus["status"] = ItemStatusCreator.getStatus(ITEMSTATUS.BLOCKED)
                    item_status.text = ItemStatusCreator.getStatus(ITEMSTATUS.BLOCKED)
                    FirebaseFirestore.getInstance().collection("items")
                        .document(viewModel.itemInfo.value!!.id).update(updatedStatus)

                    //Notify interested users that item has been blocked
                    val notificationStore: NotificationStore =
                        NotificationStore()
                    notificationStore.postNotificationMultipleUsers(
                        viewModel.itemInfo.value?.title!!,
                        viewModel.itemInfo.value?.id!!,
                        NOTIFICATION_TYPE.NO_LONGER_AVAILABLE
                    )

                    blockButton.isClickable = false
                    blockButton.backgroundTintList = ColorStateList.valueOf(Color.GRAY)

                    Snackbar.make(view, "Item is no longer on sale", Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .show()
                }
            }


            viewModel.itemInfo.observe(requireActivity(), Observer {
                item_title.text = it.title
                item_price.text = it.price
                item_location.text = it.location
                item_category_value.text = it.category
                item_subcategory_value.text = it.subCategory
                item_expire_date_value.text = it.expDate
                item_location_value.text = it.location
                item_condition_value.text = it.condition
                item_description_value.text = it.description
                item_status.text = it.status

                Glide.with(requireContext())
                    .load(it.picture_uri)
                    .error(R.drawable.default__item_image)
                    .centerCrop()
                    .into(item_picture)

                viewModel.setTempItemInfo(it)
            })
        }
        else {
            //Other person items

            //set listeners
            //Set link to seller profile
            val sellerUsernameView = view.findViewById<TextView>(R.id.seller_usr)
            sellerUsernameView.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {

                    FirebaseFirestore.getInstance().collection("users").whereEqualTo("id", viewModel.itemInfo.value?.owner)
                        .get()
                        .addOnCompleteListener {
                            if(it.result != null){
                                val accountInfo = AccountInfoFactory.fromMapToObj(it.result?.documents?.first()?.data)
                                val accountBundle = Bundle(2)
                                accountBundle.putSerializable("account_info", accountInfo as Serializable?)
                                accountBundle.putBoolean("myprofile", false)
                                view.findNavController().navigate(R.id.showProfileFragment, accountBundle)
                            }
                        }
                }
            })

            val fab: View = requireActivity().findViewById(R.id.fab_star)
            fab.setOnClickListener { view ->

                //insert favorite in user
                FirebaseFirestore.getInstance().collection("users")
                    .document(FirebaseAuth.getInstance().currentUser!!.uid)
                    .update("favorites", FieldValue.arrayUnion(viewModel.itemInfo.value?.id))

                //insert favorite in item (for other queries)
                FirebaseFirestore.getInstance().collection("items").document(viewModel.itemInfo.value!!.id)
                    .update("users_favorites", FieldValue.arrayUnion(FirebaseAuth.getInstance().currentUser!!.uid))

                //Notify owner
                val notificationStore : NotificationStore =
                    NotificationStore()
                notificationStore.apply {
                    postNotification(viewModel.itemInfo.value?.title!!, viewModel.itemInfo.value?.owner!!, NOTIFICATION_TYPE.INTERESTED)
                }

                Snackbar.make(view, "Item Added to Favourites", Snackbar.LENGTH_LONG)
                    .setAction("Action", null)
                    .show()
            }

            if(isItemSoldOrBlocked){
                buyButton.isClickable = false
                buyButton.backgroundTintList = ColorStateList.valueOf(Color.GRAY)
            }
            else {
                buyButton.setOnClickListener { v ->

                    //Rate seller
                    val ratingFragment = RateSellerDialogFragment(this, viewModel.itemInfo.value?.owner!!)
                    ratingFragment.show(this.parentFragmentManager, "sellerRating")

                    val soldItem = FireItem(viewModel.itemInfo.value?.picture_uri, viewModel.itemInfo.value!!.title,
                        viewModel.itemInfo.value?.location, viewModel.itemInfo.value!!.price, viewModel.itemInfo.value!!.category,
                        viewModel.itemInfo.value!!.subCategory, viewModel.itemInfo.value!!.expDate, viewModel.itemInfo.value!!.condition,
                        viewModel.itemInfo.value!!.description, viewModel.itemInfo.value!!.id, viewModel.itemInfo.value!!.owner,
                        ItemStatusCreator.getStatus(ITEMSTATUS.SOLD))

                    viewModel.setItemInfo(soldItem)
                    val updatedStatus = HashMap<String, Any>()
                    updatedStatus["status"] = ItemStatusCreator.getStatus(ITEMSTATUS.SOLD)
                    FirebaseFirestore.getInstance().collection("items").document(viewModel.itemInfo.value!!.id).update(updatedStatus)
                    buyButton.isClickable = false
                    buyButton.backgroundTintList = ColorStateList.valueOf(Color.GRAY)

                    // add bought item to db
                    FirebaseFirestore.getInstance().collection("users")
                        .document(FirebaseAuth.getInstance().currentUser!!.uid)
                        .update("bought", FieldValue.arrayUnion(viewModel.itemInfo.value?.id))

                    //Notify owner that item has been sold
                    val notificationStore : NotificationStore =
                        NotificationStore()
                    notificationStore.apply {
                        postNotification(viewModel.itemInfo.value?.title!!, viewModel.itemInfo.value?.owner!!, NOTIFICATION_TYPE.SOLD)
                    }

                    //Notify interested users that item has been sold
                    notificationStore.postNotificationMultipleUsers(viewModel.itemInfo.value?.title!!,
                            viewModel.itemInfo.value?.id!!,
                            NOTIFICATION_TYPE.NO_LONGER_AVAILABLE)

                }
            }

            //Viewmodel observer
            viewModel.itemInfo.observe(requireActivity(), Observer {
                item_title.text = it.title
                item_price.text = it.price
                item_location.text = it.location
                item_category_value.text = it.category
                item_subcategory_value.text = it.subCategory
                item_expire_date_value.text = it.expDate
                item_location_value.text = it.location
                item_condition_value.text = it.condition
                item_description_value.text = it.description
                item_status_buy.text = it.status

                queryOwnerName(it.owner)

                Glide.with(requireContext())
                    .load(it.picture_uri)
                    .centerCrop()
                    .into(item_picture)

                viewModel.setTempItemInfo(it)
            })
        }

        //update map
        val mapView = view.findViewById<CustomMapView>(R.id.itemLocation)
        mapView.onCreate(savedInstanceState)
        mapView.onResume()
        mapView.getMapAsync(this)

        this.requireActivity().getPreferences(Context.MODE_PRIVATE).edit().remove("item_picture_editing").apply()
    }

    private fun queryOwnerName(ownerId: String) {

        FirebaseFirestore.getInstance().collection("users").whereEqualTo("id", ownerId)
            .get()
            .addOnSuccessListener { documents ->
                //Make text underlined because it's a link
                val contentText = documents.first()["username"] as String?
                val content = SpannableString(contentText)
                content.setSpan(UnderlineSpan(), 0, content.length, 0)
                seller_usr.text = content
                seller_usr.setTextColor(Color.BLUE)
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.itemInfo.removeObservers(requireActivity())
        viewModel.interestedLiveData.removeObservers(requireActivity())
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.item_details_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.editItemIcon -> {
                this.activity?.findNavController(R.id.nav_host_fragment)?.navigate(R.id.ItemEditFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /// region RateSellerDialog

    override fun onDialogPositiveClick(dialog: DialogFragment, rating: Float?, ownerId: String, comment : String?) {

        Snackbar.make(requireView(), "Item bought! Thanks for rating and congratulations:)", Snackbar.LENGTH_LONG)
            .setAction("Action", null)
            .show()

        //Update rating of seller
        rating?.let {
            FirebaseFirestore.getInstance().collection("ratings").document(ownerId)
                .get()
                .addOnSuccessListener { doc ->
                    var updateReview = false

                    val newReview : Review? = if(comment != "") {
                                                    Review(FirebaseAuth.getInstance().currentUser?.uid!!,
                                                        FirebaseAuth.getInstance().currentUser?.displayName!!,
                                                        rating.toDouble(),
                                                        comment)
                                                }
                                            else
                                                null

                    val newRating = if(doc.data == null){
                                        updateReview = true
                                        Rating(rating / 1.0, 1)
                                    }
                                    else {
                                        val currentRating = Rating.fromMapToObj(doc.data!!["rating"] as Map<String, Any>?)
                                        val newTotal = currentRating.ratingsReceived + 1
                                        val newMean = (currentRating.meanRating + rating) / newTotal
                                        Rating(newMean, newTotal)
                                    }
                    //Update db with new rating and new review if comment was left
                    if(updateReview)
                        FirebaseFirestore.getInstance().collection("ratings").document(ownerId)
                            .set(hashMapOf(Pair<String, Rating>("rating", newRating)))
                    else
                        FirebaseFirestore.getInstance().collection("ratings").document(ownerId)
                            .update("rating", newRating)

                    newReview?.apply {
                            FirebaseFirestore.getInstance().collection("ratings").document(ownerId)
                                .update("reviews", FieldValue.arrayUnion(this))
                    }
                }
        }
    }

    override fun onDialogNegativeClick(dialog: DialogFragment) {
        Snackbar.make(requireView(), "Item bought, congratulations:)", Snackbar.LENGTH_LONG)
            .setAction("Action", null)
            .show()
    }

    override fun onMapReady(map: GoogleMap?) {
        map!!.addMarker(MarkerOptions().position(LatLng(34.6,12.1)).title("prova"))
        requireActivity()
    }

    /// endregion

}
