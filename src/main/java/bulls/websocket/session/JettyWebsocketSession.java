package bulls.websocket.session;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WriteCallback;

import java.util.concurrent.ConcurrentHashMap;

public class JettyWebsocketSession implements WebsocketSession {
    Session sess;
    WriteCallback cb;
    static ConcurrentHashMap<Session, JettyWebsocketSession> map = new ConcurrentHashMap<>();

    public static JettyWebsocketSession getOrCreate(Session sess) {
        return map.computeIfAbsent(sess, (k) -> new JettyWebsocketSession(sess));
    }

    public static JettyWebsocketSession get(Session sess) {
        return map.get(sess);
    }

    public static JettyWebsocketSession remove(Session sess) {
        return map.remove(sess);
    }

    JettyWebsocketSession(Session sess) {
        this.sess = sess;
        this.cb = new WriteCallback() {
            @Override
            public void writeFailed(Throwable throwable) {
                throwable.printStackTrace();
            }

            @Override
            public void writeSuccess() {

            }
        };
    }

    @Override
    public void sendMessage(String json) {
        sess.getRemote().sendString(json, cb);
    }
}
