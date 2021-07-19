package com.example.geolocation

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import java.util.*


class MainActivity : AppCompatActivity() {
    private var PERMISSION_ID=52
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    var latitude: Double = 0.0
    var longitude: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        fusedLocationProviderClient=LocationServices.getFusedLocationProviderClient(this)
       findViewById<Button>(R.id.btn).setOnClickListener {
           getLastLocation()

        }

    }

    @SuppressLint("MissingPermission")
    private fun getLastLoc(){
        val mLocationManager =
            this.getSystemService(LOCATION_SERVICE) as LocationManager
        val providers: List<String> = mLocationManager.getProviders(true)
        var bestLocation: Location? = null
        for (provider in providers) {
            val l: Location = mLocationManager.getLastKnownLocation(provider) ?: continue
            if (bestLocation == null || l.accuracy < bestLocation.accuracy) {
                // Found best last known location: %s", l);
                bestLocation = l
            }
        }
        val lat = bestLocation?.latitude
        val lon = bestLocation?.longitude
        Log.d(TAG, "getLastLoc: $lat  $lon")
    }
    private fun getLastLocation(){
        if (checkPermission()){
            if (isLocationEnabled()){
              //  getLastLoc()
                fusedLocationProviderClient.lastLocation.addOnCompleteListener { task->
                    var location:Location?=task.result
                    if (location==null){
                        getNewLocation()
                }else{
                    var t=findViewById<TextView>(R.id.Locationtxt)
                        t.text=("latitude: "+"${location.latitude} "+"longitude: "+"${location.longitude}")
                        latitude=location.latitude
                        longitude=location.longitude
                        Toast.makeText(this,"${latitude},${longitude}",Toast.LENGTH_SHORT).show()

                    }

                }
            }
            else{
                Toast.makeText(this,"enable your location",Toast.LENGTH_SHORT).show()
            }
        }
        else{
           RequestPermission()
        }
    }



    @SuppressLint("MissingPermission")
    private fun getNewLocation(){
       val locationRequest = LocationRequest.create()
        locationRequest.apply {
            locationRequest.priority=LocationRequest.PRIORITY_HIGH_ACCURACY
            locationRequest.interval=0
            locationRequest.fastestInterval=0
            locationRequest.numUpdates=2

            fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback,Looper.getMainLooper())

        }
    }
    private val locationCallback=object:LocationCallback(){
        override fun onLocationResult(p0: LocationResult) {
            var lastLocation=p0.lastLocation
            var f=findViewById<TextView>(R.id.Locationtxt)
            f.text=("latitude"+"${lastLocation.latitude}")

            //Toast.makeText(this@MainActivity,"${lastLocation.latitude},${lastLocation.longitude}",Toast.LENGTH_SHORT).show()
            Log.d("locate","latitude "+lastLocation.latitude)
            super.onLocationResult(p0)
        }
    }

    private fun checkPermission():Boolean{
        if (ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)
            ==PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION)
            ==PackageManager.PERMISSION_GRANTED)
        {
            return true
        }
        return false
    }
    private fun RequestPermission(){
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION,android.Manifest.permission.ACCESS_FINE_LOCATION),PERMISSION_ID)
    }

    private fun isLocationEnabled():Boolean{
        var locationManager:LocationManager=getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        if (requestCode==PERMISSION_ID){
            if (grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Log.d("Debug","You have the permission")
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}
