package com.makeshaadi.viewmodels

import android.content.Context
import android.location.Location
import android.util.Log
import androidx.lifecycle.LiveData
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices


class LocationLiveData(context: Context) : LiveData<Location?>() {
    private val fusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult?) {
            super.onLocationResult(p0)
            p0 ?: return
            for (loc in p0.locations) {
                value = loc
            }
        }
    }

    companion object {
        val locationRequest: LocationRequest = LocationRequest.create().apply {
            interval = 60000
            fastestInterval = 10000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    override fun onActive() {
        super.onActive()

        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.also {
                value = location
            }
        }.addOnFailureListener { e: Exception ->
            Log.d("LocationLiveData", e.message ?: "FusedProviderClient Failed!")
            value = null
        }

        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            null
        )
    }

    override fun onInactive() {
        super.onInactive()
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }


}