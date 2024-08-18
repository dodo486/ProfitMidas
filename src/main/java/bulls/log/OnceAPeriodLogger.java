package bulls.log;

import bulls.hephaestus.HephaLogType;
import bulls.hephaestus.document.ServerMsgDoc;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ConcurrentHashMap;

public enum OnceAPeriodLogger {
    Instance;
    private final ConcurrentHashMap<String, LocalDateTime> lastHitMap = new ConcurrentHashMap<>();

    OnceAPeriodLogger() {

    }

    public void tryPrintInfo(long unitCount, ChronoUnit unit, String msg, boolean isSendServerMsg) {
        tryPrintInfo(unitCount, unit, msg, msg, isSendServerMsg);
    }

    //특정 키(ex. BookCode, IsinCode..)로 메세지 처리
    public void tryPrintInfo(long unitCount, ChronoUnit unit, String msg, String key, boolean isSendServerMsg) {
        synchronized (this) {
            LocalDateTime lastHit = lastHitMap.get(key);
            if (lastHit == null) {
                lastHitMap.put(key, LocalDateTime.now());
                DefaultLogger.logger.info("Log Once a {}{}, msg: {}, key: {}", unitCount, unit, msg, key);
                if (isSendServerMsg)
                    ServerMsgDoc.now(HephaLogType.주의, "OnceAPeriod", msg).fire();
                return;
            }
            LocalDateTime now = LocalDateTime.now();
            if (!lastHit.plus(unitCount, unit).isBefore(now))
                return;

            lastHitMap.put(key, now);
            DefaultLogger.logger.info("Log Once a {}{}, msg: {}, key: {}", unitCount, unit, msg, key);
            if (isSendServerMsg)
                ServerMsgDoc.now(HephaLogType.주의, "OnceAPeriod", msg).fire();
        }
    }

    public boolean isPrintPossible(long unitCount, ChronoUnit unit, String key) {
        LocalDateTime lastHit = lastHitMap.get(key);
        if (lastHit == null)
            return true;

        LocalDateTime now = LocalDateTime.now();
        return lastHit.plus(unitCount, unit).isBefore(now);
    }

    public void writeLog(String key, String msg, Object... var1) {
        LocalDateTime now = LocalDateTime.now();
        lastHitMap.put(key, now);
        DefaultLogger.logger.info(msg, var1);
    }

    public void tryPrintErr(long unitCount, ChronoUnit unit, String msg, boolean isSendServerMsg) {
        tryPrintErr(unitCount, unit, msg, msg, isSendServerMsg);
    }

    public void tryPrintErr(long unitCount, ChronoUnit unit, String msg, String key, boolean isSendServerMsg) {
        synchronized (this) {
            LocalDateTime lastHit = lastHitMap.get(key);
            if (lastHit == null) {
                lastHitMap.put(key, LocalDateTime.now());
                DefaultLogger.logger.info("Log Once a {}{}, msg: {}, key: {}", unitCount, unit, msg, key);
                if (isSendServerMsg)
                    ServerMsgDoc.now(HephaLogType.운영장애, "OnceAPeriod", msg).fire();
                return;
            }
            LocalDateTime now = LocalDateTime.now();
            if (!lastHit.plus(unitCount, unit).isBefore(now))
                return;

            lastHitMap.put(key, now);
            DefaultLogger.logger.info("Log Once a {}{}, msg: {}, key: {}", unitCount, unit, msg, key);
            if (isSendServerMsg)
                ServerMsgDoc.now(HephaLogType.운영장애, "OnceAPeriod", msg).fire();
        }
    }
}
