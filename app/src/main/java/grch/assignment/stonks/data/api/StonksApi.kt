package grch.assignment.stonks.data.api

import grch.assignment.stonks.data.model.SocketResponse

import io.reactivex.Flowable
import okhttp3.WebSocket

interface StonksApi {
//    fun observeWebSocketEvent(): Flowable<>
    fun subscribeStocks(stock: String)
    fun unsubscribeStocks(stocks: List<String>)
//    fun observeTicker(): Flowable<Ticker>
}