package bulls.feed.listen;

import bulls.designTemplate.observer.ObserverStation;
import bulls.feed.abstraction.Feed;
import bulls.feed.abstraction.FeedLauncher;
import bulls.feed.current.enums.FeedTRCode;
import bulls.feed.test.KrxPollingFeedLogCenter;
import bulls.feed.udpInfo.FeedInfoCenter;
import bulls.feed.udpInfo.RawFeedInfoForSocket;
import bulls.log.DefaultLogger;
import bulls.server.ServerMessageSender;
import bulls.server.enums.ServerLocation;
import bulls.staticData.TempConf;
import bulls.thread.CustomAffinityThreadFactory;
import bulls.tool.util.BufPool;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.MembershipKey;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

public class NonBlockingThreadPerPortUdp implements FeedLauncher {

    public static int BYTE_LENGTH = 1600;

    static {
        bytePool = new BufPool(200, BYTE_LENGTH);
    }

    public static BufPool bytePool;

    private final ObserverStation<Feed> obStation;

    private final String feedIfIp;

    ServerLocation feedLocation;

    private boolean stopSignal = false;

    public NonBlockingThreadPerPortUdp(ServerLocation feedLocation, ObserverStation<Feed> obStation, String feedIfIp) throws IOException {
        this.feedLocation = feedLocation;
        this.obStation = obStation;
        this.feedIfIp = feedIfIp;
    }

    public void run(Set<RawFeedInfoForSocket> infoSet) {
        if (infoSet.size() == 0)
            return;
        RawFeedInfoForSocket longestPacket = infoSet.stream().max(Comparator.comparingInt(RawFeedInfoForSocket::getLength)).get();
        bytePool = new BufPool(200, longestPacket.getLength());
        infoSet.forEach(info -> {
            try {
                InetAddress group = InetAddress.getByName(info.getIp());
                String modifiedIp = feedIfIp;
                if (TempConf.SUB_FEED_INTERFACE_ENABLED) {
                    Set<String> subTrList = TempConf.getStringAsSet(TempConf.SUB_FEED_TR_LIST);
                    for (String s : info.trCodeList) {
                        if (subTrList.contains(s)) {
                            modifiedIp = TempConf.SUB_FEED_INTERFACE_IP;
                            DefaultLogger.logger.info("Modifying Feed Interface IP of {} as {}", s, modifiedIp);
                            break;
                        }
                    }
                }
                NetworkInterface ni = NetworkInterface.getByInetAddress(InetAddress.getByName(modifiedIp));
                DatagramChannel channel = DatagramChannel.open(StandardProtocolFamily.INET)
                        .setOption(StandardSocketOptions.SO_REUSEADDR, true)
                        .bind(new InetSocketAddress(info.getPort()))
                        .setOption(StandardSocketOptions.IP_MULTICAST_IF, ni);

                MembershipKey key = channel.join(group, ni);

                channel.configureBlocking(false);
                channel.socket().setReceiveBufferSize(8192 * 128);

                CustomAffinityThreadFactory f = new CustomAffinityThreadFactory("FEED_" + info.getPort());
                DefaultLogger.logger.info("FEED_{} trCode Info : {}", info.getPort(), info);
                Thread t = f.newThread(() -> listen(channel, key, info));

                t.start();

            } catch (IOException e) {
                // TODO Auto-generated catch block
                DefaultLogger.logger.error("error found", e);
            }

        });
    }

    public void listen(DatagramChannel channel, MembershipKey key, RawFeedInfoForSocket info) {
        // 서울
        if (info.getLocation() == ServerLocation.SEOUL) {
            while (true) {
                if (stopSignal) {
                    try {
                        channel.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    break;
                }

                if (key.isValid()) {
                    SocketAddress sa;
                    ByteBuffer buf = bytePool.acquire();

                    try {
                        sa = channel.receive(buf);
                        if (sa != null) {
                            byte[] byteReceived = new byte[buf.limit()];
                            buf.flip();
                            buf.get(byteReceived, 0, buf.limit());

                            FeedTRCode feedTRCode = FeedTRCode.matchTR(byteReceived);
                            if (!info.isValidTRCode(feedTRCode)) {
                                buf.rewind();
                                buf.limit(buf.capacity());
                                continue;
                            }

                            if (TempConf.LOG_POLLING_FEED)
                                KrxPollingFeedLogCenter.Instance.increaseCountIfPolling(feedIfIp, info, new String(byteReceived));

                            ConcreteFeed feed = new ConcreteFeed();
                            feed.of(byteReceived, info, feedTRCode);
                            obStation.notifyAll(feed.getFeed());

                            buf.rewind();
                            buf.limit(buf.capacity());
                        }
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        DefaultLogger.logger.error("error found", e);
                    } catch (Exception e) {
                        DefaultLogger.logger.error(e.toString());
                        ServerMessageSender.writeServerMessage(this.getClass(), "OracleArena", "피드처리오류", "[NonBlockingThreadPerPortUdp]\n", e);
                        throw e;
                    } finally {
                        bytePool.release(buf);
                    }
                }
            }
        } else { // 부산
            while (true) {
                if (stopSignal) {
                    try {
                        channel.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    break;
                }

                if (key.isValid()) {
                    SocketAddress sa;
                    ByteBuffer buf = bytePool.acquire();

                    try {
                        sa = channel.receive(buf);
                        if (sa != null) {
                            byte[] byteReceived = new byte[buf.limit()];
                            buf.flip();
                            buf.get(byteReceived, 0, buf.limit());

                            FeedTRCode feedTRCode = FeedTRCode.matchTRPusan(byteReceived);
                            if (!info.isValidTRCode(feedTRCode)) {
                                buf.rewind();
                                buf.limit(buf.capacity());
                                continue;
                            }

                            if (TempConf.LOG_POLLING_FEED)
                                KrxPollingFeedLogCenter.Instance.increaseCountIfPolling(feedIfIp, info, new String(byteReceived));

                            ConcreteFeed feed = new ConcreteFeed();
                            feed.of(byteReceived, info, feedTRCode);
                            obStation.notifyAll(feed.getFeed());

                            buf.rewind();
                            buf.limit(buf.capacity());
                        }
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        DefaultLogger.logger.error("error found", e);
                    } catch (Exception e) {
                        DefaultLogger.logger.error(e.toString());
                        ServerMessageSender.writeServerMessage(this.getClass(), "OracleArena", "피드처리오류", "[NonBlockingThreadPerPortUdp]\n", e);
                        throw e;
                    } finally {
                        bytePool.release(buf);
                    }
                }
            }
        }
    }

    @Override
    public void startListen(FeedTRCode[] trCodeList) {
        HashSet<FeedTRCode> hs = new HashSet<>();
        for (FeedTRCode feedTRCode : trCodeList) {
            if (feedTRCode.getDescription().getLocation() == feedLocation)
                hs.add(feedTRCode);
            else
                DefaultLogger.logger.error("현재 location={}, 수신 feed={}", feedTRCode.getDescription());
        }
        Set<RawFeedInfoForSocket> infoSet = FeedInfoCenter.getInstance(feedLocation).getConnectionInfo(hs);
        run(infoSet);
    }

    public void stop() {
        stopSignal = true;
    }

}
