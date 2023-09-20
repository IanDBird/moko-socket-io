/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.socket

import cocoapods.mokoSocketIo.SocketEventConnect
import cocoapods.mokoSocketIo.SocketEventDisconnect
import cocoapods.mokoSocketIo.SocketEventError
import cocoapods.mokoSocketIo.SocketEvent as SocketIoEvent
import cocoapods.mokoSocketIo.SocketManagerEvent as SocketIoManagerEvent

actual sealed class SocketEvent<T> : Mapper<T> {
    actual object Connect : SocketEvent<Unit>(), Mapper<Unit> by UnitMapper() {
        override val platformEvent: SocketIoEvent = SocketEventConnect

        override fun mapper(data: List<*>) = Unit
    }

    actual object Disconnect : SocketEvent<Unit>(), Mapper<Unit> by UnitMapper() {
        override val platformEvent: SocketIoEvent = SocketEventDisconnect
    }

    actual object Error : SocketEvent<Throwable>() {
        override val platformEvent: SocketIoEvent = SocketEventError

        override fun mapper(data: List<*>): Throwable {
            val message = data.first() as String
            return Exception(message)
        }
    }

    abstract val platformEvent: SocketIoEvent
}

actual sealed class SocketManagerEvent<T> : Mapper<T> {
    actual object Reconnect : SocketManagerEvent<Unit>(), Mapper<Unit> by UnitMapper() {
        override val platformManagerEvent: SocketIoManagerEvent = SocketEventReconnect
    }

    actual object ReconnectAttempt : SocketManagerEvent<Unit>(), Mapper<Unit> by UnitMapper() {
        override val platformManagerEvent: SocketIoManagerEvent = SocketEventReconnectAttempt
    }

    abstract val platformManagerEvent: SocketIoManagerEvent
}

private class UnitMapper : Mapper<Unit> {
    override fun mapper(data: List<*>) = Unit
}
