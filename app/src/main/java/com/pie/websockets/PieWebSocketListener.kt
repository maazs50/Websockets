package com.pie.websockets

import android.util.Log
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class PieWebSocketListener(private val viewModel: MainViewModel): WebSocketListener() {
    private val TAG = "Test"
    override fun onOpen(webSocket: WebSocket, response: Response) {
        super.onOpen(webSocket, response)
        viewModel.setStatus(true)
        webSocket.send("Android device connected!")
        Log.d(TAG,"On Open")
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        super.onMessage(webSocket, text)
        viewModel.addMessages(Pair(false,text))
        Log.d(TAG,"on Message")
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosing(webSocket, code, reason)
//        webSocket.send("Closing!!")
        webSocket.send("Closing manually")
        Log.d(TAG,"on Closing")
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosed(webSocket, code, reason)
        viewModel.setStatus(false)
        Log.d(TAG, "Closed!! $code $reason")
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        super.onFailure(webSocket, t, response)
        Log.d(TAG, "Closed!! ${t.message} $response")
    }
}