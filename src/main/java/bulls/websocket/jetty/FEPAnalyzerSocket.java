package bulls.websocket.jetty;


import bulls.websocket.WebSocketGlobalMessageBroadcaster;
import bulls.websocket.handler.FEPAnalyzerWSPacketHandler;
import bulls.websocket.session.JettyWebsocketSession;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;

public class FEPAnalyzerSocket extends WebSocketAdapter {
    JettyWebsocketSession jettySession = null;
    Session sess = null;
    FEPAnalyzerWSPacketHandler handler = new FEPAnalyzerWSPacketHandler();

    @Override
    public void onWebSocketConnect(Session sess) {
        this.sess = sess;
        super.onWebSocketConnect(sess);
        System.out.println("- Socket Connected: " + sess);
        jettySession = JettyWebsocketSession.getOrCreate(sess);
        WebSocketGlobalMessageBroadcaster.Instance.addSession(jettySession);
    }

    @Override
    public void onWebSocketText(String message) {
        super.onWebSocketText(message);
//        System.out.println("- Received Text message: " + message);
        String returnMsg = null;
        if (jettySession != null)
            returnMsg = handler.handle(jettySession, message);

        if (returnMsg != null)
            jettySession.sendMessage(returnMsg);
    }

    @Override
    public void onWebSocketClose(int statusCode, String reason) {
        super.onWebSocketClose(statusCode, reason);
        System.out.println("- Socket Closed: [" + statusCode + "] " + reason);
        WebSocketGlobalMessageBroadcaster.Instance.removeSession(jettySession);
        JettyWebsocketSession.remove(sess);
    }

    @Override
    public void onWebSocketError(Throwable cause) {
        super.onWebSocketError(cause);
        cause.printStackTrace(System.err);
        WebSocketGlobalMessageBroadcaster.Instance.removeSession(jettySession);
        JettyWebsocketSession.remove(sess);
    }
}