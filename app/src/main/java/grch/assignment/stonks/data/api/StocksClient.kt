package grch.assignment.stonks.data.api

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import grch.assignment.stonks.data.model.SocketResponse
import io.reactivex.Flowable
import io.reactivex.processors.PublishProcessor
import okhttp3.*
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StocksClient @Inject constructor(
    okHttpClient: OkHttpClient,
    request: Request,
    val moshi: Moshi
) : StonksApi, WebSocketListener() {
    private val webSocket: WebSocket
    lateinit var publishProcessor: PublishProcessor<SocketResponse>
    private val socketConnection: PublishProcessor<Boolean> = PublishProcessor.create()

    init {
        webSocket = okHttpClient.newWebSocket(request, this)
    }


    override fun onOpen(webSocket: WebSocket, response: Response) {
        super.onOpen(webSocket, response)
        publishProcessor = PublishProcessor.create()
        Timber.d("GRCH Socket openedd")
        socketConnection.onNext(true)
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosed(webSocket, code, reason)
        socketConnection.onNext(false)
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        super.onMessage(webSocket, text)
        Timber.d("GRCH socket msg: " + text)
        val jsonAdapter: JsonAdapter<SocketResponse.Ticker> =
            moshi.adapter(SocketResponse.Ticker::class.java)
        val ticker = try {
            jsonAdapter.fromJson(text)
        } catch (t: Throwable) {
            SocketResponse.Empty
        }
        publishProcessor.onNext(ticker)
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        super.onFailure(webSocket, t, response)
        Timber.e("GRCH Socket FAIL: %s", t.localizedMessage)
//        socketConnection.onNext(false)
    }

    override fun subscribeStocks(stock: String) {
        webSocket.send("SUBSCRIBE: $stock")
    }

    override fun unsubscribeStocks(stocks: List<String>) {
        publishProcessor = PublishProcessor.create()
        stocks.forEach {
            webSocket.send("UNSUBSCRIBE: $it")
        }
    }

    fun observeTicker(): Flowable<SocketResponse> = publishProcessor.onBackpressureLatest()

    fun observeSocketConnection(): Flowable<Boolean> = socketConnection
}
