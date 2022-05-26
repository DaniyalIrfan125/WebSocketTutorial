package de.example.websockettutorial

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import kotlinx.android.synthetic.main.activity_main.*
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import org.json.JSONObject
import java.lang.Exception
import java.net.URI
import javax.net.ssl.SSLSocketFactory

class MainActivity : AppCompatActivity() {

    private lateinit var webSocketClient: WebSocketClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()
        initWebSocket()
    }

    override fun onPause() {
        super.onPause()
        webSocketClient.close()
    }

    private fun initWebSocket() {
        val coinbaseUri: URI? = URI(WEB_SOCKET_URL)

        createWebSocketClient(coinbaseUri)

//        val socketFactory: SSLSocketFactory = SSLSocketFactory.getDefault() as SSLSocketFactory
//        webSocketClient.setSocketFactory(socketFactory)

//        webSocketClient.setConnectTimeout(10000)
//        webSocketClient.setReadTimeout(60000)
//        webSocketClient.addHeader("Origin", "http://developer.example.com")
//        webSocketClient.enableAutomaticReconnection(5000)

       // webSocketClient.
        webSocketClient.connect()
    }

    private fun createWebSocketClient(coinbaseUri: URI?) {
        webSocketClient = object : WebSocketClient(coinbaseUri) {

            override fun onOpen(handshakedata: ServerHandshake?) {
                Log.d(TAG, "onOpen")
                subscribe()
            }

            override fun onMessage(message: String?) {
                Log.d(TAG, "onMessage: $message")
                setUpBtcPriceText(message)
            }

            override fun onClose(code: Int, reason: String?, remote: Boolean) {
                Log.d(TAG, "onClose")
           //    unsubscribe()
            }

            override fun onError(ex: Exception?) {
                Log.e(TAG, "onError: ${ex?.message}")
            }

        }


    }

    private fun subscribe() {
//        webSocketClient.send(
//            "{\n" +
//                    "    \"type\": \"subscribe\",\n" +
//                    "    \"channels\": [{ \"name\": \"ticker\", \"product_ids\": [\"BTC-EUR\"] }]\n" +
//                    "}"
//        )

//        webSocketClient.send(
//            "{\"symbol\": \"ADA/USDT\"}"
//        )

//
//        webSocketClient.send(
//            JSONObject()
//                .put("method", "MARKET_CAPITAL")
//                .toString())

        webSocketClient.send(
            JSONObject()
                .put("method", "ORDER_BOOK")
                .put("symbol", "ADA/USDT")
                .toString())

    }

    private fun setUpBtcPriceText(message: String?) {
        message?.let {
            val moshi = Moshi.Builder().build()
            val adapter: JsonAdapter<DemoData> = moshi.adapter(DemoData::class.java)
            val bitcoin = adapter.fromJson(message)
            runOnUiThread { btc_price_tv.text = "1 BTC: ${bitcoin?.data} €" }
        }
    }

    private fun unsubscribe() {
        webSocketClient.send(
            "{\n" +
                    "    \"type\": \"unsubscribe\",\n" +
                    "    \"channels\": [\"ticker\"]\n" +
                    "}"
        )
    }

    companion object {
        const val WEB_SOCKET_URL = "ws://54.173.26.51/banking/stream"
        const val TAG = "Coinbase"
    }

}