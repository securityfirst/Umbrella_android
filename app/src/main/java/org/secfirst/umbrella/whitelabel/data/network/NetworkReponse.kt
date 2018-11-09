package org.secfirst.umbrella.whitelabel.data.network

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.squareup.moshi.FromJson
import com.squareup.moshi.Json
import com.squareup.moshi.JsonQualifier
import com.squareup.moshi.ToJson
import kotlinx.android.parcel.Parcelize


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
@Parcelize
data class FeedItemResponse(
        @SerializedName("moduleTitle")
        var title: String = "",
        @SerializedName("description")
        var description: String = "",
        @SerializedName("url")
        var url: String = "",
        @field:Json(name = "")
        @SerializedName("updated_at")
        var updatedAt: Long = 0) : Parcelable


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
