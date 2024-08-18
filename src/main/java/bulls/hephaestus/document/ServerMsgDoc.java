package bulls.hephaestus.document;

import bulls.dateTime.TimeCenter;
import bulls.db.mongodb.DBCenter;
import bulls.db.mongodb.MongoDBCollectionName;
import bulls.db.mongodb.MongoDBDBName;
import bulls.hephaestus.HephaLogType;
import bulls.log.DefaultLogger;
import bulls.staticData.TempConf;
import bulls.websocket.WebSocketGlobalMessageBroadcaster;
import org.bson.Document;

import java.util.Date;
import java.util.HashSet;

public class ServerMsgDoc {
    final String type;
    final String trCode;
    final String msg;
    final String serverID;
    final Date date;

    private static final HashSet<String> onceADaySet = new HashSet<>();

    private ServerMsgDoc(String type, String trCode, String msg, Date date) {
        this.type = type;
        this.trCode = trCode;
        this.msg = msg;
        this.date = date;
        this.serverID = TempConf.SERVER_ID;
    }

    public static ServerMsgDoc now(HephaLogType type, String trCode, String msg) {
        onceADaySet.add(trCode);
        return new ServerMsgDoc(type.toString(), trCode, msg, TimeCenter.Instance.getDateTimeAsDateType());
    }

    public void fireOnceADay() {
        if (ServerMsgDoc.onceADaySet.contains(trCode))
            return;
        fire();
    }

    public void fire() {
        Document docErr = new Document()
                .append("type", type)
                .append("trCode", trCode)
                .append("msg", msg)
                .append("date", date)
                .append("serverID", serverID);

        DBCenter.Instance.updateBulk(MongoDBDBName.NOTICE, MongoDBCollectionName.SERVER_MSG, new Document(), docErr);
        DefaultLogger.logger.info("ServerMsg: [{}] [{}] [{}] [{}]", serverID, type, trCode, msg);

        WebSocketGlobalMessageBroadcaster.Instance.broadcastLogMessage("ServerMsg", msg);
    }
}
