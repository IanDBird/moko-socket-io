/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.socket

expect sealed class SocketEvent<T> {
    object Connect : SocketEvent<Unit>
    object Disconnect : SocketEvent<Unit>
    object Error : SocketEvent<Throwable>
}

expect sealed class SocketManagerEvent<T> {

    object Reconnect : SocketManagerEvent<Unit>

    object ReconnectAttempt : SocketManagerEvent<Unit>
}
