package org.secfirst.umbrella.whitelabel.data.geolocation

import android.location.Geocoder
import javax.inject.Inject

class GeolocationServiceImp @Inject constructor(private val geocoder: Geocoder,
                                                private val geolocation: Geolocation) : GeolocationService {

    override suspend fun retrieveAddress(locationName: String): List<String> = geolocation.getAddress(locationName, geocoder)
}