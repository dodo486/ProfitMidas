package bulls.dmaLog;

import com.fasterxml.jackson.databind.node.ObjectNode;
import bulls.designTemplate.HasTime;
import bulls.designTemplate.JsonConvertible;
import bulls.dmaLog.enums.DMALogFileType;
import bulls.dmaLog.enums.DMALogTransactionType;
import bulls.log.DefaultLogger;
import bulls.order.CodeAndBook;
import bulls.order.enums.FundType;
import bulls.order.enums.LongShort;
import bulls.order.enums.ShortSellCode;

import java.nio.ByteBuffer;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

public abstract class DMALog implements Comparable<DMALog>, JsonConvertible, HasTime {
    protected String serverType;
    protected String sendType;
    protected String masterType;
    protected String serverNumberString;
    protected LocalTime time;
    protected String orderId;
    protected ByteBuffer directBufferForPacket;

    // Filled from concrete class
    protected char transactionCode;

    public String getPacket() {
        byte[] packetBytes = new byte[directBufferForPacket.capacity()];
        directBufferForPacket.position(0);
        directBufferForPacket.get(packetBytes);
        return new String(packetBytes);
    }

    public byte[] getPacketBytes() {
        byte[] packetBytes = new byte[directBufferForPacket.capacity()];
        directBufferForPacket.position(0);
        directBufferForPacket.get(packetBytes);
        return packetBytes;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime t) {
        time = t;
    }

    public String getCurrentOrderId() {
        return orderId;
    }

    public String getPacketBody() { // remove header
        byte[] packetBytes = new byte[directBufferForPacket.capacity() - 20];
        directBufferForPacket.position(20);
        directBufferForPacket.get(packetBytes);
        return new String(packetBytes);
    }

    public String getServerType() {
        return serverType;
    }

    public String getFileName() {
        return serverType + "_" + sendType + "_" + masterType + serverNumberString;
    }

    public abstract String getOriginalOrderId();

    public abstract String getIsinCode();

    public abstract double getOrderPrice();

    public abstract long getOrderQuantity();

    public abstract LongShort getAskBidType();

    public abstract String getAccountNumber();

    public abstract String getBookCode();

    public abstract String getPurpose();

    public abstract FundType getFundType(); // 잔고 유형 (해당 주문이 일반 잔고 / 대차 잔고 중 어느 잔고를 차감하며 주문이 나갔는지)

    public abstract ShortSellCode getShortSellCode();

    public final CodeAndBook getCodeAndBook() {
        return CodeAndBook.getOrCreate(getIsinCode(), getBookCode());
    }

    public final DMALogTransactionType getTransactionType() {
        return DMALogTransactionType.of(transactionCode);
    }

    final boolean parse(DMALogFileType type, String logString) {
        if (type == DMALogFileType.LOG)
            return parseLog(logString);
        else if (type == DMALogFileType.PCAP)
            return parsePcap(logString);

        return false;
    }

    private boolean parseLog(String logString) {
        time = DMALogUtil.parseTimeInLog(logString);
        if (time == null)
            return false;

        List<String> typeParseResult = DMALogUtil.parseTypeString(logString);
        if (typeParseResult.size() != 4)
            return false;

        serverType = typeParseResult.get(0).intern();
        sendType = typeParseResult.get(1).intern();
        masterType = typeParseResult.get(2).intern();
        serverNumberString = typeParseResult.get(3).intern();

        String packet = DMALogUtil.parsePacket(logString);
        return internalParse(packet);
    }

    private boolean parsePcap(String logString) {   // NanoTime
        time = DMALogUtil.parseTimeInPcap(logString);
        String packet = logString.substring(15);
        return internalParse(packet);
    }

    private boolean internalParse(String packet) {
        try {
            if (!fillDataFromPacket(packet))
                return false;
        } catch (NumberFormatException e) {
            DefaultLogger.logger.error("DMALog Parsing Error : packet=" + packet);
            return false;
        }

        this.directBufferForPacket = ByteBuffer.allocateDirect(packet.length());
        this.directBufferForPacket.put(packet.getBytes());
        return true;
    }

    protected abstract boolean fillDataFromPacket(String packet);

    public abstract void print();

    public abstract String getTypeString();

    public void fillObjectNode(ObjectNode node) {
        node.put("type", this.getClass().getSimpleName());
        node.put("serverType", serverType);
        node.put("sendType", sendType);
        node.put("masterType", masterType);
        node.put("serverNumberString", serverNumberString);
        node.put("time", time.toString());
        node.put("orderId", orderId);
        node.put("packet", getPacket());
    }

    @Override
    public int compareTo(DMALog log) {
        int result;
        result = time.compareTo(log.getTime());
        if (result != 0)
            return result;

        result = orderId.compareTo(log.orderId);
        if (result != 0)
            return result;

        return getPacket().compareTo(log.getPacket());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DMALog dmaLog = (DMALog) o;
        return Objects.equals(serverType, dmaLog.serverType) &&
                Objects.equals(sendType, dmaLog.sendType) &&
                Objects.equals(masterType, dmaLog.masterType) &&
                Objects.equals(serverNumberString, dmaLog.serverNumberString) &&
                Objects.equals(time, dmaLog.time) &&
                Objects.equals(orderId, dmaLog.orderId) &&
                Objects.equals(directBufferForPacket, dmaLog.directBufferForPacket);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getServerType(), sendType, masterType, serverNumberString, time, orderId, directBufferForPacket);
    }
}