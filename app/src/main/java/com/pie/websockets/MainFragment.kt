package com.pie.websockets

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class MainFragment : Fragment() {
    private lateinit var viewModel: MainViewModel
    private lateinit var webSocketListener: PieWebSocketListener
    private val okHttpClient = OkHttpClient()
    private var webSocket: WebSocket? =null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        webSocketListener = PieWebSocketListener(viewModel)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val messageET = view.findViewById<EditText>(R.id.messageET)
        val sendMessageButton  = view.findViewById<ImageButton>(R.id.sendButton)
        val connectButton = view.findViewById<Button>(R.id.connectButton)
        val disconnectButton = view.findViewById<Button>(R.id.disconnectButton)
        val statusTV = view.findViewById<TextView>(R.id.statusTV)
        val messageTV = view.findViewById<TextView>(R.id.messageTV)

        viewModel.socketStatus.observe(viewLifecycleOwner){status->
            statusTV.text = if (status) "Connected!" else "Disconnected!"
        }

        var text = ""
        viewModel.messages.observe(viewLifecycleOwner){ message->
            text+="${if (message.first) "You: " else "Other: " }${message.second}\n"
            messageTV.text = text
        }
        connectButton.setOnClickListener {
            webSocket = okHttpClient.newWebSocket(createRequest(),webSocketListener)
        }

        disconnectButton.setOnClickListener {
            webSocket?.close(1000,"Closed manually!")
        }

        sendMessageButton.setOnClickListener {
            val text = messageET.text.toString()

                webSocket?.send(text)
                viewModel.addMessages(Pair(true,text))

        }

    }
    //On Connect Button
    private fun createRequest(): Request{
        val url = "wss://${Constants.CLUSTER_ID}.piesocket.com/v3/1?api_key=${Constants.API_KEY}"
        return Request.Builder().url(url).build()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        okHttpClient.dispatcher.executorService.shutdown()
    }
}