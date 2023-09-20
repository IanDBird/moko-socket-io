/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.socket

import io.socket.client.Manager
import io.socket.client.Socket

actual sealed class SocketEvent<T> : Mapper<T> {
    actual object Connect : SocketEvent<Unit>(), Mapper<Unit> by UnitMapper() {
        override val socketIoEvents: List<String> = listOf(Socket.EVENT_CONNECT)
    }

    actual object Disconnect : SocketEvent<Unit>(), Mapper<Unit> by UnitMapper() {
        override val socketIoEvents: List<String> = listOf(Socket.EVENT_DISCONNECT)
    }

    actual object Error : SocketEvent<Throwable>() {
        override val socketIoEvents: List<String> = listOf(Socket.EVENT_CONNECT_ERROR)

        override fun mapper(array: Array<out Any>): Throwable {
            return array[0] as Throwable
        }
    }

    abstract val socketIoEvents: List<String>
}

actual sealed class SocketManagerEvent<T> : Mapper<T> {
    actual object Reconnect : SocketManagerEvent<Unit>(), Mapper<Unit> by UnitMapper() {
        override val socketManagerEvents: List<String> = listOf(Manager.EVENT_RECONNECT)
    }

    actual object ReconnectAttempt : SocketManagerEvent<Unit>(), Mapper<Unit> by UnitMapper() {
        override val socketManagerEvents: List<String> = listOf(Manager.EVENT_RECONNECT_ATTEMPT)
    }

    abstract val socketManagerEvents: List<String>
}

private class UnitMapper : Mapper<Unit> {
    override fun mapper(array: Array<out Any>) = Unit
}
