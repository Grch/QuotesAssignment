package grch.assignment.quotes.utils

import grch.assignment.quotes.data.model.Product
import grch.assignment.quotes.data.model.SocketResponse.Ticker.Tick

class QuotesConstants {
    companion object {
        fun generateInitialPair(product: Product): Tick {
            return Tick(product, "0.0", "0.0", "0.0")
        }
    }
}