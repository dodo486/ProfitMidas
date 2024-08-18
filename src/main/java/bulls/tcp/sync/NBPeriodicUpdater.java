package bulls.tcp.sync;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import bulls.db.mongodb.DocumentConvertible;
import bulls.designTemplate.JsonConvertible;
import bulls.hephaestus.HephaLogType;
import bulls.hephaestus.document.ServerMsgDoc;
import bulls.json.DefaultMapper;
import bulls.json.JsonKey;
import bulls.json.JsonValue;
import bulls.log.DefaultLogger;
import bulls.staticData.TempConf;
import bulls.tcp.ClientContents;
import bulls.thread.GeneralCoreThread;
import bulls.thread.GeneralCoreTimeoutThreadPoolExecutor;
import bulls.tool.util.GeneralCoreTimer;
import bulls.tool.util.SimplePacketHandler;
import org.bson.Document;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

public enum NBPeriodicUpdater {
    Instance;

    private static final ExecutorService packetMaker =
            new GeneralCoreTimeoutThreadPoolExecutor(1, 3, 0L, TimeUnit.MILLISECONDS, 2, TimeUnit.SECONDS, "packet_maker_for_ciri");
    private static final ConcurrentHashMap<String, Boolean> emergencyMap = new ConcurrentHashMap<>();

    private final List<ClientContents> updateContents;
    private final GeneralCoreThread ciri;

    private NonBlockingTcpServer server;

    public Queue<String> queue = new LinkedBlockingDeque<>();

    public void addGlobalMsg(String message) {
        if (server != null)
            server.broadcast(message);
    }

    public void init(NonBlockingTcpServer server) {
        this.server = server;
        ciri.start();
    }

    public void onConnect(SocketChannel sc) {
        updateContents.forEach(cc -> {
            List<?> list = cc.getInitialUpdateContents();
            for (Object o : list) {
                try {
                    byte[] contents = DefaultMapper.getMapper().writeValueAsBytes(o);
                    ObjectNode node = (ObjectNode) DefaultMapper.getMapper().readTree(contents);
                    node.put(JsonKey.TRCODE, cc.getTrCode());
                    String packet = DefaultMapper.getMapper().writeValueAsString(node);
                    SimplePacketHandler.sendPacketWithAsciiLengthHeader(sc, packet);
                } catch (IOException e) {
                    DefaultLogger.logger.error("error found", e);
                }
            }
        });
    }


