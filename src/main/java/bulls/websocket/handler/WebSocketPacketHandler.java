package bulls.websocket.handler;


import bulls.websocket.session.WebsocketSession;

public interface WebSocketPacketHandler {
    String handle(WebsocketSession sess, byte[] packetFromClient);

    String handle(WebsocketSession sess, String utfJSON);
}
