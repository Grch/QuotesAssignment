package grch.assignment.quotes.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Subscribe(
    @Json(name = "subscribed_count")
    val subscribedCound: Int,
    @Json(name = "subscribed_list")
    val subscribedList: SocketResponse.Ticker

)