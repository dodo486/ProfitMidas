package bulls.websocket.asyncHandling;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import bulls.log.DefaultLogger;
import bulls.websocket.session.WebsocketSession;

import java.util.concurrent.ConcurrentHashMap;

public class AsyncResponse {
    private final WebsocketSession sess;
    private final ObjectNode inputNode;
    private final ObjectNode outputNode;
    private final ConcurrentHashMap<String, Object> objectMap;

    public AsyncResponse(WebsocketSession sess, ObjectNode inputNode) {
        this.sess = sess;
        this.inputNode = inputNode;
        this.outputNode = JsonNodeFactory.instance.objectNode();
        this.objectMap = new ConcurrentHashMap<>();
    }

    public ObjectNode getInputNode() {
        return inputNode;
    }

    public ObjectNode getOutputNode() {
        return outputNode;
    }

    public WebsocketSession getWebsocketSession() {
        return sess;
    }

    public <T> T get(String key, Class<T> clazz) {
        Object obj = objectMap.get(key);
        if (obj == null)
            return null;

        T tObj;
        try {
            tObj = clazz.cast(obj);
        } catch (ClassCastException e) {
            DefaultLogger.logger.error("key가 {}인 obj를 {} class로 변환할 수 없음. 원래 클래스는 {}", key, clazz.getSimpleName(), obj.getClass().getSimpleName());
            e.printStackTrace();
            return null;
        }

        return tObj;
    }

    public void put(String key, Object value) {
        objectMap.put(key, value);
    }

    public void sendMessage(String trCode, String action, String msgType, String msg) {
        ObjectNode root = JsonNodeFactory.instance.objectNode();
        root.put("trCode", trCode);
        root.put("action", action);
        root.put("msgType", msgType);
        root.put("msg", msg);
        sess.sendMessage(root.toString());
    }
}
