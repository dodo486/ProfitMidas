package bulls.feed.launch;

import bulls.designTemplate.observer.ObserverStation;
import bulls.exception.InvalidFeedConfigException;
import bulls.exception.InvalidLocationException;
import bulls.feed.abstraction.Feed;
import bulls.feed.current.enums.FeedTRCode;
import bulls.feed.listen.ConcreteFeed;
import bulls.feed.test.KrxPollingFeedLogCenter;
import bulls.feed.udpInfo.RawFeedInfoForSocket;
import bulls.log.DefaultLogger;
import bulls.server.ServerMessageSender;
import bulls.server.enums.ServerLocation;
import bulls.staticData.TempConf;
import bulls.thread.CustomAffinityThreadFactory;
import bulls.thread.GeneralCoreThreadFactory;
import bulls.tool.conf.KrxConfiguration;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.BusySpinWaitStrategy;
import com.lmax.disruptor.WorkHandler;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import net.openhft.affinity.AffinityStrategies;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ThreadFactory;

public enum FeedLaunchCenter {
    Instance;
    Disruptor<ConcreteFeed> disruptor;
    final HashMap<String, KrxFeedLauncher> map;
    ObserverStation<Feed> obStation;
    boolean isInitialized = false;

    FeedLaunchCenter() {
        map = new HashMap<>();
    }

    /**
     * example
     * <p>
     * DECLARE_STRUCT=feed
     * feed.0.type=KRX
     * feed.0.location=SEOUL
     * feed.0.interfaceIp=192.168.21.87
     * feed.0.blockintTrList=서울/시세,
     * feed.0.nonBlockintTrList=K200선물
     * <p>
     * feed.1.type=KRX
     * feed.1.location=PUSAN
     * feed.1.interfaceIp=192.168.21.87
     * feed.1.blockintTrList=
     * feed.1.nonBlockintTrList=K200선물
     * <p>
     * feed.2.type=KRX
     * feed.2.location=SEOUL
     * feed.2.interfaceIp=192.168.21.87
     * feed.2.blockintTrList=코스닥150
     * feed.2.nonBlockintTrList=
     */

    public void init(KrxConfiguration conf, ObserverStation<Feed> obStation) throws IOException, InvalidFeedConfigException {
        if (isInitialized) {
            DefaultLogger.logger.error("Can not init multiple times");
            return;
        }
        isInitialized = true;
        this.obStation = obStation;
        //init worker with disruptor
        int workerSize = TempConf.RAW_FEED_HANDLER_CONSUMER_SIZE;
//        int workerSize = conf.getInteger("feedPoolSize", 5);
        if (workerSize == 0) {
            DefaultLogger.logger.error("workerSize는 0보다 커야합니다. 강제로 5개로 설정합니다.");
            workerSize = 5;
        }
        int bufferSize = 1024;
        if (TempConf.DISRUPTOR_BLOCKING) {
            ThreadFactory factory = new GeneralCoreThreadFactory("BlockingRawFeedHandler");
            disruptor = new Disruptor<>(ConcreteFeed::new, bufferSize, factory, ProducerType.MULTI, new BlockingWaitStrategy());
        } else {
            ThreadFactory factory = new CustomAffinityThreadFactory("RawFeedHandler", AffinityStrategies.ANY);
            disruptor = new Disruptor<>(ConcreteFeed::new, bufferSize, factory, ProducerType.MULTI, new BusySpinWaitStrategy());
        }
        WorkHandler[] workHandlers = new WorkHandler[workerSize];
        Arrays.fill(workHandlers, (WorkHandler<ConcreteFeed>) concreteFeed -> {
            try {
                obStation.notifyAll(concreteFeed.getFeed());
            } catch (Exception e) {
                DefaultLogger.logger.error(e.toString());
                ServerMessageSender.writeServerMessage(this.getClass(), "OracleArena", "피드처리오류", "[CustomFeedLauncher]", e);
                throw e;
            }
        });
        disruptor.handleEventsWithWorkerPool(workHandlers);
        disruptor.start();
        //process conf
        conf.parseStructure("feed");
        int size = conf.getStructSize("feed");

        for (int i = 0; i < size; i++) {
            String typeStr = conf.getStructString("feed", i, "type");
            ServerLocation loc = null;
            try {
                loc = ServerLocation.getValue(conf.getStructString("feed", i, "location"));
            } catch (InvalidLocationException e) {
                e.printStackTrace();
                throw new InvalidFeedConfigException("잘못된 Location : " + conf.getStructString("feed", i, "location"));
            }
            String ip = conf.getStructString("feed", i, "interfaceIp");
            List<String> blockingTrDescList = conf.getStructStringList("feed", i, "blockingTrList", ",");
            List<String> nonBlockingTrDescList = conf.getStructStringList("feed", i, "nonBlockingTrList", ",");

            if (typeStr.equals("KRX")) {
                KrxFeedLauncher l = new KrxFeedLauncher(loc, ip, blockingTrDescList, nonBlockingTrDescList, obStation, disruptor);
                l.init();
                map.put(l.getKey(), l);
            }
        }

        for (KrxFeedLauncher value : map.values()) {
            value.print();
        }
        //check 중복
        for (KrxFeedLauncher l1 : map.values()) {
            for (FeedTRCode ftr1 : l1.getListeningFeedTrCodeSet()) {
                for (KrxFeedLauncher l2 : map.values()) {
                    if (l2 == l1)
                        continue;
                    for (FeedTRCode ftr2 : l2.getListeningFeedTrCodeSet()) {
                        if (ftr1.getTrCodeStr().equals(ftr2.getTrCodeStr())) {
                            throw new InvalidFeedConfigException("중복된 FeedTrCode " + l1.getKey() + " " + l2.getKey());
                        }
                    }
                }
            }
        }
        //polling feed logging
        if (TempConf.LOG_POLLING_FEED) {
            map.values().forEach(v -> {
                if (v.getFeedType().equals("KRX")) {
                    KrxPollingFeedLogCenter.Instance.addInterface(v.getFeedIfIp());
                    for (RawFeedInfoForSocket soc : v.getBlockingRawFeedInfoForSocketSet()) {
                        KrxPollingFeedLogCenter.Instance.initCount(v.getFeedIfIp(), soc);
                    }
                    for (RawFeedInfoForSocket soc : v.getNonBlockingRawFeedInfoForSocketSet()) {
                        KrxPollingFeedLogCenter.Instance.initCount(v.getFeedIfIp(), soc);
                    }
                }
            });
        }
    }

    public void start() {
        for (KrxFeedLauncher l : map.values()) {
            l.start();
        }
    }

    public void stop() {
        for (KrxFeedLauncher l : map.values()) {
            l.stop();
        }
    }

    public Set<FeedTRCode> getMonitoringFeedTrCodeSet() {
        HashSet<FeedTRCode> set = new HashSet<>();
        for (KrxFeedLauncher value : map.values()) {
            set.addAll(value.getMonitoringFeedTrCodeSet());
        }
        return set;
    }

    public Set<FeedTRCode> getListeningFeedTrCodeSet() {
        HashSet<FeedTRCode> set = new HashSet<>();
        for (KrxFeedLauncher value : map.values()) {
            set.addAll(value.getListeningFeedTrCodeSet());
        }
        return set;
    }

    public Collection<KrxFeedLauncher> getLauncherList() {
        return map.values();
    }
}
