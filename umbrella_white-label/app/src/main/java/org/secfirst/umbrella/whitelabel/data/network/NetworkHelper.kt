package org.secfirst.umbrella.whitelabel.data.network

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.reactivex.Observable
import org.secfirst.umbrella.whitelabel.di.ApiKeyInfo
import retrofit2.http.GET
import javax.inject.Inject

/**
 * Just define possible header for requests.
 */
class ApiHeader @Inject constructor(internal val publicApiHeader: PublicApiHeader) {

    class PublicApiHeader @Inject constructor(@ApiKeyInfo
                                              @Expose
                                              @SerializedName
                                              ("api_key") val apiKey: String)
}

/**
 * Responsible to tracking all API calls.
 */
interface ApiHelper {

    @GET(NetworkEndPoint.GET_BLOG)
    fun getBlogApiCall(): Observable<BlogResponse>
}