    NBPeriodicUpdater() {
        updateContents = new ArrayList<>();
        // 등록된 ClientNotifier 컨텐츠를 n초에 한번씩 업뎃 해준다
        GeneralCoreTimer t = new GeneralCoreTimer("NB_Client_Contents_checker");

        Runnable contentsToQueue = () -> {
            if (server == null)
                return;

            if (server.getConnectedCiriCount() == 0)
                return;

            for (ClientContents clientContents : updateContents) {
                List<?> contentsList = clientContents.getPeriodicUpdateContents();
                for (Object o : contentsList) {
                    try {
                        byte[] contents = DefaultMapper.getMapper().writeValueAsBytes(o);
                        ObjectNode node = (ObjectNode) DefaultMapper.getMapper().readTree(contents);
                        node.put(JsonKey.TRCODE, clientContents.getTrCode());
                        String packet = DefaultMapper.getMapper().writeValueAsString(node);
                        queue.add(packet);
                    } catch (IOException e) {
                        DefaultLogger.logger.error("error found", e);
                    }
                }
            }
        };

        int clientNotifyInterval = TempConf.CLIENT_NOTIFY_INTERVAL;
        t.scheduleAtFixedRate(contentsToQueue, clientNotifyInterval, clientNotifyInterval);

        Runnable sender;
        sender = () -> {
            while (true) {
                String msg = queue.poll();
                if (msg == null)
                    continue;

                synchronized (server.selector.keys()) {
                    for (SelectionKey key : server.selector.keys()) {
                        if (!key.isValid() || !(key.channel() instanceof SocketChannel)) {
                            continue;
                        }

                        SocketChannel channel = (SocketChannel) key.channel();
                        try {
                            SimplePacketHandler.sendPacketWithAsciiLengthHeader(channel, msg);
                        } catch (IOException e) {
                            DefaultLogger.logger.error("error found", e);
                            server.onDisconnected(channel);
                        }
                    }
                }

                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        ciri = new GeneralCoreThread("WriteToCiri", sender);
    }

    public void addPeriodicUpdateContents(ClientContents clientContents) {
        if (clientContents != null)
            updateContents.add(clientContents);
        else {
            ServerMsgDoc.now(HephaLogType.운영장애, "serverError", "Ciri 클라이언트 컨텐츠 Null").fire();
        }
    }

    public void notifyLazy(String jsonPacket) {
        packetMaker.execute(() -> addGlobalMsg(jsonPacket));
    }

    public void notifyLazyTo(SocketChannel sc, String trCode, String key, String value) {
        Runnable lazy = () -> {
            try {
                ObjectNode node = (ObjectNode) DefaultMapper.getMapper().readTree("{}");
                node.put(JsonKey.TRCODE, trCode);
                node.put(key, value);
                String packet = DefaultMapper.getMapper().writeValueAsString(node);
                SimplePacketHandler.sendPacketWithAsciiLengthHeader(sc, packet);
            } catch (IOException e) {
                DefaultLogger.logger.error("error found", e);
            }
        };

        packetMaker.execute(lazy);
    }

    public void notifyLazy(String trCode, String key, String value) {
        Runnable lazy = () -> {
            try {
                ObjectNode node = JsonNodeFactory.instance.objectNode();
                node.put(JsonKey.TRCODE, trCode);
                node.put(key, value);
                String packet = DefaultMapper.getMapper().writeValueAsString(node);
                addGlobalMsg(packet);
            } catch (IOException e) {
                DefaultLogger.logger.error("error found", e);
            }
        };

        packetMaker.execute(lazy);
    }

    // 새로 보낼테니 이전 데이터 지워라
    public void notifyReset(String trToReset) {
        notifyLazy(JsonValue.TRCODE_RESET, "trToReset", trToReset);
    }

    private boolean isPausedCategory(String category) {
        if (!emergencyMap.containsKey(category))
            return true;

        return !emergencyMap.get(category);
    }

    public void notifyEmergency(String category, Object o) {
        if (isPausedCategory(category))
            return;

        try {
            byte[] contents = DefaultMapper.getMapper().writeValueAsBytes(o);
            ObjectNode node = (ObjectNode) DefaultMapper.getMapper().readTree(contents);
            node.put("category", category);
            node.put(JsonKey.TRCODE, JsonValue.TRCODE_EMERGENCY);

            notifyEmergency(category, node);
        } catch (IOException e) {
            DefaultLogger.logger.error("error found", e);
        }
    }

    public void notifyEmergency(String category, String msg) {
        if (isPausedCategory(category))
            return;

        ObjectNode node = JsonNodeFactory.instance.objectNode();
        node.put(JsonKey.TRCODE, JsonValue.TRCODE_EMERGENCY);
        node.put("category", category);
        node.put("msg", msg);

        notifyEmergency(category, node);
    }

    private void notifyEmergency(String category, ObjectNode node) {
        Runnable lazy = () -> {
            try {
                String packet = DefaultMapper.getMapper().writeValueAsString(node);
                addGlobalMsg(packet);

                emergencyMap.put(category, false);
                GeneralCoreTimer t = new GeneralCoreTimer("DuplicatingProtection");
                t.schedule(() -> emergencyMap.put(category, true), 10000);
            } catch (JsonProcessingException e) {
                DefaultLogger.logger.error("error found", e);
            }
        };

        packetMaker.execute(lazy);
    }

    public void notifyObjectImmediately(String trCode, Object o) {
        try {
            String packetString;

            if (o instanceof DocumentConvertible) {
                Document doc = ((DocumentConvertible) o).getDataDocument();
                doc.put(JsonKey.TRCODE, trCode);
                packetString = doc.toJson();
            } else {
                ObjectNode node;
                if (o instanceof JsonConvertible) {
                    node = ((JsonConvertible) o).toObjectNode();
                } else {
                    byte[] contents = DefaultMapper.getMapper().writeValueAsBytes(o);
                    node = (ObjectNode) DefaultMapper.getMapper().readTree(contents);
                }
                node.put(JsonKey.TRCODE, trCode);
                packetString = DefaultMapper.getMapper().writeValueAsString(node);
            }

            addGlobalMsg(packetString);
        } catch (IOException e) {
            DefaultLogger.logger.error("error found", e);
        }
    }

    public void notifyObjectLazy(String trCode, Object o) {
        packetMaker.execute(() -> notifyObjectImmediately(trCode, o));
    }

    public void sendLogMsg(String category, String msg) {
        Runnable lazy = () -> {
            try {
                ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
                node.put(JsonKey.TRCODE, JsonValue.TRCODE_LOGMSG);
                node.put("category", category);
                node.put("msg", msg);
                String packet = DefaultMapper.getMapper().writeValueAsString(node);
                addGlobalMsg(packet);
            } catch (JsonProcessingException e) {
                DefaultLogger.logger.error("error found", e);
            }
        };

        packetMaker.execute(lazy);
    }

    public void sendLogMsg(String category, Object o) {
        try {
            String objectStr = DefaultMapper.getMapper().writeValueAsString(o);
            sendLogMsg(category, objectStr);
        } catch (JsonProcessingException e) {
            DefaultLogger.logger.error("error found", e);
        }
    }

    public void sendLogMsg(String msg) {
        sendLogMsg("일반", msg);
    }
}
