package grch.assignment.quotes.data.api

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import grch.assignment.quotes.data.model.SocketResponse
import io.reactivex.Flowable
import io.reactivex.processors.PublishProcessor
import okhttp3.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SocketClient @Inject constructor(
    private val okHttpClient: OkHttpClient,
    private val request: Request,
    val moshi: Moshi
) : WebSocketListener(), QuotesApi {
    private var webSocket: WebSocket
    private var publishProcessor: PublishProcessor<SocketResponse>
    private val socketConnection: PublishProcessor<Boolean> = PublishProcessor.create()
    private var backupSubscriptionList = mutableListOf<String>()

    init {
        webSocket = okHttpClient.newWebSocket(request, this)
        okHttpClient.connectionPool().evictAll()
        publishProcessor = PublishProcessor.create()
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        super.onOpen(webSocket, response)
        socketConnection.onNext(true)
        backupSubscriptionList.forEach {
            subscribeStocks(it)
        }
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosed(webSocket, code, reason)
        socketConnection.onNext(false)
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        super.onMessage(webSocket, text)
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
        socketConnection.onNext(false)
        Thread.sleep(4000)
        reconnect()
    }

    override fun subscribeStocks(stock: String) {
        webSocket.send("SUBSCRIBE: $stock")
        if (!backupSubscriptionList.contains(stock)) {
            backupSubscriptionList.add(stock)
        }
    }

    override fun unsubscribeStocks(stock: String) {
        webSocket.send("UNSUBSCRIBE: $stock")
        backupSubscriptionList.remove(stock)
    }

    private fun reconnect() {
        webSocket.close(1000, "No reason!")
        webSocket = okHttpClient.newWebSocket(request, this)
    }

    override fun observeTicker(): Flowable<SocketResponse> = publishProcessor.onBackpressureLatest()

    fun observeSocketConnection(): Flowable<Boolean> = socketConnection
}
