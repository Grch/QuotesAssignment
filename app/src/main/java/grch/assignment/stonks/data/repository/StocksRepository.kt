package grch.assignment.stonks.data.repository

import grch.assignment.stonks.data.api.StocksClient
import grch.assignment.stonks.data.model.SocketResponse
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.processors.BehaviorProcessor
import javax.inject.Inject

class StocksRepository @Inject constructor(
    private val stocksClient: StocksClient
) {

    val disposables = CompositeDisposable()

    private val tickerProcessor = BehaviorProcessor.create<SocketResponse.Ticker.Tick>()

    init {
        disposables.add(stocksClient.observeSocketConnection()
            .filter { it }
            .flatMap { stocksClient.observeTicker() }
            .filter { it is SocketResponse.Ticker }
            .subscribe { response ->
                val ticker = response as SocketResponse.Ticker
                ticker.ticks.forEach {
                    tickerProcessor.onNext(it)
                }
            })
    }

    fun subscribeStocks(stock: String) {
        stocksClient.subscribeStocks(stock)
    }

    fun unsubscribeStocks(stock: String) {
        stocksClient.unsubscribeStocks(stock)

    }

    fun observeTicker() = tickerProcessor.distinctUntilChanged()
}