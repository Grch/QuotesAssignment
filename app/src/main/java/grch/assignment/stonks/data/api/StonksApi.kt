package grch.assignment.stonks.data.api

interface StonksApi {
//    fun observeWebSocketEvent(): Flowable<>
    fun subscribeStocks(stock: String)
    fun unsubscribeStocks(stock: String)
    fun reconnect()
//    fun observeTicker(): Flowable<Ticker>
}