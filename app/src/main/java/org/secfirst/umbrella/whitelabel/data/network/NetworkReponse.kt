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



