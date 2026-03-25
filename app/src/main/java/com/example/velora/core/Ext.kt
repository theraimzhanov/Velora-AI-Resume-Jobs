package com.example.velora.core

import android.util.Patterns
import androidx.navigation.NavController
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

fun String.clean(): String = trim()
fun String.isValidEmail(): Boolean = Patterns.EMAIL_ADDRESS.matcher(this.trim()).matches()

class UiEvents<E> {
    private val channel = Channel<E>(capacity = Channel.BUFFERED)
    val flow = channel.receiveAsFlow()
    suspend fun send(event: E) = channel.send(event)
}