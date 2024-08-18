package bulls.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import bulls.json.DefaultMapper;
import bulls.json.JsonKey;
import bulls.json.JsonValue;
import bulls.staticData.TempConf;
import bulls.thread.GeneralCoreExecutors;
import bulls.websocket.external.ExternalWebsocketServerGateway;
import bulls.websocket.session.WebsocketSession;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;

/**
 * Hedge/LP가 생성할 수 있는 websocket 서버는 자신을 컨트롤 하기 위한 hedgeWS와 livesiseWS 두개인데 이 클래스는 hedgews로 메시지를 전송하기 위해 사용
 */
public enum WebSocketGlobalMessageBroadcaster {
    Instance;

    ConcurrentLinkedQueue<WebsocketSession> sessList = new ConcurrentLinkedQueue<>();
    Executor exc;

    final String serverId;

    WebSocketGlobalMessageBroadcaster() {
        exc = GeneralCoreExecutors.newFixedThreadPool(1);

        String serverIdStr = TempConf.SERVER_ID;
        if (serverIdStr == null || serverIdStr.length() == 0 || serverIdStr.equalsIgnoreCase("UNDEFINED")) {
            try {
                InetAddress IP = InetAddress.getLocalHost();
                serverIdStr = IP.getHostAddress();
            } catch (UnknownHostException e) {
                e.printStackTrace();
                serverIdStr = "UNKNOWN";
            }
        }

        serverId = serverIdStr;
    }

    public void addSession(WebsocketSession session) {
        sessList.add(session);
    }

    public void removeSession(WebsocketSession session) {
        sessList.remove(session);
    }

    public void broadcastJsonObject(String json) {
        exc.execute(() -> {
            for (WebsocketSession sess : sessList) {
                sess.sendMessage(json);
            }
            ExternalWebsocketServerGateway.Instance.broadcastMessage(json);
        });
    }

    public void broadcastLogMessage(String category, Object o) {
        exc.execute(() -> {
            String objectStr = null;
            try {
                objectStr = DefaultMapper.getMapper().writeValueAsString(o);
                ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
                node.put(JsonKey.TRCODE, JsonValue.TRCODE_LOGMSG);
                node.put("category", category);
                node.put("msg", objectStr);
                String packet = DefaultMapper.getMapper().writeValueAsString(node);
                broadcastLogMessage(category, packet);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        });
    }

    public void broadcastLogMessage(String category, String msg) {
        exc.execute(() -> {
            String json = null;
            try {

                if (category.equals("체결채널주문거부")) {
                    ObjectNode node = (ObjectNode) DefaultMapper.getMapper().readTree("{}");
                    node.put("serverID", serverId);
                    node.put(JsonKey.TRCODE, "ServerMsg");
                    node.put("msg", msg);
                    json = DefaultMapper.getMapper().writeValueAsString(node);
                } else {
                    ObjectNode node = (ObjectNode) DefaultMapper.getMapper().readTree("{}");
                    node.put("serverID", serverId);
                    node.put(JsonKey.TRCODE, category);
                    node.put("msg", msg);
                    json = DefaultMapper.getMapper().writeValueAsString(node);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (json != null) {
                boolean useExternalWS = TempConf.EXTERNAL_WS_FOR_AP_CONTROL_ENABLED;
                if (useExternalWS && ExternalWebsocketServerGateway.Instance.isInitialized())
                    ExternalWebsocketServerGateway.Instance.broadcastMessage(json);

                for (WebsocketSession sess : sessList)
                    sess.sendMessage(json);
            }
        });
    }
}
