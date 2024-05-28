package com.yondikavl.narasiqu.ui

import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.yondikavl.narasiqu.databinding.ActivityMapsBinding
import com.yondikavl.narasiqu.viewModels.MapStoryModels
import com.yondikavl.narasiqu.viewModels.ViewModelsFactory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.yondikavl.narasiqu.R

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private val mapStoryModel by viewModels<MapStoryModels> {
        ViewModelsFactory.getInstance(this)
    }
    private lateinit var mMap: GoogleMap
    private lateinit var bind: ActivityMapsBinding
    private val boundsBuilder = LatLngBounds.Builder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bind = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(bind.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        val lampung = LatLng(-5.562261, 105.547437)
        mMap.addMarker(MarkerOptions().position(lampung).title("Marker in Lampung").snippet("Lampung Selatan"))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lampung, 15f))
//        callMapFromApi(mMap)

        mMap.setOnPoiClickListener { pointOfInterest ->
            val poiMarker = mMap.addMarker(
                MarkerOptions()
                    .position(pointOfInterest.latLng)
                    .title(pointOfInterest.name)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))
            )
            poiMarker?.showInfoWindow()
        }

        getMyLocation()
        setMapStyle()
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            getMyLocation()
        }
    }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", exception)
        }
    }

    companion object {
        private const val TAG = "MapsActivity"
    }

//    private fun callMapFromApi(mMap: GoogleMap) {
//        mapStoryModel.getStory().observe(this){
//            it.forEach { place ->
//                val latLng = LatLng(place.lat!!, place.lon!!)
//                mMap.addMarker(MarkerOptions().position(latLng).title(place.name))
//                boundsBuilder.include(latLng)
//                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
//            }
//        }
//    }
}