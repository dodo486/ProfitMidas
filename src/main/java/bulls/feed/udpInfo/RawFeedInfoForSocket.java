package bulls.feed.udpInfo;

import com.google.common.collect.HashBasedTable;
import bulls.feed.current.enums.FeedTRCode;
import bulls.log.DefaultLogger;
import bulls.server.enums.ServerLocation;
import bulls.tool.util.ElapsedTimeChecker;
import org.apache.commons.math3.util.FastMath;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


public final class RawFeedInfoForSocket {

    private final ServerLocation feedLocation;
    private final String ip;
    private final int port;

    private int length;
    public Set<String> trCodeList;
    private List<byte[]> trCodeByteList;
    private Set<FeedTRCode> trCodeSet;

    private static HashBasedTable<String, Integer, RawFeedInfoForSocket> rawFeedInfoTable = HashBasedTable.create();

    RawFeedInfoForSocket(ServerLocation location, String ip, int port) {
        this.feedLocation = location;
        this.ip = ip;
        this.port = port;
        trCodeList = new HashSet<>();
        length = 0;
    }

    public ServerLocation getLocation() {
        return feedLocation;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public int getLength() {
        return length;
    }

    private void finalizeRawInfo() {
        this.trCodeByteList = trCodeList.stream().map(String::getBytes).collect(Collectors.toList());
        if (feedLocation == ServerLocation.SEOUL)
            this.trCodeSet = trCodeList.stream().map(FeedTRCode::matchTR).collect(Collectors.toSet());
        else
            this.trCodeSet = trCodeList.stream().map(trCodeStr -> FeedTRCode.matchTRPusan(trCodeStr.getBytes())).collect(Collectors.toSet());
        DefaultLogger.logger.debug("RawFeedInfo {}", this);
    }

    public static RawFeedInfoForSocket addTrCode(ServerLocation location, String ip, int port, String trCode, int length) {
        RawFeedInfoForSocket feedInfoForSocket = rawFeedInfoTable.get(ip, port);

        if (feedInfoForSocket == null) {
            feedInfoForSocket = new RawFeedInfoForSocket(location, ip, port);
            rawFeedInfoTable.put(ip, port, feedInfoForSocket);
        }
        feedInfoForSocket.addTrCode(trCode, length);
        return feedInfoForSocket;
    }

    public static Set<RawFeedInfoForSocket> finalizeTrCode() {
        Set<RawFeedInfoForSocket> feedInfoList = rawFeedInfoTable.values().stream().peek(rawInfo -> rawInfo.finalizeRawInfo()).collect(Collectors.toSet());
        rawFeedInfoTable = HashBasedTable.create(); // 다음 사용을 위해 초기화
        return feedInfoList;
    }

    private void addTrCode(String trCode, int length) {
        trCodeList.add(trCode);
        this.length = FastMath.max(this.length, length);
    }

    public boolean isValidTRCode(FeedTRCode trCode) {
        return trCodeSet.contains(trCode);
    }

    public byte[] isTrCodeMatch(byte[] buffer) {
        return compareByteArray(buffer);
    }

    public byte[] compareByteArray(byte[] buffer) {
        ElapsedTimeChecker.setStart();
        for (byte[] bytes : trCodeByteList) {
            int i;
            for (i = 0; i < 5; i++) {
                if (bytes[i] != buffer[i])
                    break;
            }
            if (i == 5) {
                ElapsedTimeChecker.setEnd(TimeUnit.MICROSECONDS);
                return bytes;
            }
        }

        return null;
    }

    public String getIpPortLengthTrString() {
        StringBuilder sb = new StringBuilder();
        sb.append(ip);
        sb.append(',');
        sb.append(port);
        sb.append(',');
        sb.append(length);

        for (String s : trCodeList) {
            sb.append(',');
            sb.append(s);
        }
        return sb.toString();
    }

    @Override
//    @SuppressWarnings("cast")
    public boolean equals(Object o) {
        if (!(o instanceof RawFeedInfoForSocket))
            return false;
        RawFeedInfoForSocket temp = (RawFeedInfoForSocket) o;
        return ip.equals(temp.ip) && port == temp.port;
    }

    @Override
    public int hashCode() {
        return ip.hashCode() + (13 * Integer.hashCode(port));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(ip);
        sb.append(":");
        sb.append(port);
        sb.append("[length:");
        sb.append(length);
        sb.append("]");
        sb.append(" [");
        for (String trCode : trCodeList) {
            sb.append(trCode);
            sb.append("(");
            if (getLocation() == ServerLocation.SEOUL)
                sb.append(FeedTRCode.matchTR(trCode).getDescription());
            else
                sb.append(FeedTRCode.matchTRPusan(trCode.getBytes()).getDescription());
            sb.append(") ");
        }
        sb.append("]");
        return sb.toString();
    }
}

