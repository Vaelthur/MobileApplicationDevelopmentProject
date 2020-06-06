package com.example.myapplication

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import com.google.firebase.firestore.GeoPoint


class RouteMapFragment : Fragment(), OnMapReadyCallback {
    var src = GeoPoint(51.7, 61.9)
    var dst = GeoPoint(1.0, 8.9)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val c1 : DoubleArray = arguments?.get("src") as DoubleArray
        src = GeoPoint(c1[0], c1[1])
        val c2 : DoubleArray = arguments?.get("dst") as DoubleArray
        dst = GeoPoint(c2[0], c2[1])
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_route_map, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapView = view.findViewById<MapView>(R.id.mapRoute)
        mapView.onCreate(savedInstanceState)
        mapView.onResume()
        mapView.getMapAsync(this)
    }


    override fun onMapReady(map: GoogleMap?) {


        map?.addPolyline(
            PolylineOptions()
                .add(
                    LatLng(src.latitude, src.longitude),
                    LatLng(dst.latitude, dst.longitude)
                )
                .width(5f)
                .color(Color.RED) )
    }
}


