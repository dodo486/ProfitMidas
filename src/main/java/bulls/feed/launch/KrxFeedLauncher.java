package bulls.feed.launch;

import bulls.designTemplate.observer.ObserverStation;
import bulls.exception.InvalidFeedConfigException;
import bulls.exception.InvalidLocationException;
import bulls.feed.abstraction.Feed;
import bulls.feed.current.enums.FeedTRCode;
import bulls.feed.current.enums.TRDescription;
import bulls.feed.listen.BlockingThreadPerPortUdp;
import bulls.feed.listen.ConcreteFeed;
import bulls.feed.listen.FeedProducer;
import bulls.feed.listen.NonBlockingThreadPerPortUdp;
import bulls.feed.udpInfo.FeedInfoCenter;
import bulls.feed.udpInfo.RawFeedInfoForSocket;
import bulls.log.DefaultLogger;
import bulls.server.enums.ServerLocation;
import com.lmax.disruptor.dsl.Disruptor;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class KrxFeedLauncher {

    public final ServerLocation location;
    final String feedIfIp;
    NonBlockingThreadPerPortUdp nb;
    BlockingThreadPerPortUdp b;

    Set<RawFeedInfoForSocket> forNB = new HashSet<>();
    Set<RawFeedInfoForSocket> forBlocking = new HashSet<>();

    Set<TRDescription> nonBlockingTrDescSet = new HashSet<>();
    Set<TRDescription> blockingTrDescSet = new HashSet<>();
    Set<TRDescription> excludeNonBlockingTrDescSet = new HashSet<>();
    Set<TRDescription> excludeBlockingTrDescSet = new HashSet<>();

    Set<FeedTRCode> nonBlockingFeedTRCodeSet = new HashSet<>(), blockingFeedTRCodeSet = new HashSet<>();

    public KrxFeedLauncher(ServerLocation loc, String ip,
                           List<String> blockingTrDescList, List<String> nonBlockingTrDescList,
                           ObserverStation<Feed> obStation,
                           Disruptor<ConcreteFeed> disruptor) throws IOException, InvalidFeedConfigException {
        location = loc;
        feedIfIp = ip;
        FeedProducer producer = new FeedProducer(disruptor.getRingBuffer(), feedIfIp);
        nb = new NonBlockingThreadPerPortUdp(location, obStation, ip);
        b = new BlockingThreadPerPortUdp(location, obStation, ip, producer);
        for (String descStr : nonBlockingTrDescList) {
            if (descStr.startsWith("-")) {
                String exclDescStr = descStr.trim().substring(1);
                TRDescription desc = TRDescription.getValue(exclDescStr);
                if (desc != null) {
                    excludeNonBlockingTrDescSet.add(desc);
                    continue;
                }

                String[] arr = exclDescStr.split("/");
                if (arr.length != 2) {
                    DefaultLogger.logger.error("잘못된 config : {}", exclDescStr);
                    throw new InvalidFeedConfigException(exclDescStr);
                }

                ServerLocation loca;
                try {
                    loca = ServerLocation.getValue(arr[0].trim());
                } catch (InvalidLocationException e) {
                    e.printStackTrace();
                    loca = ServerLocation.NA;
                }

                Set<TRDescription> set = TRDescription.getTrDescriptionList(loca, arr[1].trim());
                if (set.size() == 0) {
                    DefaultLogger.logger.error("잘못된 config : {}", exclDescStr);
                    throw new InvalidFeedConfigException(exclDescStr);
                }

                excludeNonBlockingTrDescSet.addAll(set);
            }
            TRDescription desc = TRDescription.getValue(descStr.trim());
            if (desc != null) {
                nonBlockingTrDescSet.add(desc);
                continue;
            }
            String[] arr = descStr.split("/");
            if (arr.length != 2) {
                DefaultLogger.logger.error("잘못된 config : {}", descStr);
                throw new InvalidFeedConfigException(descStr);
            }
            ServerLocation loca;
            try {
                loca = ServerLocation.getValue(arr[0].trim());
            } catch (InvalidLocationException e) {
                e.printStackTrace();
                loca = ServerLocation.NA;
            }
            Set<TRDescription> set = TRDescription.getTrDescriptionList(loca, arr[1].trim());
            if (set.size() == 0) {
                DefaultLogger.logger.error("잘못된 config : {}", descStr);
                throw new InvalidFeedConfigException(descStr);
            }
            nonBlockingTrDescSet.addAll(set);
        }
        for (String descStr : blockingTrDescList) {
            if (descStr.startsWith("-")) {
                String exclDescStr = descStr.trim().substring(1);
                TRDescription desc = TRDescription.getValue(exclDescStr);
                if (desc != null) {
                    excludeBlockingTrDescSet.add(desc);
                    continue;
                }

                String[] arr = exclDescStr.split("/");
                if (arr.length != 2) {
                    DefaultLogger.logger.error("잘못된 config : {}", exclDescStr);
                    throw new InvalidFeedConfigException(exclDescStr);
                }

                ServerLocation loca;
                try {
                    loca = ServerLocation.getValue(arr[0].trim());
                } catch (InvalidLocationException e) {
                    e.printStackTrace();
                    loca = ServerLocation.NA;
                }

                Set<TRDescription> set = TRDescription.getTrDescriptionList(loca, arr[1].trim());
                if (set.size() == 0) {
                    DefaultLogger.logger.error("잘못된 config : {}", exclDescStr);
                    throw new InvalidFeedConfigException(exclDescStr);
                }

                excludeBlockingTrDescSet.addAll(set);
                continue;
            }

            TRDescription desc = TRDescription.getValue(descStr.trim());
            if (desc != null) {
                blockingTrDescSet.add(desc);
                continue;
            }

            String[] arr = descStr.split("/");
            if (arr.length != 2) {
                DefaultLogger.logger.error("잘못된 config : {}", descStr);
                throw new InvalidFeedConfigException(descStr);
            }

            ServerLocation loca;
            try {
                loca = ServerLocation.getValue(arr[0].trim());
            } catch (InvalidLocationException e) {
                e.printStackTrace();
                loca = ServerLocation.NA;
            }

            Set<TRDescription> set = TRDescription.getTrDescriptionList(loca, arr[1].trim());
            if (set.size() == 0) {
                DefaultLogger.logger.error("잘못된 config : {}", descStr);
                throw new InvalidFeedConfigException(descStr);
            }

            blockingTrDescSet.addAll(set);
        }
    }

    public Set<RawFeedInfoForSocket> getBlockingRawFeedInfoForSocketSet() {
        return forBlocking;
    }

    public Set<RawFeedInfoForSocket> getNonBlockingRawFeedInfoForSocketSet() {
        return forNB;
    }

    public void init() {
        nonBlockingFeedTRCodeSet.clear();
        blockingFeedTRCodeSet.clear();
        forNB.clear();
        forBlocking.clear();
        //nonbocking 처리
        for (TRDescription trDescription : nonBlockingTrDescSet) {
            List<FeedTRCode> trList = FeedTRCode.getTRListFromTRName(trDescription);
            nonBlockingFeedTRCodeSet.addAll(trList);
        }
        for (TRDescription trDescription : excludeNonBlockingTrDescSet) {
            List<FeedTRCode> trList = FeedTRCode.getTRListFromTRName(trDescription);
            nonBlockingFeedTRCodeSet.removeAll(trList);
        }
        if (nonBlockingFeedTRCodeSet.size() > 0) {
            Collection<RawFeedInfoForSocket> infoForNB = FeedInfoCenter.getInstance(location).getConnectionInfo(nonBlockingFeedTRCodeSet);
            infoForNB.forEach(info -> forNB.add(info));
            Iterator<RawFeedInfoForSocket> it = forBlocking.iterator();
        }
        //blocking 처리
        for (TRDescription trDescription : blockingTrDescSet) {
            List<FeedTRCode> trList = FeedTRCode.getTRListFromTRName(trDescription);
            for (FeedTRCode feedTRCode : trList) {
                if (nonBlockingFeedTRCodeSet.contains(feedTRCode)) {
                    DefaultLogger.logger.info("NonBlocking 으로 수신할 FeedTrCode:{} 는 blocking에서 제외합니다.", feedTRCode);
                    continue;
                }
                blockingFeedTRCodeSet.addAll(trList);
            }
        }
        for (TRDescription trDescription : excludeBlockingTrDescSet) {
            List<FeedTRCode> trList = FeedTRCode.getTRListFromTRName(trDescription);
            blockingFeedTRCodeSet.removeAll(trList);
        }
        if (blockingFeedTRCodeSet.size() > 0) {
            Set<RawFeedInfoForSocket> infoForBlocking = FeedInfoCenter.getInstance(location).getConnectionInfo(blockingFeedTRCodeSet);
            infoForBlocking.stream()
                    .filter(info -> !forNB.contains(info)) // NB 에서 커버한 포트는 열지 않는다.
                    .forEach(info -> forBlocking.add(info));
        }
    }

    public Set<FeedTRCode> start() {
        Set<String> trCodeSet = new HashSet<>();

        forNB.stream().forEach(info -> {
            DefaultLogger.logger.debug("Dedicated NonBlocking Feed start with {}", info);
            trCodeSet.addAll(info.trCodeList);
        });

        forBlocking.stream().forEach(info -> {
            DefaultLogger.logger.debug("Blocking Feed start with {}", info);
            trCodeSet.addAll(info.trCodeList);
        });

        nb.run(forNB);
        b.run(forBlocking);

        return trCodeSet.stream().map(FeedTRCode::valueOf).collect(Collectors.toSet());
    }

    public void stop() {
        nb.stop();
        b.stop();
    }

    public void print() {
        StringBuilder sb = new StringBuilder();
        sb.append("####################################################\n");
        sb.append("## " + getKey());
        sb.append("\n### Blocking TrDesc\n* TrDesc:\n");
        sb.append(blockingTrDescSet.stream().map(s -> s.toString()).collect(Collectors.joining(",")));
        sb.append("\n* Exclude :\n");
        sb.append(excludeBlockingTrDescSet.stream().map(s -> s.toString()).collect(Collectors.joining(",")));
        sb.append("\n* RawFeedInfo:\n");
        TreeSet<RawFeedInfoForSocket> tsBlocking = new TreeSet<>(Comparator.comparing(e -> (e.getIp() + " " + e.getPort())));
        tsBlocking.addAll(forBlocking);
        for (RawFeedInfoForSocket soc : tsBlocking) {
            sb.append(soc.toString());
            sb.append("\n");
        }
        sb.append("### NonBlocking TrDesc\n* TrDesc:\n");
        sb.append(nonBlockingTrDescSet.stream().map(s -> s.toString()).collect(Collectors.joining(",")));
        sb.append("\n* Exclude :\n");
        sb.append(excludeNonBlockingTrDescSet.stream().map(s -> s.toString()).collect(Collectors.joining(",")));
        sb.append("\n* RawFeedInfo:\n");
        TreeSet<RawFeedInfoForSocket> tsNonBlocking = new TreeSet<>(Comparator.comparing(e -> (e.getIp() + " " + e.getPort())));
        tsNonBlocking.addAll(forNB);
        for (RawFeedInfoForSocket soc : tsNonBlocking) {
            sb.append(soc.toString());
            sb.append("\n");
        }
        sb.append("####################################################\n");
        DefaultLogger.logger.info("\n{}", sb);
    }

    /**
     * 사용자가 수신하겠다고 한 FeedTrCode 리스트
     *
     * @return 사용자가 수신하겠다고 한 FeedTrCode 리스트
     */
    public Set<FeedTRCode> getMonitoringFeedTrCodeSet() {
        HashSet<FeedTRCode> set = new HashSet<>();
        set.addAll(nonBlockingFeedTRCodeSet);
        set.addAll(blockingFeedTRCodeSet);
        return set;
    }

    /**
     * 사용자가 수신하겠다고 한 FeedTrCode를 수신하기 위해 오픈한 멀티캐스트 ip/port에서 들어오는 모든 FeedTrCode
     *
     * @return 사용자가 수신하겠다고 한 FeedTrCode를 수신하기 위해 오픈한 멀티캐스트 ip/port에서 들어오는 모든 FeedTrCode
     */
    public Set<FeedTRCode> getListeningFeedTrCodeSet() {
        HashSet<FeedTRCode> set = new HashSet<>();
        forNB.forEach(t -> {
            for (String s : t.trCodeList) {
                if (location == ServerLocation.SEOUL)
                    set.add(FeedTRCode.matchTR(s));
                else if (location == ServerLocation.PUSAN)
                    set.add(FeedTRCode.matchTRPusan(s));

            }
        });
        forBlocking.forEach(t -> {
            for (String s : t.trCodeList) {
                if (location == ServerLocation.SEOUL)
                    set.add(FeedTRCode.matchTR(s));
                else if (location == ServerLocation.PUSAN)
                    set.add(FeedTRCode.matchTRPusan(s));
            }
        });
        return set;
    }

    public String getKey() {
        return generateKey(location, feedIfIp);
    }

    public static String generateKey(ServerLocation location, String interfaceIp) {
        return "KRX_" + location + "_" + interfaceIp;
    }

    public String getFeedIfIp() {
        return feedIfIp;
    }

    public ServerLocation getFeedLocation() {
        return location;
    }

    public String getFeedType() {
        return "KRX";
    }
}
