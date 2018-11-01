package org.secfirst.umbrella.whitelabel.data.geolocation

import org.secfirst.umbrella.whitelabel.data.database.reader.LocationInfo

interface GeolocationService {

   suspend fun retrieveAddress(locationName: String): LocationInfo

}