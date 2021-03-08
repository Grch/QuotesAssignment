package grch.assignment.quotes.data.repository

import grch.assignment.quotes.data.api.SocketClient
import grch.assignment.quotes.data.model.SocketResponse
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.processors.BehaviorProcessor
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class QuotesRepository @Inject constructor(
    private val socketClient: SocketClient
) {

    private val disposables = CompositeDisposable()

    private val tickerProcessor = BehaviorProcessor.create<SocketResponse.Ticker.Tick>()

    init {
        disposables.add(socketClient.observeSocketConnection()
            .filter { it }
            .subscribeOn(Schedulers.io())
            .flatMap { socketClient.observeTicker() }
            .filter { it is SocketResponse.Ticker }
            .subscribe { response ->
                val ticker = response as SocketResponse.Ticker
                ticker.ticks.forEach {
                    tickerProcessor.onNext(it)
                }
            })
    }

    fun subscribeProduct(stock: String) {
        socketClient.subscribeStocks(stock)
    }

    fun unsubscribeProduct(stock: String) {
        socketClient.unsubscribeStocks(stock)
    }

    fun observeTicker() = tickerProcessor.distinctUntilChanged()
}