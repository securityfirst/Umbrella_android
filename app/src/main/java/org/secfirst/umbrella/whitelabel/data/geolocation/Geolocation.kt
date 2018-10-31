package org.secfirst.umbrella.whitelabel.data.geolocation

import android.location.Geocoder
import kotlinx.coroutines.experimental.withContext
import org.secfirst.umbrella.whitelabel.misc.AppExecutors.Companion.ioContext

interface Geolocation {

    suspend fun getAddress(locationName: String, geocoder: Geocoder): List<String> {
        val addressLabelList = mutableListOf<String>()
        withContext(ioContext) {
            val addressList = geocoder.getFromLocationName(locationName, 5)
            addressList.forEach { fullAddress ->
                val city = fullAddress.locality
                val country = fullAddress.countryName
                val address = fullAddress.getAddressLine(0)
                addressLabelList.add("$address, $city, $country")
            }
        }
        return addressLabelList
    }
}