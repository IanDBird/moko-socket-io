/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package com.icerockdev.library

import dev.icerock.moko.socket.Socket
import dev.icerock.moko.socket.SocketEvent
import dev.icerock.moko.socket.SocketOptions

class Testing {
    val socket = Socket(
        endpoint = "https://socketio-chat-h9jt.herokuapp.com",
        config = SocketOptions(
            queryParams = mapOf("param1" to "1", "param2" to "2"),
            transport = SocketOptions.Transport.WEBSOCKET
        )
    ) {
        on(SocketEvent.Connect) {
            println("connect")
        }

        on(SocketEvent.Disconnect) {
            println("disconnect")
        }

        on(SocketEvent.Error) {
            println("error $it")
        }

        listOf(
            "input",
            "login",
            "new message",
            "user joined",
            "user left",
            "typing",
            "stop typing"
        ).forEach { eventName ->
            on(eventName) { data ->
                println("$eventName $data")
            }
        }
    }

    fun login() {
        socket.emit("add user", "mokoSocketIo")
    }
}
