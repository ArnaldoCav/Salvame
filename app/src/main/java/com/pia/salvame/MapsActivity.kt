package com.pia.salvame

import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore


class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    //Variable para obtener ubicacion actual del usuario
    private lateinit var  fusedLocationClient: FusedLocationProviderClient
//Variable para ultima ubicacion conocida
    private lateinit var lastLocation:Location
    private lateinit var locationCallback: LocationCallback
    private var contador = 0.0

    companion object{
        private const val LOCATION_PERMISSION_REQUEST_OBJECT = 1
    }

    private lateinit var mMap: GoogleMap
    private val db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        val bundle = intent.extras
        val email = bundle?.getString("email")

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

    }



    private fun countDownTimer(email:String){
        val timer = object : CountDownTimer(3000, 1500) {
            override fun onTick(millisUntilFinished: Long) {


            }

            override fun onFinish() {
                Log.e("TIMER", "MENSAJE DEL TIMER")

                onMapReady(mMap)
            }
        }.start()
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val bundle = intent.extras
        val email = bundle?.getString("email")
        mMap.uiSettings.isZoomControlsEnabled = true





       db.collection("users").document(email ?: "").collection("security").document("location").get().addOnSuccessListener {
            var latitude = it.getDouble("latitude")
            var longitude = it.getDouble("longitude")
            var ubicacion = LatLng(latitude ?: 0.0, longitude ?: 0.0)
           mMap.clear()
            mMap.addMarker(MarkerOptions().position(ubicacion))

            mMap.setOnMarkerClickListener(this)

        }
setupMap()
        countDownTimer(email?:"")



    }





    override fun onMarkerClick(p0: Marker?): Boolean {
        return false
    }

    private fun setupMap(){
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_OBJECT)
            return
        }

        mMap.isMyLocationEnabled=true

        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location->


            if(location != null)
            {
                if(contador == 0.0) {
                    lastLocation = location
                    val currentLatLong = LatLng(location.latitude, location.longitude)
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLong, 12f))
                    contador+=0.1
                }
            }

        }


    }

}