package org.secfirst.umbrella.data.network

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json
import com.squareup.moshi.JsonQualifier
import kotlinx.android.parcel.Parcelize


@Suppress("MatchingDeclarationName")
data class BlogResponse(
    @Expose
    @SerializedName("status_code")
    private var statusCode: String? = null,

    @Expose
    @SerializedName("message")
    private var message: String? = null,

    @Expose
    @SerializedName("date")
    var data: List<Blog>? = null
)

@Parcelize
data class FeedItemResponse(
    @SerializedName("title")
    var title: String = "",
    @SerializedName("description")
    var description: String = "",
    @SerializedName("url")
    var url: String = "",
    @field:Json(name = "")
    @SerializedName("updated_at")
    var updatedAt: Long = 0
) : Parcelable

@Retention(AnnotationRetention.RUNTIME)
@JsonQualifier
annotation class Wrapped
