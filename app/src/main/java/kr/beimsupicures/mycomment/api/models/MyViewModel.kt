package kr.beimsupicures.mycomment.api.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MyViewModel : ViewModel() {

    private val sendMessage = MutableLiveData<Event<String>>()

    val getMessage: LiveData<Event<String>>
        get() = sendMessage

    fun setMessage(message: String) {
        sendMessage.value = Event(message)
    }
    //
    private val sendMessage2 = MutableLiveData<Event<String>>()

    val getMessage2: LiveData<Event<String>>
        get() = sendMessage2

    fun setMessage2(message: String) {
        sendMessage2.value = Event(message)
    }
    //
    private val replyMessage = MutableLiveData<Event<String>>()

    val getReply: LiveData<Event<String>>
        get() = replyMessage

    fun setReply(message: String) {
        replyMessage.value = Event(message)
    }
    //
    private val replyMessage2 = MutableLiveData<Event<String>>()

    val getReply2: LiveData<Event<String>>
        get() = replyMessage2

    fun setReply2(message: String) {
        replyMessage2.value = Event(message)
    }
}