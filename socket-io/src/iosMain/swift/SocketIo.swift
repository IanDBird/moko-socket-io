/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

import SocketIO

@objc
public enum SocketIoTransport: Int {
  case websocket
  case polling
  case undefined
}

@objc
public enum SocketEvent: Int {
  case connect
  case disconnect
  case error
}

@objc
public enum SocketManagerEvent: Int {
  case reconnect
  case reconnectAttempt
}

@objc
public class SocketIo: NSObject {
  private let socketManager: SocketManager
  private let socket: SocketIOClient
  
  @objc
  public init(
    endpoint: String,
    queryParams: [String: Any]?,
    transport: SocketIoTransport
  ) {
    var configuration: SocketIOClientConfiguration = [ .compress ]
    if let queryParams = queryParams {
      configuration.insert(.connectParams(queryParams))
    }
    
    switch transport {
    case .websocket:
      configuration.insert(.forceWebsockets(true))
    case .polling:
      configuration.insert(.forcePolling(true))
    case .undefined: do {}
    }
    
    socketManager = SocketManager(socketURL: URL(string: endpoint)!,
                                  config: configuration)
    socket = socketManager.defaultSocket
  }
  
  @objc
  public func connect() {
    socket.connect()
  }
  
  @objc
  public func disconnect() {
    socket.disconnect()
  }
  
  @objc
  public func isConnected() -> Bool {
    return socket.status == SocketIOStatus.connected
  }
  
  @objc
  public func on(event: String, action: @escaping (String) -> Void) {
    // FIXME сейчас получается что SocketIo десериализует строку в json (dictionary), а мы после этого сериализуем обратно в строку, чтобы на уровне общей логики мультиплатформенный json парсер спарсил данные (результат парсинга iOS и Android варианта socketio разный - приводить к общему виду проблемно, проще в json вернуть и в общем коде преобразовать)
    socket.on(event) { data, emitter in
      let jsonData = try! JSONSerialization.data(withJSONObject: data[0], options: .prettyPrinted)
      let jsonString = String(data: jsonData, encoding: .utf8)!
      let _ = action(jsonString)
    }
  }
  
  @objc
  public func on(socketEvent: SocketEvent, action: @escaping (Array<Any>) -> Void) {
    let clientEvent: SocketClientEvent
    switch socketEvent {
    case .connect:
      clientEvent = .connect
      break
    case .error:
      clientEvent = .error
      break
    case .disconnect:
      clientEvent = .disconnect
      break
    default:
      return
    }
    socket.on(clientEvent: clientEvent) { data, _ in
      action(data)
    }
  }
  
  @objc
  public func emit(event: String, data: Array<Any>) {
    var result = Array<SocketData>()
    for i in (0...(data.count - 1)) {
      let item = data[i]
      if let itemData = (item as? String)?.data(using: .utf8) {
        do {
          guard let itemObject = try JSONSerialization.jsonObject(with: itemData, options: []) as? SocketData else {
            print("itemObject didn't cast")
            break
          }
          result.append(itemObject)
        } catch {
          print(error.localizedDescription)
          break
        }
      } else {
        guard let itemObject = item as? SocketData else {
          print("itemObject didn't cast")
          break
        }
        result.append(itemObject)
      }
    }
    socket.emit(event, with: result, completion: nil)
  }
  
  @objc
  public func emit(event: String, string: String) {
    socket.emit(event, with: [string], completion: nil)
  }
}

private extension UUID {
  func add(to array: inout Array<UUID>) {
    array.append(self)
  }
}

private extension SocketIOClient {
  func off(ids: Array<UUID>) {
    for id in ids {
      off(id: id)
    }
  }
}
