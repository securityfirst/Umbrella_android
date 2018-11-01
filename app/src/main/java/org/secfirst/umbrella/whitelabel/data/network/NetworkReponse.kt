package org.secfirst.umbrella.whitelabel.data.network

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

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

data class FeedListResponse(private val feedItemResponse: List<FeedItemResponse>)

data class FeedItemResponse(@SerializedName("title")
                            @Expose
                            var title: String? = null,
                            @SerializedName("description")
                            @Expose
                            var description: String? = null,
                            @SerializedName("url")
                            @Expose
                            var url: String? = null,
                            @SerializedName("updated_at")
                            @Expose
                            var updatedAt: Int? = null)

