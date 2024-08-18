package bulls.feed.listen;


import bulls.designTemplate.observer.ObserverStation;
import bulls.feed.abstraction.Feed;
import bulls.feed.abstraction.FeedLauncher;
import bulls.feed.current.enums.FeedTRCode;
import bulls.feed.udpInfo.FeedInfoCenter;
import bulls.feed.udpInfo.RawFeedInfoForSocket;
import bulls.log.DefaultLogger;
import bulls.server.enums.ServerLocation;
import bulls.staticData.TempConf;
import bulls.thread.GeneralCoreThread;

import java.io.IOException;
import java.net.*;
import java.util.HashSet;
import java.util.Set;

public class BlockingThreadPerPortUdp implements FeedLauncher {
    private final ServerLocation feedLocation;
    private final ObserverStation<Feed> obStation;
    private final String feedIfIp; // 시세 수신 인터페이스 ip
    //    private HashSet<String> trCodeStrSet;
    private final FeedProducer producer;

    private boolean stopSignal = false;


    public BlockingThreadPerPortUdp(ServerLocation feedLocation, ObserverStation<Feed> obStation, String feedIfIp, FeedProducer feedProducer) throws UnknownHostException, SocketException {
        this.feedLocation = feedLocation;
        this.obStation = obStation;
        this.producer = feedProducer;
        this.feedIfIp = feedIfIp;
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
        Set<RawFeedInfoForSocket> infoList = FeedInfoCenter.getInstance(feedLocation).getConnectionInfo(hs);
        run(infoList);
    }

    public void run(Set<RawFeedInfoForSocket> infoSet) {
        infoSet.forEach((info) -> {
            try {
                MulticastSocket soc = new MulticastSocket(info.getPort());
                InetAddress group = InetAddress.getByName(info.getIp());
                String modifiedIp = feedIfIp;
                if (TempConf.SUB_FEED_INTERFACE_ENABLED) {
                    Set<String> subTrList = TempConf.getStringAsSet(TempConf.SUB_FEED_TR_LIST);
                    for (String s : info.trCodeList) {
                        if (subTrList.contains(s)) {
                            modifiedIp = TempConf.SUB_FEED_INTERFACE_IP;
                            DefaultLogger.logger.info("Modifying Feed Interface IP of {} {} {} as {}", s, FeedTRCode.valueOf(s).getParser().getName(), FeedTRCode.valueOf(s).getDescription(), modifiedIp);
                            break;
                        }
                    }
                }
                NetworkInterface ethMarketData = NetworkInterface.getByInetAddress(InetAddress.getByName(modifiedIp));
                soc.joinGroup(new InetSocketAddress(group, info.getPort()), ethMarketData);
                Runnable dedicatedFeedReader = () -> listen(soc, info);

                GeneralCoreThread t = new GeneralCoreThread("FEED_" + info.getPort(), dedicatedFeedReader);

                t.start();

            } catch (IOException e) {
                DefaultLogger.logger.error("error found", e);
            }

        });

    }

    int dropCnt = 0;

    public void listen(MulticastSocket soc, RawFeedInfoForSocket info) {
        while (true) {
            if (stopSignal) {
                soc.close();
                break;
            }

            try {
                int packetLength = info.getLength();
                DatagramPacket packet = new DatagramPacket(new byte[packetLength], packetLength);
                soc.receive(packet);
                byte[] buf = packet.getData();
//                String trCode = new String(buf,0,5);
//                if(trCodeStrSet.contains(trCode))
                producer.onData(buf, info);
//                else
//                    DefaultLogger.logger.trace("[Blocking] Drop packet {} : {}",++dropCnt,trCode);

            } catch (IOException e) {
                DefaultLogger.logger.error("error found", e);
            }
        }
    }

    public void stop() {
        stopSignal = true;
    }
}

