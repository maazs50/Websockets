package com.pie.websockets

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket

class MainViewModel: ViewModel() {
    private val _socketStatus = MutableLiveData<Boolean>()
    val socketStatus: LiveData<Boolean>
        get() = _socketStatus

    private val _messages = MutableLiveData<Pair<Boolean, String>>()
    val messages: LiveData<Pair<Boolean, String>> = _messages

    private lateinit var webSocketListener: PieWebSocketListener
    private lateinit var okHttpClient: OkHttpClient
    private var webSocket: WebSocket? =null
    init {
        webSocketListener = PieWebSocketListener(this)
        okHttpClient = OkHttpClient()
    }

    fun setStatus(status: Boolean) {
        viewModelScope.launch(Dispatchers.Main) {
            _socketStatus.value = status
        }
    }

    fun addMessages(message: Pair<Boolean, String>) = viewModelScope.launch(Dispatchers.Main){
        if (_socketStatus.value == true){
            _messages.value = message
        }
    }

    fun makeConnection(){
        webSocket = okHttpClient.newWebSocket(createRequest(),webSocketListener)
    }
    fun createRequest(): Request {
        val url = "wss://${Constants.CLUSTER_ID}.piesocket.com/v3/1?api_key=${Constants.API_KEY}"
        return Request.Builder().url(url).build()
    }

    fun disconnect(){
        webSocket?.close(1000,"Closed manually!")
    }

    fun sendMessage(text: String){
        webSocket?.send(text)
        addMessages(Pair(true,text))
    }

    override fun onCleared() {
        super.onCleared()
        okHttpClient.dispatcher.executorService.shutdown()
    }
}