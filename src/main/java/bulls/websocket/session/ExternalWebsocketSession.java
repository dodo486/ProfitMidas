package bulls.websocket.session;

import bulls.websocket.external.ExternalWebsocketServerGateway;

import java.util.concurrent.ConcurrentHashMap;

public class ExternalWebsocketSession implements WebsocketSession {
    int sessId;
    static ConcurrentHashMap<Integer, ExternalWebsocketSession> map = new ConcurrentHashMap<>();

    public static ExternalWebsocketSession getOrCreate(int sessId) {
        return map.computeIfAbsent(sessId, ExternalWebsocketSession::new);
    }

    public static ExternalWebsocketSession remove(int sessId) {
        return map.remove(sessId);
    }

    public ExternalWebsocketSession(int sessId) {
        this.sessId = sessId;
    }

    public int getSessId() {
        return sessId;
    }

    @Override
    public void sendMessage(String msg) {
        ExternalWebsocketServerGateway.Instance.sendMessage(sessId, msg);
    }
}
