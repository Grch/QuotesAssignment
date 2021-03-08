package grch.assignment.stonks.utils

import grch.assignment.stonks.data.model.Product
import grch.assignment.stonks.data.model.SocketResponse.Ticker.Tick

class QuotesConstants {
    companion object {
        fun generateInitialPair(product: Product): Tick {
            return Tick(product, "0.0", "0.0", "0.0")
        }
    }
}