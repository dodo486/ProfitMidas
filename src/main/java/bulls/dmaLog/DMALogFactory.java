package bulls.dmaLog;

import bulls.channel.hanwhaDMA.주문;
import bulls.dmaLog.enums.DMALogFileType;
import bulls.dmaLog.enums.DMALogTransactionType;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class DMALogFactory {

    private final static DMALog dummyLog = new TradeDMALog();

    public static DMALog getDummyLog() {
        return dummyLog;
    }

    public static DMALog getLog(String logString) {
        return getLog(DMALogFileType.LOG, logString);
    }

    public static DMALog getLogFromPcap(String logString) {
        return getLog(DMALogFileType.PCAP, logString);
    }

    public static DMALog getLog(DMALogFileType fileType, String logString) {
        String packet;
        if (fileType == DMALogFileType.LOG)
            packet = DMALogUtil.parsePacket(logString);
        else if (fileType == DMALogFileType.PCAP)
            packet = logString.substring(15);
        else
            return null;

        byte[] packetBytes = packet.getBytes();
        byte messageType, transactionCode;

        if (packet.equals(""))
            return null;

        messageType = 주문.HeaderMsgType.parser().parseSingleByte(packetBytes);
        if (messageType != 'D')
            return null;

        transactionCode = 주문.TRCode.parser().parseSingleByte(packetBytes);
        DMALogTransactionType transactionType = DMALogTransactionType.of((char) transactionCode);
        if (transactionType.clazz == null)
            return null;

        DMALog log;
        try {
            Constructor<? extends DMALog> constructor = transactionType.clazz.getConstructor();
            log = constructor.newInstance();
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }

        if (log.parse(fileType, logString))
            return log;

        return null;
    }
}
