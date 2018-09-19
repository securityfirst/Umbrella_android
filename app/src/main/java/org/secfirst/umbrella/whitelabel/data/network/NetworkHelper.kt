package org.secfirst.umbrella.whitelabel.data.network

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.experimental.Deferred
import okhttp3.ResponseBody
import org.secfirst.umbrella.whitelabel.di.ApiKeyInfo
import retrofit2.http.GET
import retrofit2.http.Url
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
    fun getBlogApiCall(): Deferred<BlogResponse>

    @GET
    fun getRss(@Url url: String): Deferred<ResponseBody>
}
