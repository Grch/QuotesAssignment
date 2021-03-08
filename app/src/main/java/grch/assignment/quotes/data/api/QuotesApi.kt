package grch.assignment.quotes.data.api

import grch.assignment.quotes.data.model.SocketResponse
import io.reactivex.Flowable

interface QuotesApi {
    fun subscribeStocks(stock: String)
    fun unsubscribeStocks(stock: String)
    fun observeTicker(): Flowable<SocketResponse>
}