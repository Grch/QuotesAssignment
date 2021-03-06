package grch.assignment.stonks.data.repository


import grch.assignment.stonks.data.api.StocksClient
import grch.assignment.stonks.data.model.Product
import grch.assignment.stonks.data.model.SocketResponse
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.processors.BehaviorProcessor
import timber.log.Timber
import java.util.Arrays.asList
import javax.inject.Inject

class StocksRepository @Inject constructor(
    val stocksClient: StocksClient
) {

    val disposables = CompositeDisposable()

    private val tickerProcessor = BehaviorProcessor.create<SocketResponse.Ticker.Tick>()

    init {
        disposables.add(stocksClient.observeSocketConnection()
            .doOnNext { Timber.d("socket connected: $it") }
            .filter { it }
            .flatMap { stocksClient.observeTicker() }
            .filter { it is SocketResponse.Ticker }
            .subscribe { response ->
                val ticker = response as SocketResponse.Ticker
                ticker.ticks.forEach {
                    Timber.d("socket msg: %s", it)
                    tickerProcessor.onNext(it)
                }
            })
    }

    fun subscribeStocks(product: List<String>) {
        product.forEach {
            stocksClient.subscribeStocks(it)
        }
    }

    fun observeTicker() = tickerProcessor
}