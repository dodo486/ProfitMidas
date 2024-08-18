package bulls.websocket.external;

import bulls.log.DefaultLogger;
import bulls.thread.GeneralCoreExecutors;
import bulls.thread.GeneralCoreThread;
import bulls.tool.util.GeneralCoreTimer;
import bulls.websocket.handler.WebSocketPacketHandler;
import bulls.websocket.session.ExternalWebsocketSession;
import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.queue.ExcerptAppender;
import net.openhft.chronicle.queue.ExcerptTailer;
import net.openhft.chronicle.queue.RollCycles;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueue;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueueBuilder;
import net.openhft.chronicle.wire.DocumentContext;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Executor;

/**
 * ExternalWebsocket에서 외부 요청을 전달받거나, Websocket을 통해 데이터를 보내고 싶을 때 OracleArena에서 사용.
 */
public enum ExternalWebsocketServerGateway {
    Instance;

    static final int HEARTBEAT_FAIL_TIME_IN_SECOND = 10;
    static final int HEARTBEAT_SEND_TIME_IN_SECOND = 5;
    static final int HEARTBEAT_CHECK_INTERVAL_IN_SECOND = 2;

    SingleChronicleQueue oaToWSQueue;
    SingleChronicleQueue wsToOAQueue;
    ExcerptAppender appender;
    ExcerptTailer tailer;
    WebSocketPacketHandler handler;
    GeneralCoreThread t_reader;
    GeneralCoreTimer timer;
    Executor exc;
    boolean isFinished = false;
    boolean isInitalized = false;
    LocalDateTime lastRecvTime = LocalDateTime.now();
    LocalDateTime lastSendTime = LocalDateTime.now();

    ExternalWebsocketServerGateway() {

    }

    public boolean isInitialized() {
        return isInitalized;
    }

    public void init(WebSocketPacketHandler handler) {
        isInitalized = true;
        oaToWSQueue = SingleChronicleQueueBuilder.binary("/tmp/oraclearena/oa_to_ws").rollCycle(RollCycles.DAILY).build();
        wsToOAQueue = SingleChronicleQueueBuilder.binary("/tmp/oraclearena/ws_to_oa").rollCycle(RollCycles.DAILY).build();
        appender = oaToWSQueue.acquireAppender();
        tailer = wsToOAQueue.createTailer().toEnd();
//        handler = new HedgeWSPacketHandler();
        this.handler = handler;
        t_reader = new GeneralCoreThread("WSToOASReader", this::doReadFromQueue);
        t_reader.start();
        timer = new GeneralCoreTimer("ExternalWebsocketServerGatewayTimer");
        timer.scheduleAtFixedRate(this::checkHeartbeat, 10000, HEARTBEAT_CHECK_INTERVAL_IN_SECOND * 1000);
        exc = GeneralCoreExecutors.newFixedThreadPool(1);
    }

    private void checkHeartbeat() {
        LocalDateTime currTime = LocalDateTime.now();
        LocalDateTime baseTimeForRecv = currTime.minusSeconds(HEARTBEAT_FAIL_TIME_IN_SECOND);
        LocalDateTime baseTimeForSend = currTime.minusSeconds(HEARTBEAT_SEND_TIME_IN_SECOND);
        //check recv hb
        if (baseTimeForRecv.isAfter(lastRecvTime)) {
            long sec = ChronoUnit.SECONDS.between(lastRecvTime, currTime);
            DefaultLogger.logger.error("ExternalWebsocketServer 로부터 {}초간 수신된 메시지가 없습니다.", sec);
        }
        //check send hb
        if (baseTimeForSend.isAfter(lastSendTime)) {
            //send Heartbeat
            sendMessage(0, "");
        }
    }

    public void close() {
        timer.cancel();
        oaToWSQueue.close();
        wsToOAQueue.close();
        isFinished = true;
    }

    public void waitTillFinish() {
        try {
            t_reader.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    void doReadFromQueue() {
        while (!isFinished) {
            try (DocumentContext dc = tailer.readingDocument()) {
                if (dc.isPresent()) {
                    lastRecvTime = LocalDateTime.now();
                    Bytes bytes = dc.wire().bytes();
                    int sessId = bytes.readInt();
                    String msg = bytes.readUtf8();
                    if (msg.isEmpty()) {
                        //hb from extWS
                        DefaultLogger.logger.info("HB from ExternalWebsocketServer received!");
                        continue;
                    }
                    onMessage(sessId, msg);
                } else {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    void onMessage(int sessId, String json) {
        exc.execute(() -> {
            String ret = handler.handle(ExternalWebsocketSession.getOrCreate(sessId), json);
            if (ret != null) {
                sendMessage(sessId, ret);
            }
        });
    }

    public void sendMessage(int sessId, String json) {
        if (!isInitalized)
            return;
        try (DocumentContext dc = appender.writingDocument()) {
            Bytes bytes = dc.wire().bytes();
            bytes.writeInt(sessId);
            bytes.writeUtf8(json);
        }
        lastSendTime = LocalDateTime.now();
    }

    public void broadcastMessage(String json) {
        if (!isInitalized)
            return;
        try (DocumentContext dc = appender.writingDocument()) {
            Bytes bytes = dc.wire().bytes();
            bytes.writeInt(0);
            bytes.writeUtf8(json);
        }
        lastSendTime = LocalDateTime.now();
    }
}
