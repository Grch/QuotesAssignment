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
    val okHttpClient: OkHttpClient,
    val request: Request,
    val moshi: Moshi
) : WebSocketListener(), StonksApi {
    private var webSocket: WebSocket
    private var publishProcessor: PublishProcessor<SocketResponse>
    private val socketConnection: PublishProcessor<Boolean> = PublishProcessor.create()
    private var isConnected = false
    private var backupList = mutableListOf<String>()

    init {
        webSocket = okHttpClient.newWebSocket(request, this)
        okHttpClient.connectionPool().evictAll()
        publishProcessor = PublishProcessor.create()
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        super.onOpen(webSocket, response)
        isConnected = true
        socketConnection.onNext(true)
        backupList.forEach{
            subscribeStocks(it)
        }
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosed(webSocket, code, reason)
        socketConnection.onNext(false)
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        super.onMessage(webSocket, text)
        Timber.d("GRCH socket msg: %s", text)
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
        socketConnection.onNext(false)
        isConnected = false
        Thread.sleep(4000)
        reconnect()
    }

    override fun subscribeStocks(stock: String) {
        Timber.d("Socket SUBSCRIBE : $stock" )
        webSocket.send("SUBSCRIBE: $stock")
        if (!backupList.contains(stock)) {
            backupList.add(stock)
        }
    }

    override fun unsubscribeStocks(stock: String) {
        webSocket.send("UNSUBSCRIBE: $stock")
        backupList.remove(stock)
    }

    override fun reconnect() {
        Timber.d("Socket try reconnect")
        webSocket.close(1000, "No reason!")
        webSocket = okHttpClient.newWebSocket(request, this)
    }

    fun observeTicker(): Flowable<SocketResponse> = publishProcessor.onBackpressureLatest()

    fun observeSocketConnection(): Flowable<Boolean> = socketConnection
}
