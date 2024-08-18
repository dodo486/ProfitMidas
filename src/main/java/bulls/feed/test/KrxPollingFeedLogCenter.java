package bulls.feed.test;

import bulls.feed.udpInfo.RawFeedInfoForSocket;
import bulls.log.DefaultLogger;
import bulls.tool.util.MultiScheduleTimer;
import bulls.tool.util.PeriodicRunnable;

import java.util.Comparator;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum KrxPollingFeedLogCenter {
    Instance;
    final ConcurrentHashMap<String, ConcurrentHashMap<RawFeedInfoForSocket, AtomicInteger>> map = new ConcurrentHashMap<>();
    final Pattern pattern = Pattern.compile("^I[0-9]{4}");

    KrxPollingFeedLogCenter() {
        PeriodicRunnable pr = new PeriodicRunnable("KrxPollingFeedLogCenter", this::print, 30, 0);
        MultiScheduleTimer.Instance.registerPeriodic(pr);
    }

    public void initCount(String feedIfIp, RawFeedInfoForSocket info) {
        ConcurrentHashMap<RawFeedInfoForSocket, AtomicInteger> subMap = map.get(feedIfIp);
        if (subMap == null) {
            DefaultLogger.logger.error("초기화되지 않은 FeedIfIp : {}", feedIfIp);
            return;
        }

        subMap.put(info, new AtomicInteger());
    }

    public boolean increaseCountIfPolling(String feedIfIp, RawFeedInfoForSocket info, String trCode) {
        Matcher m = pattern.matcher(trCode);
        if (m.find()) {
            var subMap = map.get(feedIfIp);
            if (subMap == null) {
                DefaultLogger.logger.error("초기화되지 않은 FeedIfIp : {}", feedIfIp);
                return false;
            }
            AtomicInteger counter = subMap.get(info);
            if (counter == null) {
                DefaultLogger.logger.error("초기화되지 않은 RawFeedInfoForSocket : {}", info);
                return false;
            }
            counter.incrementAndGet();
            return true;
        }
        return false;
    }

    public void print() {
        DefaultLogger.logger.info("\n{}", this);
    }

    public void addInterface(String feedIfIp) {
        map.putIfAbsent(feedIfIp, new ConcurrentHashMap<>());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("#################################################################\n");
        sb.append("                   KrxPollingFeedLogCenter\n");
        sb.append("KRX시세 폴링 매분마다 Main회선에서 1개 Backup회선에서 1개 총 2개 동시 발생\n");
        sb.append("#################################################################\n");
        map.forEach((feedIfIp, m) -> {
                    TreeMap<RawFeedInfoForSocket, AtomicInteger> tm = new TreeMap<>(Comparator.comparing(e -> (e.getIp() + " " + e.getPort())));
                    tm.putAll(m);
                    tm.forEach((k, v) -> {
                        sb.append(k.getLocation());
                        sb.append("/");
                        sb.append(feedIfIp);
                        sb.append("\t");
                        sb.append(k.getIpPortLengthTrString());
                        sb.append("\t");
                        sb.append(v.get());
                        sb.append("\n");
                    });
                }
        );
        sb.append("#####################################\n");
        return sb.toString();
    }
}
