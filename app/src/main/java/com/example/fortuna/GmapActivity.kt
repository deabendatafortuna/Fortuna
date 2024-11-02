package com.example.fortuna

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlin.math.log2


class GmapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gmap)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        // Controlla e richiedi i permessi
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_REQUEST_CODE)
            return
        }

        map.isMyLocationEnabled = true

        // Ottieni la posizione corrente
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                val zoomLevel = calcolaZoomLevel(15.0) // 15km in metri
                val currentLatLng = LatLng(it.latitude, it.longitude)
                map.addMarker(MarkerOptions().position(currentLatLng).title("La tua posizione"))
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, zoomLevel))
                /* map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f)) */
            }


        }
    }

    companion object {
        private const val LOCATION_REQUEST_CODE = 1
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                onMapReady(map)
            }
        }
    }

    private fun calcolaZoomLevel(scalaInMetri: Double): Float {
        val larghezzaSchermoInPixel = resources.displayMetrics.widthPixels
        val metriPerPixel = scalaInMetri * 1000 / larghezzaSchermoInPixel
        val zoomLevel = log2(20015109.35 / metriPerPixel) - 8
        return zoomLevel.toFloat()
    }
}
