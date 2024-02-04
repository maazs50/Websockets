package com.pie.websockets

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel: ViewModel() {
    private val _socketStatus = MutableLiveData<Boolean>()
    val socketStatus: LiveData<Boolean>
        get() = _socketStatus

    private val _messages = MutableLiveData<Pair<Boolean, String>>()
    val messages: LiveData<Pair<Boolean, String>> = _messages

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
}