package bulls.websocket;

import com.fasterxml.jackson.databind.node.ObjectNode;
import bulls.websocket.asyncHandling.AsyncFinish;
import bulls.websocket.asyncHandling.AsyncResponse;
import bulls.websocket.session.WebsocketSession;

public class WebSocketSessionCallbackMessageSender implements AsyncFinish {
    private void notify(AsyncResponse obj) {
        WebsocketSession sess = obj.getWebsocketSession();
        ObjectNode node = obj.getOutputNode();
        sess.sendMessage(node.toString());
    }

    @Override
    public void finish(AsyncResponse obj) {
        notify(obj);
    }
}
