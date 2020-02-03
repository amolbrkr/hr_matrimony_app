package com.halalrishtey

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.util.Log
import java.util.*

object CustomUtils {
    fun convertCoordsToAddr(context: Context, latitude: Double, longitude: Double): Address? {
        val geocoder = Geocoder(context, Locale.getDefault())

        return try {
            geocoder.getFromLocation(latitude, longitude, 1)[0]
        } catch (e: Exception) {
            Log.d("CustomUtils", "Error: ${e.message}")
            null
        }
    }
}