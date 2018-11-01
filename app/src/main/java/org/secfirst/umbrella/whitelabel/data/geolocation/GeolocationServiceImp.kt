package org.secfirst.umbrella.whitelabel.data.geolocation

import android.location.Geocoder
import org.secfirst.umbrella.whitelabel.data.database.reader.LocationInfo
import javax.inject.Inject

class GeolocationServiceImp @Inject constructor(private val geocoder: Geocoder,
                                                private val geolocation: Geolocation) : GeolocationService {

    override suspend fun retrieveAddress(locationName: String): LocationInfo = geolocation.getAddress(locationName, geocoder)
}