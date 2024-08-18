package bulls.tcp.sync;

import bulls.dateTime.DateCenter;
import bulls.exception.NoBidAskDataException;
import bulls.log.DefaultLogger;
import bulls.staticData.PredefinedString;
import bulls.tcp.NBOnConnectHandler;
import bulls.tcp.NBPacketHandler;
import bulls.thread.GeneralCoreThread;
import bulls.tool.util.BufPool;
import bulls.tool.util.SimplePacketHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.Scanner;

public class NonBlockingTcpServer implements Runnable {

    public static int SENT_AT_ONCE = 100;
    public static int BYTE_LENGTH = 1000;

    protected ServerSocketChannel server;
    protected Selector selector;

    protected final String ip;
    protected final int port;
    protected final int selectionOps;
    public BufPool bytePool;

    private final NBPacketHandler onDataReceive;
    private final NBOnConnectHandler onConnectHandler;

    private final HashMap<String, Integer> byteToReadMap = new HashMap<>();
    private final HashMap<String, Boolean> isHeaderMap = new HashMap<>();
    //    private HashMap<String, byte[]> frontFragMap = new HashMap<>();
//    private int byteToRead = 0;
//    private byte[] frontFrag;
    private final HashMap<String, ByteBuffer> byteBufferMap = new HashMap<>();

    public NonBlockingTcpServer(String ip, int port, NBOnConnectHandler onConnectHandler, NBPacketHandler handler) {
        this.ip = ip;
        this.port = port;
        this.selectionOps = SelectionKey.OP_READ | SelectionKey.OP_WRITE;
        bytePool = new BufPool(200, BYTE_LENGTH); // pool size 20, buffer size 3000;
        this.onConnectHandler = onConnectHandler;
        this.onDataReceive = handler;
    }

    public int getConnectedCiriCount() {
//        return byteToReadMap.size();
        return byteBufferMap.size();
    }


    public void initServer() {
        try {
            server = ServerSocketChannel.open();
            server.configureBlocking(false);
            server.bind(new InetSocketAddress(ip, port));
            selector = Selector.open();

            server.register(selector, SelectionKey.OP_ACCEPT);

        } catch (IOException e) {
            e.printStackTrace();
//            DefaultLogger.logger.error("error found", e);
        }
    }

    public String getKey(SocketChannel channel) throws IOException {
        return channel.getRemoteAddress().toString();
    }

