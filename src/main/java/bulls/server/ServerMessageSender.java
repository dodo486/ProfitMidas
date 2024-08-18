package bulls.server;

import ch.qos.logback.core.joran.spi.JoranException;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import bulls.db.mongodb.MongoDBDBName;
import bulls.db.mongodb.MongoDBUri;
import bulls.exception.ConfigurationException;
import bulls.log.DefaultLogger;
import bulls.log.LogMsgType;
import bulls.staticData.TempConf;
import bulls.tool.conf.KrxConfiguration;
import bulls.tool.util.MsgUtil;
import bulls.zulip.ZulipClient;
import org.bson.Document;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

public abstract class ServerMessageSender {
    static MongoClient client = null;
    private static final Set<String> validStreamSet = Set.of("ServerMsg", "ServerInfoMsg");
    private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd(E) HH:mm:ss");

    public static void writeServerMessage(Class<?> clazz, String trCodeTopic, String msgType, String msg, Throwable e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        String exceptionAsString = sw.toString();
        send(clazz, "ServerMsg", trCodeTopic, msgType, msg + "\n------------------------------------------\n" + exceptionAsString, true);
    }

    public static void writeServerMessage(Class<?> clazz, String trCodeTopic, String msgType, String msg) {
        send(clazz, "ServerMsg", trCodeTopic, msgType, msg, true);
    }

    public static void writeServerMessage(Class<?> clazz, String trCodeTopic, String msgType, String msg, Object... arguments) {
        String formattedString = MsgUtil.getFormattedString(msg, arguments);
        send(clazz, "ServerMsg", trCodeTopic, msgType, formattedString, true);
    }

    public static void writeServerInfoMessage(Class<?> clazz, String trCodeTopic, String msgType, String msg) {
        send(clazz, "ServerInfoMsg", trCodeTopic, msgType, msg, false);
    }

    public static void writeServerInfoMessage(Class<?> clazz, String trCodeTopic, String msgType, String msg, Object... arguments) {
        String formattedString = MsgUtil.getFormattedString(msg, arguments);
        send(clazz, "ServerInfoMsg", trCodeTopic, msgType, formattedString, false);
    }

    public static void writeLogAndServerInfoMsg(Class<?> clazz, LogMsgType logMsgType, String trCodeTopic, String msgType, String msg) {
        if (logMsgType == LogMsgType.INFO)
            DefaultLogger.logger.info(msgType + " - " + msg);
        else if (logMsgType == LogMsgType.ERROR)
            DefaultLogger.logger.error(msgType + " - " + msg);

        ServerMessageSender.writeServerInfoMessage(clazz, trCodeTopic, msgType, msg);
    }

    public static void writeLogAndServerInfoMsg(Class<?> clazz, LogMsgType logMsgType, String trCodeTopic, String msgType, String msg, Object... arguments) {
        if (logMsgType == LogMsgType.INFO)
            DefaultLogger.logger.info(msgType + " - " + msg, arguments);
        else if (logMsgType == LogMsgType.ERROR)
            DefaultLogger.logger.error(msgType + " - " + msg, arguments);

        ServerMessageSender.writeServerInfoMessage(clazz, trCodeTopic, msgType, msg, arguments);
    }

    private static void send(Class<?> clazz, String stream, String trCodeTopic, String msgType, String msg, boolean writeToDB) {
        if (!validStreamSet.contains(stream)) {
            DefaultLogger.logger.error("ServerMessageSender : 허용되지 않는 stream이므로 메시지를 보내지 않습니다. clazz={}, stream={}, trCodeTopic={}, msgType={}, msg={}",
                    clazz, stream, trCodeTopic, msgType, msg);
            return;
        }

        if (client == null)
            client = new MongoClient(new MongoClientURI(MongoDBUri.Instance.getLiveUri()));

        String serverId = TempConf.SERVER_ID;
        if (serverId == null || serverId.length() == 0 || serverId.equalsIgnoreCase("UNDEFINED")) {
            try {
                InetAddress IP = InetAddress.getLocalHost();
                serverId = IP.getHostAddress();
            } catch (UnknownHostException e) {
                e.printStackTrace();
                serverId = "UNKNOWN";
            }
        }

        Date now = new Date();
        if (writeToDB) {
            MongoDatabase defaultDB = client.getDatabase(MongoDBDBName.NOTICE);
            MongoCollection<Document> serverMsgCol = defaultDB.getCollection("serverMsg");
            Document t = new Document();
            t.put("trCode", trCodeTopic);
            t.put("type", msgType);
            t.put("serverID", serverId);
            t.put("msg", msg);
            t.put("date", now);
            serverMsgCol.insertOne(t);
        }

        ZulipClient.Instance.postMessage(stream, trCodeTopic, String.format("[%s][%s][%s][%s]\n%s", formatter.format(now), msgType, serverId, clazz.getSimpleName(), msg));
    }

    public static void main(String[] args) throws ConfigurationException, IOException, JoranException {
        if (args.length >= 3) {
            KrxConfiguration conf = new KrxConfiguration("ServerMessageSender.conf");
            TempConf.init(conf);
            String trCode = args[0].trim();
            String msgType = args[1].trim();
            StringBuilder msg = new StringBuilder();
            for (int i = 2; i < args.length; ++i) {
                msg.append(args[i]);
                msg.append(' ');
            }
            ServerMessageSender.writeServerMessage(ServerMessageSender.class, trCode, msgType, msg.toString());
        } else {
            System.out.println("Usage : ServerMessageSender [trCode] [msgType] [msg]");
        }
    }
}
