package grch.assignment.stonks.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


sealed class SocketResponse {

    @JsonClass(generateAdapter = true)
    data class Ticker(
        @Json(name = "ticks")
        val ticks: List<Tick>
    ): SocketResponse() {

        @JsonClass(generateAdapter = true)
        data class Tick(
            @Json(name = "s")
            val product: Product,
            @Json(name = "b")
            var bid: String,
            @Json(name = "a")
            var ask: String,
            @Json(name = "spr")
            var spread: String
        )
    }

    object Empty: SocketResponse()
}

