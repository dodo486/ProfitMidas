package bulls.feed.udpInfo;

import bulls.datastructure.Pair;
import bulls.feed.current.enums.FeedTRCode;
import bulls.log.DefaultLogger;
import bulls.server.enums.ServerLocation;
import bulls.staticData.TempConf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class FeedInfoCenter {

    public Logger logger = LoggerFactory.getLogger(FeedInfoCenter.class);

    private final ServerLocation location;

    private final HashMap<String, TrInfo> trMap = new HashMap<>();

    private FeedInfoCenter(ServerLocation location) {
        this.location = location;
        InputStream is;
        if (location == ServerLocation.SEOUL)
            is = FeedInfoCenter.class.getResourceAsStream("/feedIpPortInfo/" + TempConf.TRINFOLIST_SEOUL_FILENAME);
        else
            is = FeedInfoCenter.class.getResourceAsStream("/feedIpPortInfo/" + TempConf.TRINFOLIST_PUSAN_FILENAME);


        if (is == null)
            System.out.println("Failed to read TrInfoList");


        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        String line;
        try {
            while ((line = br.readLine()) != null) {

                String[] r = line.split(" ");

                String trCode = r[0];
                String ip = r[1];
                int port = Integer.parseInt(r[2]);
                int length = Integer.parseInt(r[3]);
                String description = r[4];

                TrInfo info = trMap.get(trCode);
                if (info == null) {
                    info = new TrInfo(trCode, length);
                    info.addExclusiveIpPort(ip, port);
                    info.setDescription(description);

                    trMap.put(trCode, info);
                } else {
                    info.addExclusiveIpPort(ip, port);
                }
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            DefaultLogger.logger.error("error found", e);
        }
    }

    private static FeedInfoCenter seoulInstance = null;
    private static FeedInfoCenter pusanInstance = null;
    private static ServerLocation firstLocation = null;

    public static FeedInfoCenter getInstance(ServerLocation location) {
        if (firstLocation == null)
            firstLocation = location;
        if (firstLocation != location)
            DefaultLogger.logger.error("Location 초기 셋업과 다른 접근입니다!!!");
        if (location == ServerLocation.PUSAN) {
            if (pusanInstance == null) {
                synchronized (FeedInfoCenter.class) {
                    if (pusanInstance == null)
                        pusanInstance = new FeedInfoCenter(location);
                }
            }
            return pusanInstance;
        } else {
            if (seoulInstance == null) {
                synchronized (FeedInfoCenter.class) {
                    if (seoulInstance == null)
                        seoulInstance = new FeedInfoCenter(location);
                }
            }
            return seoulInstance;
        }
    }

    public synchronized Set<RawFeedInfoForSocket> getConnectionInfo(Set<FeedTRCode> feedSet) {
        feedSet.forEach(feedTRCode -> {
            TrInfo info = trMap.get(feedTRCode.getTrCodeStr());
            if (info == null) {
                DefaultLogger.logger.error("TR정보가 없습니다. feedTRCode={} {}", feedTRCode, feedTRCode.getTrCodeStr());
                return;
            }
            for (Pair<String, Integer> ipPort : info.ipPort) {
                RawFeedInfoForSocket.addTrCode(feedTRCode.getDescription().getLocation(), ipPort.firstElem, ipPort.secondElem, info.trCode, info.length);
            }
        });

        return RawFeedInfoForSocket.finalizeTrCode();
    }

    public Set<RawFeedInfoForSocket> getConnectionInfo(String[] feedList) {
        HashSet<FeedTRCode> set = new HashSet<>();
        for (String s : feedList) {
            for (FeedTRCode feedTRCode : FeedTRCode.values()) {
                if (feedTRCode.toString().equals(s))
                    set.add(feedTRCode);
            }
        }
        return getConnectionInfo(set);
    }

    public TrInfo getTrInfo(String trCode) {
        return trMap.get(trCode);
    }
}
