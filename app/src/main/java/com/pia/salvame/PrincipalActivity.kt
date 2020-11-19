package com.pia.salvame

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_principal.*
import kotlinx.android.synthetic.main.item_contact.*


private val db = FirebaseFirestore.getInstance()


class PrincipalActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastLocation:Location
    private lateinit var locationCallback: LocationCallback
    private var emergencia = false


    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

     override fun onBackPressed() {
        //super.onBackPressed()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_principal)



        val bundle = intent.extras
        val email = bundle?.getString("email")


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        emergenciaButton.setOnClickListener {
            emergencia = true
            detenerButton.setVisibility(View.VISIBLE)
            emergenciaButton.setVisibility(View.GONE)

            db.collection("users").document(email?:"").collection("security").document("state").set(
                hashMapOf("emergency" to true)
            )

        }

        detenerButton.setOnClickListener {
            emergencia=false
            detenerButton.setVisibility(View.GONE)
            emergenciaButton.setVisibility(View.VISIBLE)
            db.collection("users").document(email?:"").collection("security").document("state").set(
                hashMapOf("emergency" to false))
        }



                    locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult?) {
                    locationResult ?: return
                    for (location in locationResult.locations) {
                        Log.e("Ubicacion: ", "Latitud: " + location?.latitude + " Longitud: " + location?.longitude)
                        if(emergencia==true) {
                            db.collection("users").document(email
                                    ?: "").collection("security").document("location").set(
                                    hashMapOf("longitude" to location?.longitude,
                                            "latitude" to location?.latitude)
                            )
                        }

                    }
                }
            }


goToContactsButton.setOnClickListener {
    showContacts(email?:"") }

        goToAccountButton.setOnClickListener {
            showHome(email?:"")
        }

        goToHomeButton.isClickable=false
        goToAccountButton.isClickable=true
        goToContactsButton.isClickable=true


        }



    override fun onResume() {
        super.onResume()
        startLocationUpdates()
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }

        val locationRequest = LocationRequest.create()?.apply {
            interval = 3000
            fastestInterval = 1500
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        }

        fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper())
    }

    private fun showContacts (email:String) {
        val contactsIntent: Intent = Intent(this, ContactsActivity::class.java).apply{
            putExtra("email", email)
        }
        startActivity(contactsIntent)
    }

    private fun showHome (email:String) {
        val homeIntent: Intent = Intent(this, HomeActivity::class.java).apply{
            putExtra("email", email)
        }
        startActivity(homeIntent)
    }



    }