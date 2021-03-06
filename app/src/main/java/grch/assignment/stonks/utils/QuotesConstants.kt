package grch.assignment.stonks.utils

import grch.assignment.stonks.data.model.Product
import grch.assignment.stonks.data.model.SocketResponse

class QuotesConstants {
    companion object {
        fun generateInitialPair(product: Product): SocketResponse.Ticker.Tick {
            return SocketResponse.Ticker.Tick(product, "0.0", "0.0", "0.0")
        }
    }
}