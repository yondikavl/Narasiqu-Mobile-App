package com.yondikavl.narasiqu

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.yondikavl.narasiqu.databinding.ActivityMapsBinding
import com.yondikavl.narasiqu.viewModels.MapStoryModels
import com.yondikavl.narasiqu.viewModels.ViewModelsFactory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions

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

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
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

        val local = LatLng(-6.137962924705028, 106.78188193263647)
        mMap.addMarker(MarkerOptions().position(local).title("mantap"))
        boundsBuilder.include(local)
        mMap.moveCamera(CameraUpdateFactory.newLatLng(local))

        callMapFromApi(mMap)
    }

    private fun callMapFromApi(mMap: GoogleMap) {
        mapStoryModel.getStory().observe(this){
            it.forEach { place ->
                val latLng = LatLng(place.lat!!, place.lon!!)
                mMap.addMarker(MarkerOptions().position(latLng).title(place.name))
                boundsBuilder.include(latLng)
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
            }
        }
    }
}