    public synchronized void acceptClient(SelectionKey key) {
        ServerSocketChannel server = (ServerSocketChannel) key.channel();
        SocketChannel sc;
        try {
            sc = server.accept();
            // accept 소켓채널 selector 에 등록
            sc.configureBlocking(false);
//            socketChannelList.add(sc);
            sc.socket().setSendBufferSize(1024 * 1024);
            DefaultLogger.logger.info("Set socket send buffer size to {}. result is {}", 1024 * 1024, sc.socket().getSendBufferSize());
            String clientKey = getKey(sc);
//            NBPeriodicUpdater.Instance.msgQ.put(clientKey, new LinkedBlockingQueue<>());
            System.out.println("Adding Client Key:" + clientKey);
//            byteToReadMap.put(clientKey, 0);
//            frontFragMap.put(clientKey, new byte[0]);
            byteBufferMap.put(clientKey, ByteBuffer.allocateDirect(1024 * 1024));
            isHeaderMap.put(clientKey, true);
            if (onConnectHandler != null)
                onConnectHandler.onConnect(sc);

            sc.register(selector, selectionOps);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    ////    public String onDataReceive(final SocketChannel sc) throws IOException;
//
    public void handlePacket(byte[] data, SocketChannel channel) throws NoBidAskDataException, IOException {
//        DefaultLogger.logger.debug("Perfect packet[{}]", new String(data));
        String response;
        if (onDataReceive != null)
            response = onDataReceive.handle(data);
        else
            response = new String(data);

        if (!response.equals(PredefinedString.NO_RESPONSE)) {
            SimplePacketHandler.sendPacketWithAsciiLengthHeader(channel, response);
        }
    }
//    public void parseAndHandle(SocketChannel channel, byte[] byteReceived, int offset, int remainingByteToRead) throws NoBidAskDataException, IOException {
//        if ( offset > byteReceived.length)
//            return;
//        if ( remainingByteToRead == 0)
//            return ;
//        if (remainingByteToRead < 4){
//            DefaultLogger.logger.error("패킷 사이즈를 결정하기 위한 byte를 모두 수신하지 못했습니다.");
//        }
//        byte[] lengthBytes = new byte[4];
//        System.arraycopy(byteReceived, offset, lengthBytes,0,4);
//        int lengthToRead = SimplePacketHandler.bytesToIntBigEndian(lengthBytes);
//        remainingByteToRead -= 4;
//        offset += 4;
//
//        String scKey = channel.getRemoteAddress().toString();
//        // 읽어야 할것 보다 남은 것이 적다면...
//        if( lengthToRead > remainingByteToRead){
//            //keep broken packet & return
//            byte[] frontFrag = new byte[remainingByteToRead];
//            System.arraycopy(byteReceived, offset, frontFrag,0,remainingByteToRead);
//            byteToReadMap.put(scKey, lengthToRead - remainingByteToRead);
//            frontFragMap.put(scKey, frontFrag);
//            return;
//        }
//
//        byte[] data = new byte[lengthToRead];
//        System.arraycopy(byteReceived, offset, data,0, lengthToRead);
//
////        DefaultLogger.logger.debug("Perfect packet[{}]", new String(data));
//        handlePacket(data, channel);
//
//        remainingByteToRead -= lengthToRead;
//        offset += lengthToRead;
//        parseAndHandle(channel, byteReceived, offset , remainingByteToRead);
//    }

    public SocketChannel read(SelectionKey key) {
        SocketChannel sc = (SocketChannel) key.channel();
        try {
            String scKey = getKey(sc);
            ByteBuffer buf = byteBufferMap.get(scKey);
            int nRead = sc.read(buf);
            if (nRead == -1) {
                throw new IOException(" read fail : nRead -1 from client socket channel");
            }
            //updateAndStart read
            buf.flip();
            while (true) {
//                DefaultLogger.logger.info("ByteBuffer={}", buf.toString());
                Boolean isHeader = isHeaderMap.get(scKey);

                int bytesInBuffer = buf.limit() - buf.position();
//                DefaultLogger.logger.info("bytesInBuffer={}",bytesInBuffer);
                if (isHeader) {
                    if (bytesInBuffer >= 4) {
                        byte[] lengthBytes = new byte[4];

                        buf.get(lengthBytes);
                        int length = SimplePacketHandler.bytesToIntBigEndian(lengthBytes);
                        byteToReadMap.put(scKey, length);
                        isHeaderMap.put(scKey, false);

//                        DefaultLogger.logger.info("헤더 바이트를 수신했습니다. length={}", length);
                    } else {
//                        DefaultLogger.logger.info("헤더 바이트가 부족합니다. Read를 종료합니다. length=", bytesInBuffer);
                        //더 이상 처리할 수 있는 데이터가 없으므로 버퍼 정리
                        if (bytesInBuffer == 0)
                            buf.clear();
                        else
                            buf.compact();

                        break;
                    }
                } else {
                    int length = byteToReadMap.get(scKey);
                    if (bytesInBuffer >= length) {
                        byte[] bodyBytes = new byte[length];

                        buf.get(bodyBytes);
//                        DefaultLogger.logger.info("바디 바이트를 수신했습니다. body={}", new String(bodyBytes));
                        handlePacket(bodyBytes, sc);
                        isHeaderMap.put(scKey, true);

                    } else {
//                        DefaultLogger.logger.info("바디 바이트가 부족합니다. Read를 종료합니다. length=", bytesInBuffer);
                        //더 이상 처리할 수 있는 데이터가 없으므로 버퍼 정리
                        if (bytesInBuffer == 0)
                            buf.clear();
                        else
                            buf.compact();

                        break;
                    }
                }

                bytesInBuffer = buf.limit() - buf.position();
//                DefaultLogger.logger.info("bytesInBuffer={} ByteBuffer={}", bytesInBuffer, buf.toString());
                if (bytesInBuffer == 0) {
                    //더 이상 read할 데이터가 없으므로 clear
                    buf.clear();
//                    DefaultLogger.logger.info("Nothing to read more... exit read()");
                    break;
                }
            }

//            // 읽은 만큼 byte array 에 일단 다 때려 박는다.
//            byte[] byteReceived = new byte[nRead];
//            buf.flip();
//            buf.get(byteReceived, 0, nRead); // length byte ignore
//
//            String scKey = sc.getRemoteAddress().toString();
//            int byteToRead = byteToReadMap.get(scKey);
//            int offset = 0;
//            // 읽어야 할게 있다면 읽는다.
//            if ( byteToRead > 0) {
//                // 이거 읽고도 더 읽어야함, 기존 frontFrag 업데이트 , byteToRead 차감 하고 리턴
//                if ( byteToRead > nRead) {
//                    byte[] frontFrag = frontFragMap.get(scKey);
//                    byte[] longerFrontPacket = new byte[frontFrag.length + nRead];
//                    System.arraycopy(frontFrag, 0, longerFrontPacket,0, frontFrag.length);
//                    System.arraycopy(byteReceived, 0, longerFrontPacket, frontFrag.length, nRead);
//                    byteToRead -= nRead;
//                    frontFragMap.put(scKey, longerFrontPacket);
//                    byteToReadMap.put(scKey, byteToRead);
//                    DefaultLogger.logger.debug("불완전 packet[{}]", new String(frontFrag));
//                    return sc; // 다 읽었으나 이번턴에는 완성된 패킷이 없으므로 리턴
//                } else { //미완성 패킷이 이번에 완성
//                    byte[] frontFrag = frontFragMap.get(scKey);
//                    byte[] fullPacket = new byte[frontFrag.length + byteToRead];
//                    System.arraycopy(frontFrag, 0, fullPacket,0, frontFrag.length);
//                    System.arraycopy(byteReceived, 0, fullPacket, frontFrag.length, byteToRead);
//
//                    offset += byteToRead;
//                    nRead -= byteToRead;
//                    DefaultLogger.logger.debug("Concatenated packet[{}]", new String(fullPacket));
//                    handlePacket(fullPacket, sc);
//                    byteToReadMap.put(scKey, 0);
//                    frontFragMap.put(scKey, new byte[0]);
//                }
//
//            }
//
//            parseAndHandle(sc, byteReceived,offset,  nRead);


        } catch (Exception e) {
            e.printStackTrace();
            onDisconnected(sc);
        }
        return sc;
    }


    public void broadcast(String msg) {
        for (SelectionKey key : selector.keys()) {
            if (!key.isValid() || !(key.channel() instanceof SocketChannel)) {
                continue;
            }

            SocketChannel channel = (SocketChannel) key.channel();
            try {
                SimplePacketHandler.sendPacketWithAsciiLengthHeader(channel, msg);
            } catch (IOException e) {
                e.printStackTrace();
                onDisconnected(channel);
            }
        }
//        for (SelectionKey key : selector.keys()) {
//            if (!key.isValid() || !(key.channel() instanceof SocketChannel)) {
//                continue;
//            }
//            SocketChannel channel = (SocketChannel) key.channel();
//            try {
//                SimplePacketHandler.sendPacketWithAsciiLengthHeader(channel, msg);
//            } catch (IOException e) {
//                e.printStackTrace();
//                onDisconnected(channel);
//            }
//        }

        if (byteToReadMap.size() > 0)
            DefaultLogger.logger.debug("Msg Dropped:{}", msg);
    }


    @Override
    public void run() {
        while (true) {
            try {
                selector.select();

                Iterator<SelectionKey> it = selector.selectedKeys().iterator();
                while (it.hasNext()) {
                    SelectionKey key = it.next();

                    it.remove();
                    if (key.isAcceptable()) {
                        // client 접속 시도
                        DefaultLogger.logger.debug("Client Acceptable!!!");
                        acceptClient(key);
                    }
                    if (key.isReadable()) {
                        // 기존 연결된 client 가 message 보낸 경우
//                        DefaultLogger.logger.debug("Client Readable!!!");
                        SocketChannel sc = read(key);
                    }
//                    if ( key.isWritable()) {
////                        DefaultLogger.logger.debug("Client Writable!!!");
//                        SocketChannel sc = write(key);
//                        sc.register(selector, SelectionKey.OP_READ);
//                    }
                    // 이미 처리한 이벤트. 삭제 요망

                }
            } catch (Exception e) {
                e.printStackTrace();
//                DefaultLogger.logger.error("error found", e);
            }
        }
    }

    //for test
    public void keepShoot(String packet) {
        Runnable r = () -> {
            Random rand = new Random();
            while (true) {
                int n = rand.nextInt(500);
                try {
                    Thread.sleep(n);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                broadcast("[" + DateCenter.Instance.getDateTime() + "][" + packet + "]");
            }
        };
        GeneralCoreThread t = new GeneralCoreThread(packet, r);
        t.start();
    }

    public void onDisconnected(SocketChannel channel) {
        try {
            String keyToRemove = channel.getRemoteAddress().toString();

            channel.close();
            byteToReadMap.remove(keyToRemove);
//            frontFragMap.remove(keyToRemove);
            byteBufferMap.remove(keyToRemove);
            isHeaderMap.remove(keyToRemove);
        } catch (IOException e1) {
            e1.printStackTrace();
        }


    }

    public static void main(String[] args) {
        NonBlockingTcpServer server = new NonBlockingTcpServer("localhost", 1351, null, null);
        server.initServer();

        GeneralCoreThread t = new GeneralCoreThread("NonBlockTCPServerForClient", server);
        t.start();

        Scanner sc = new Scanner(System.in);
        while (true) {
            String packet = sc.nextLine().trim();
            server.keepShoot(packet);
        }
    }
}
