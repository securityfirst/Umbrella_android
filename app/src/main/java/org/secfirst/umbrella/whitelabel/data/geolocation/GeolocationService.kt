package org.secfirst.umbrella.whitelabel.data.geolocation

interface GeolocationService {

   suspend fun retrieveAddress(locationName: String): List<String>

}