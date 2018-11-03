package org.secfirst.umbrella.whitelabel.data.network

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.squareup.moshi.FromJson
import com.squareup.moshi.Json
import com.squareup.moshi.JsonQualifier
import com.squareup.moshi.ToJson


@Suppress("MatchingDeclarationName")
data class BlogResponse(@Expose
                        @SerializedName("status_code")
                        private var statusCode: String? = null,

                        @Expose
                        @SerializedName("message")
                        private var message: String? = null,

                        @Expose
                        @SerializedName("date")
                        var data: List<Blog>? = null)

data class FeedResponse(val feedItemResponse: List<FeedItemResponse>)

data class FeedItemResponse(
        @Json(name = "title")
        var title: String? = null,
        @Json(name = "description")
        var description: String? = null,
        @Json(name = "url")
        var url: String? = null,
        @Json(name = "updated_at")
        var updatedAt: Int? = null)


class FeedJsonConverter {
    @Wrapped
    @FromJson
    fun fromJson(json: FeedResponse): List<FeedItemResponse> {
        return json.feedItemResponse
    }

    @ToJson
    fun toJson(@Wrapped value: List<FeedItemResponse>): FeedResponse {
        throw UnsupportedOperationException()
    }
}

@Retention(AnnotationRetention.RUNTIME)
@JsonQualifier
annotation class Wrapped
