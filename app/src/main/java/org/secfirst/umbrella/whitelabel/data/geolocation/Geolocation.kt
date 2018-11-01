package org.secfirst.umbrella.whitelabel.data.geolocation

import android.location.Geocoder
import android.util.Log
import kotlinx.coroutines.experimental.withContext
import org.secfirst.umbrella.whitelabel.data.database.reader.LocationInfo
import org.secfirst.umbrella.whitelabel.misc.AppExecutors.Companion.ioContext

interface Geolocation {

    suspend fun getAddress(locationName: String, geocoder: Geocoder): LocationInfo {
        val addressLabelList = mutableListOf<String>()
        var countryCode = ""
        var locationInfo = LocationInfo()
        withContext(ioContext) {
            if (locationName.isNotBlank()) {
                try {
                    val addressList = geocoder.getFromLocationName(locationName, 5)
                    addressList.forEach { fullAddress ->
                        countryCode = fullAddress.countryCode ?: ""
                        addressLabelList.add(fullAddress.getAddressLine(0))
                    }
                    locationInfo = LocationInfo(addressLabelList, countryCode)
                } catch (e: Exception) {
                    Log.e("test", "geolocation error.")
                }
            }
        }
        return locationInfo
    }
}