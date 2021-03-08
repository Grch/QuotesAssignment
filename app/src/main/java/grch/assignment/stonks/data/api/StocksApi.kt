package grch.assignment.stonks.data.api

import grch.assignment.stonks.data.model.SocketResponse
import io.reactivex.Flowable

interface StocksApi {
    fun subscribeStocks(stock: String)
    fun unsubscribeStocks(stock: String)
    fun reconnect()
    fun observeTicker(): Flowable<SocketResponse>
}