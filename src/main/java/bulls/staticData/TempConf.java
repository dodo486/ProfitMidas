package bulls.staticData;

import ch.qos.logback.core.joran.spi.JoranException;
import bulls.db.mongodb.DBCenter;
import bulls.db.mongodb.MongoDBDBName;
import bulls.db.mongodb.MongoDBUri;
import bulls.log.DefaultLogger;
import bulls.log.LogBackInit;
import bulls.server.ServerMessageSender;
import bulls.server.enums.MainTestSimul;
import bulls.server.enums.ServerLocation;
import bulls.server.enums.ServerPurpose;
import bulls.tool.conf.KrxConfiguration;
import bulls.tool.util.OsChecker;
import org.bson.Document;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

public class TempConf {
    public static Set<String> getStringAsSet(String str) {
        HashSet<String> strSet = new HashSet<>();
        String[] arr = str.split(",");
        for (String s : arr)
            strSet.add(s.trim());

        return strSet;
    }

    private static final Map<String, String> compatibleMap;

    public static Boolean INTERNAL_WS_FOR_AP_CONTROL_ENABLED = true;

    static {
        Map<String, String> cMap = new HashMap<>();
        cMap.put("RFR", "조달금리");
        cMap.put("serverID", "SERVER_ID");
        cMap.put("proxyDCConsumerSize", "PROXY_DC_CONSUMER_SIZE");
        cMap.put("nativeDCConsumerSize", "NATIVE_DC_CONSUMER_SIZE");
        cMap.put("isRemainOrderElapsedTime", "REMAIN_ORDER_ELAPSEDTIME");
        cMap.put("isRemainContractToDB", "IS_REMAIN_CONTRACT_TO_DB");
        cMap.put("lazyWorkerPoolSize", "LAZY_WORKER_POOL_SIZE");
        cMap.put("monitorLazyWorkerQueue", "MONITOR_LAZY_QUEUE");
        cMap.put("priceInfoCacheSize", "PRICE_INFO_CACHE_SIZE");
        cMap.put("marketStartHour", "MARKET_START_HOUR");
        cMap.put("marketEndHour", "MARKET_END_HOUR");
        cMap.put("marketStartMinute", "MARKET_START_MINUTE");
        cMap.put("marketEndMinute", "MARKET_END_MINUTE");
        cMap.put("mainTestSimul", "MAIN_TEST_SIMUL");
        cMap.put("feedLevel", "FEED_LEVEL");
        cMap.put("disruptorBlocking", "DISRUPTOR_BLOCKING");
        compatibleMap = Collections.unmodifiableMap(cMap);
    }

    public static double 조달금리 = 0.015d;

    public final static int DAY_IN_MILSEC = 1000 * 60 * 60 * 24;

    public static ServerLocation FEP_LOCATION = ServerLocation.SEOUL;
    public static String FEP_LOG_PATH = "/hw02/dma/feplog/";
    public static String logPathRoot;
    public static String SERVER_ID = "UNDEFINED";
    public static ServerPurpose SERVER_PURPOSE = ServerPurpose.UNKNOWN;
    public static MainTestSimul MAIN_TEST_SIMUL = MainTestSimul.TEST;
    public static boolean IS_REMAIN_CONTRACT_TO_DB = false;
    public static boolean DISRUPTOR_BLOCKING = false; //DISRUPTOR_BLOCKING를 체크하는 코드는 disruptor 생성 직전에 삽입
    public static boolean REMAIN_ORDER_ELAPSEDTIME = false;
    public static int PRICE_INFO_CACHE_SIZE = 10;
    public static boolean MONITOR_LAZY_QUEUE = false;
    public static int LAZY_WORKER_POOL_SIZE = 3;
    public static int PROXY_DC_CONSUMER_SIZE = 0;
    public static int NATIVE_DC_CONSUMER_SIZE = 0;
    public static int NATIVE_DC_PRICE_INFO_CONSUMER_SIZE = 0;
    public static int CONTRACT_BREAKDOWN_CENTER_CONSUMER_SIZE = 0;
    public static int DECORATED_DC_CONSUMER_SIZE = 0;
    public static int RAW_FEED_HANDLER_CONSUMER_SIZE = 5;

    public static String DB_URI = "mongodb://172.30.222.38:4611";

    public static int CIRI_PORT = 1351;
    public static int WS_PORT = 4622;

    public static String HEDGE_SERVER_WS_BIND_IP = "0.0.0.0";
    public static int HEDGE_SERVER_WS_PORT = 4622;

    public static String LIVESISE_SERVER_WS_BIND_IP = "0.0.0.0";
    public static int LIVESISE_SERVER_WS_PORT = 4504;

    public static String FEP_ANALYZER_WS_BIND_IP = "172.30.222.37";
    public static int FEP_ANALYZER_WS_PORT = 4623;
    public static int FEP_ANALYZER_POLLING_SEC = 15;

    public static String FEP_ANALYZER_LOG_PATH = "/hw02/dma/LOG/LOG/";

    public static Boolean SEOUL_FEED_CLIENT_ENABLED = false;
    public static String SEOUL_FEED_MULTICAST_INTERFACE_IP = "127.0.0.1";
    public static String SEOUL_FEED_MULTICAST_IP = "224.0.0.1";
    public static int SEOUL_FEED_MULTICAST_PORT = 50009;

    public static Boolean PUSAN_FEED_CLIENT_ENABLED = false;
    public static String PUSAN_FEED_MULTICAST_INTERFACE_IP = "127.0.0.1";
    public static String PUSAN_FEED_MULTICAST_IP = "224.0.0.1";
    public static int PUSAN_FEED_MULTICAST_PORT = 50010;

    public static boolean LIVESISESERVER_USE_WS = true;
    public static boolean LIVESISESERVER_USE_ORDER_BOOK_REPLAYER_EXPANSION = false;
    public static String ORDER_BOOK_REPLAYER_EXPANSION_MARKET_DATA_PATH = "";
    public static String ORDER_BOOK_REPLAYER_EXPANSION_FEP_LOG_PATH = "";

    public static boolean TCPSIMUL_FEP_ENABLED = false;
    public static boolean HIT_ALL_BEFORE_MARKET_END = false;
    public static boolean MARKET_END_INVOLVE_PROTECTOR = false;
    public static boolean FADE_OUT_AMENDER = false;
    public static boolean EXIT_ETC_ETF = false;
    public static boolean STOP_MKOSPI_FUTURE = false;
    public static boolean USE_TOMORROW_DELTA_ON_DERIV_ETF = false;
    public static int NEW_ORDER_COOL_TIME_MS = 3000;

    public static String NO_IP = "NO_IP";
    //	public static String CHANNEL_IP = "192.168.102.117";

    public static boolean CHECK_SELF_IMPACT = false;

    /**
     * <h3>TempConf 초기화</h3>
     *
     * <p>Reflection을 이용해 TempConf 설정을 불러온다.</p>
     * <p>TempConf 파일에 있는 key 값과 static 변수의 이름이 일치하면 해당 변수의 클래스에 맞게 값을 변환해서 넣어주고,
     * 값이 없는 경우에는 compatibleMap에서 호환되는 이름이 있는지 찾아본다. 그래도 없으면 넣어주지 못한다.</p>
     *
     * @param conf KrxConfiguration
     */

    public static void init(KrxConfiguration conf) throws JoranException, IOException {
        Enumeration<String> keys = conf.keys();

        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            String compatibleFieldName = null;

            try {
                Field f = null;
                try {
                    f = TempConf.class.getField(key);
                } catch (NoSuchFieldException e) {
                    try {
                        compatibleFieldName = compatibleMap.get(key);
                        if (compatibleFieldName != null) {
                            f = TempConf.class.getField(compatibleFieldName);
                        } else {
                            DefaultLogger.logger.error("{} 은(는) TempConf에 존재하지 않습니다.", key);
                        }
                    } catch (NoSuchFieldException ex) {
                        DefaultLogger.logger.error("{} 은(는) TempConf에 존재하지 않습니다.", key);
                    }
                }

                if (f == null)
                    continue;

                f.setAccessible(true);
                Class<?> t = f.getType();

                if (t == Integer.class || t == int.class) {
                    try {
                        f.set(null, conf.getInteger(key));
                        if (compatibleFieldName != null)
                            DefaultLogger.logger.info("{} ({}) set to {}", compatibleFieldName, key, f.get(null));
                        else
                            DefaultLogger.logger.info("{} set to {}", key, f.get(null));
                    } catch (NumberFormatException e) {
                        DefaultLogger.logger.info("{} : 입력이 정수가 아니므로 값을 설정하지 않았습니다. (Input : {})", key, conf.getString(key, ""));
                    }
                } else if (t == Double.class || t == double.class) {
                    try {
                        f.set(null, conf.getDouble(key));
                        if (compatibleFieldName != null)
                            DefaultLogger.logger.info("{} ({}) set to {}", compatibleFieldName, key, f.get(null));
                        else
                            DefaultLogger.logger.info("{} set to {}", key, f.get(null));
                    } catch (NumberFormatException e) {
                        DefaultLogger.logger.info("{} : 입력이 실수가 아니므로 값을 설정하지 않았습니다. (Input : {})", key, conf.getString(key, ""));
                    }
                } else if (t == String.class) {
                    f.set(null, conf.getString(key));
                    if (compatibleFieldName != null)
                        DefaultLogger.logger.info("{} ({}) set to {}", compatibleFieldName, key, f.get(null));
                    else
                        DefaultLogger.logger.info("{} set to {}", key, f.get(null));
                } else if (t == byte[].class) {
                    f.set(null, conf.getString(key).getBytes());
                    if (compatibleFieldName != null)
                        DefaultLogger.logger.info("{} ({}) set to {}", compatibleFieldName, key, new String((byte[]) f.get(null)));
                    else
                        DefaultLogger.logger.info("{} set to {}", key, new String((byte[]) f.get(null)));
                } else if (t == Boolean.class || t == boolean.class) {
                    f.set(null, conf.getBoolean(key));
                    if (compatibleFieldName != null)
                        DefaultLogger.logger.info("{} ({}) set to {}", compatibleFieldName, key, f.get(null));
                    else
                        DefaultLogger.logger.info("{} set to {}", key, f.get(null));
                } else if (t.isEnum()) {
                    try {
                        f.set(null, Enum.valueOf((Class<Enum>) t, conf.getString(key)));
                        if (compatibleFieldName != null)
                            DefaultLogger.logger.info("{} ({}) set to {}", compatibleFieldName, key, f.get(null));
                        else
                            DefaultLogger.logger.info("{} set to {}", key, f.get(null));
                    } catch (IllegalArgumentException e) {
                        DefaultLogger.logger.info("enum 타입인 {} 은(는) {} 이(가) 없습니다.", key, conf.getString(key));
                    }
                } else {
                    DefaultLogger.logger.error("{}의 타입을 알 수 없어서 값을 설정하지 못했습니다.", key);
                }
            } catch (IllegalAccessException e) {
                DefaultLogger.logger.error("{}에 접근할 권한이 없는 것 같습니다.", key);
            }
        }

        if (!OsChecker.isWindows() && TempConf.MAIN_TEST_SIMUL == MainTestSimul.MAIN)
            TempConf.IS_REMAIN_CONTRACT_TO_DB = true;

        if (OsChecker.isWindows()) {
            logPathRoot = "D:\\log\\";
        } else {
            logPathRoot = "/hw03/dma/log/";
        }

        if (TempConf.LOGBACK_PATH != null && !TempConf.LOGBACK_PATH.isEmpty()) {
            LogBackInit.Instance.initialize(TempConf.LOGBACK_PATH);
            System.out.println("Logback Init file : " + TempConf.LOGBACK_PATH);
        }

        MongoDBUri.Instance.setUri(DB_URI);
        setRateFromDB();
    }

    private static void setRateFromDB() {
        Document d = DBCenter.Instance.findIterable(MongoDBDBName.PRICING_DATA, "rate").first();
        if (d == null) {
            DefaultLogger.logger.error("DB에서 금리를 불러오지 못했습니다. 금리는 기본값인 {}로 설정됩니다.", TempConf.조달금리);
            return;
        }

        double rate;
        try {
            rate = d.getDouble("rate");
        } catch (ClassCastException e) {
            DefaultLogger.logger.error("DB에서 금리를 불러올 때 오류가 발생했습니다. 금리는 기본값인 {}로 설정됩니다.", TempConf.조달금리);
            return;
        }

        if (rate <= 0 || rate >= 0.1) {
            ServerMessageSender.writeServerMessage(TempConf.class, "OracleArena", "주의",
                    "DB에 들어있는 금리가 이상할 수 있습니다. (" + rate + ")");
        }

        TempConf.조달금리 = rate;
        DefaultLogger.logger.info("DB에서 금리를 불러왔습니다. 금리는 {}로 설정됩니다.", TempConf.조달금리);
    }

    public static byte[] MAC_ADDRESS = "UNKNOWNMAC".getBytes();
    public static String IP_FOR_LOG = "172.18.108.67";
    public static byte[] IP_FOR_KRX = "000000000000".getBytes();
    public static int HEARTBEAT_INTERVAL = 15 * 1000;

    public static String memberId = "052";
    public static int HeartbeatPeriod = 10000;
    public static int CLIENT_NOTIFY_INTERVAL = 2000;

    public static double DELTA_RESERVE_RATE = 0.95;

    public static String EXECUTION_LOG_PATH;
    public static String ORDER_ID_LOG_PATH = "/home/cleu/log/orderId";

    public static String ifPidInfo;
    public static String ifNum;

    public static double DEFINED_VOL = 0.15;

    public static int VIRTUAL_ORDER_ID_START = 7000000;

    public static int AMEND_LIMIT = 30;
    public static String LOGBACK_PATH = null;
    public static int CLIENT_CONNECTION_PORT = 9800;
    public static String COND_ORDER_INFO = "CondOrderInfo";
    public static String COND_ORDER_INFO_INST = "CondOrderInfoInst";

    /**
     * 주문/체결 관련
     */
    public static boolean SEND_CANARIES = false;
    public static boolean DECODE_JAMMER = false;
    public static String DECODE_JAMMER_FUT_DUT_LIST = ""; // MKI,KQI
    public static String DECODE_JAMMER_OPT_DUT_LIST = "MKI,KQI"; // MKI,KQI

    /**
     * 호가 관련
     */
    public static long DERIV_MONEY_LIMIT_PER_ORDER = 500_000_000;

    public static Boolean FEED_TO_DB_TS_ENABLED = false;
    public static Boolean FEED_TO_DB_SAVE_HISTORY = false;
    public static Boolean MARKETGREEK_DB_CLEAR = false;
    public static Boolean MARKETGREEKCENTER_ENABLED = false;
    public static Boolean FEED_TO_PGDB = false;

    public static Boolean LOAD_MINIMAL_PDF = false;
    public static String LOAD_PREVIOUS_PDF_ISINCODE_LIST = null;

    public static double 유관기관수수료_주식 = 0.00001;

    public static Boolean LIVE_ACCOUNT_BALANCE_TO_DB = false;
    public static Boolean LIVE_BOOK_BALANCE_TO_DB = false;

    public static String SM_MGMT_ADDR = "http://172.30.222.38:18080/hazelcast-mancenter/";

    public static Boolean SM_SERVER_HEDGESERVER_BOOK_BALANCE = false;
    public static Boolean SM_CLIENT_HEDGESERVER_BOOK_BALANCE = false;
    public static Boolean SM_SERVER_HEDGESERVER_BOOK_ACC_CONTRACT = false;
    public static Boolean SM_CLIENT_HEDGESERVER_BOOK_ACC_CONTRACT = false;
    public static Boolean SM_SERVER_HEDGESERVER_CONTRACT_LIST = false;
    public static String SM_SERVER_HEDGESERVER_CONTRACT_LIST_TARGET = "S:KRD020020016, M:KRD020020016";
    public static Boolean SM_CLIENT_HEDGESERVER_CONTRACT_LIST = false;
    public static String SM_CLIENT_HEDGESERVER_CONTRACT_LIST_TARGET = "S:KRD020020016, M:KRD020020016";


    public static Boolean SM_SERVER_ENABLED = false;
    public static String SM_SERVER_IP = "127.0.0.1";
    public static Integer SM_SERVER_PORT = 19000;

    public static Boolean SM_CLIENT_ENABLED = false;

    public static String HEDGE_DELEGATE_BOOKCODE_LIST = "M:KR7001450006";
    public static String ETF_SELF_HEDGE_BOOKCODE_LIST = "M:ETF_K200";

    public static Boolean HEDGE_SERVER_PACKET_CAPTURE_ENABLED = false;
    public static String HEDGE_SERVER_PACKET_CAPTURE_INTERFACE_IP = "";
    public static String HEDGE_SERVER_PACKET_CAPTURE_LIST = "SeoulDerivContractAP2,SeoulEquityContractAP1";
    public static Boolean MAIN_TO_SUB = false;
    public static Boolean COPY_FIXED_VOL_CURVE = false;
    public static double IOC_SIMUL_CONTRACT_RATIO = 0.4;

    public static Boolean EXTERNAL_WS_FOR_AP_CONTROL_ENABLED = false;

    public static Boolean CUSTOMINDEX_STARTINGPOINT_ENABLED = false;
    public static Boolean CUSTOMINDEX_SNAPSHOT_ENABLED = false;

    public static String ZULIP_장개시전정합성체크 = "장개시전정합성체크";
    public static String ZULIP_장중자동화 = "장중자동화";
    public static String ZULIP_장종료자동화 = "장종료자동화";
    public static String ZULIP_장개시자동화 = "장개시자동화";

    public static Boolean EQUITY_AC_ON_NATIVE_DC = true;

    public static Boolean DERIV_AC_ON_NATIVE_DC = true;
    public static Boolean NATIVEDC_QUOTE_OPPOSITE_ON_CONTRACT = false;
    public static Boolean NATIVEDC_QUOTE_MIN_AMOUNT = true;
    public static Boolean NATIVEDC_BIDASK_TO_DB = false;
    public static String AC_TO_CONSOLE_CODE = "";
    public static Boolean RECORD_AC = false;
    public static Boolean UPDATE_FROM_FEED = false;

    /**
     * 장운영 시간 관련
     */

    public static int MARKET_START_HOUR = 9;
    public static int MARKET_START_MINUTE = 0;
    public static int MARKET_END_HOUR = 15;
    public static int MARKET_END_MINUTE = 20;


    public static Boolean FORCE_FUTURES_MATURITY_ENABLED = false;
    public static Integer FORCE_FUTURES_MATURITY_DATE = 0;

    public static Boolean ELW_MONITORING_CENTER_ENABLED = false;
    public static Boolean ELW_BASIS_MONITORING_CENTER_ENABLED = false;

    public static Boolean WRITE_CONTRACT_PROBABILITY_TO_DB = false;

    public static Boolean ML_FEATURES_BROADCAST = false;
    public static String ML_FEATURES_BROADCAST_IP = "224.0.0.123";
    public static int ML_FEATURES_BROADCAST_PORT = 5555;
    public static String ML_FEATURES_BROADCAST_INTERFACE = "127.0.0.1";

    public static Boolean MARKET_VOL_CENTER_ENABLED = false;
    public static Boolean MARKET_VOL_CURR_DATA_TO_DB = false;
    public static Boolean MARKET_VOL_BUILD_TS = false;
    public static Boolean MARKET_VOL_INIT_ALL = false;
    public static Boolean MARKET_VOL_INIT_SO_DUTY = false;
    public static String MARKET_VOL_SO_UNDERLYING_ISINCODE_LIST = "";
    public static Boolean MARKET_VOL_INIT_KP200_OPT = false;
    public static Boolean MARKET_VOL_INIT_KQ150_OPT = false;
    public static Boolean MARKET_VOL_INIT_KP200_WEEKLY_OPT = false;

    public static Boolean QUOTE_VOL_CENTER_ENABLED = false;
    public static Boolean QUOTE_VOL_CURR_DATA_TO_DB = false;
    public static String QUOTE_VOL_CALC_LIST = "MKI";

    public static String MINI_SUB_BOOK_CODE = "S:KRD020020016";
    public static String MINI_MAIN_BOOK_CODE = "M:KRD020020016";
    public static String KQ_SUB_BOOK_CODE = "S:KRD020021378";
    public static String KQ_MAIN_BOOK_CODE = "M:KRD020021378";
    public static boolean ACCOUNT_PAIR_ENABLED = false;

    public static boolean MARKET_DATA_REPLAYER_ENABLED = true;
    public static String MARKET_DATA_REPLAYER_INDEX_PATH = "tmpindex";

    public static boolean UPLOAD_INDEX_TO_INFLUX_DB = false;

    public static boolean LIMIT_PRICE_TO_DB = false;
    public static boolean IGNORE_LIMIT_PRICE = false;

    public static int UP_DOWN_TICK_FOR_LP_AMEND_ORDER = 5;

    public static double ACCOUNT_BALANCED_AMOUNT_MULTIPLE_MIN = 1.2;
    public static double ACCOUNT_BALANCED_AMOUNT_MULTIPLE_MAX = 3.0;

    public static int HISTORICAL_ACCOUNT_BALANCE_LOAD_DAYS_MAX = 30;
    public static int HISTORICAL_ACCOUNT_BALANCE_LOAD_DAYS_DEFAULT = 2;

    public static double 종가_직전가대비_변화율한도 = 0.01;
    public static double 종가_체결수량_관여한도비율 = 0.20;

    public static String ETF_PDF_DIFFERENCE_CHECK_ISINCODE_LIST = "";

    public static boolean IS_FEED_BROADCASTER = false;

    public static String EXTERNAL_ETF_PRICER_DATA_LIST = "KR7252670005,KR7267770006";

    public static String ATM_CALC_DUT_LIST = "K2I";

    public static boolean MARKET_VOL_CENTER_DIRECT_CALC_HIGH_PRIORITY = false;

    public static boolean SUB_FEED_INTERFACE_ENABLED = false;
    public static String SUB_FEED_INTERFACE_IP = "127.0.0.1";
    //Kosdaq equity, kospi opt, weekly opt, mini opt, equity option, kosdaq option
    public static String SUB_FEED_TR_LIST = "A3012,B6012,A6012,A3034,B6034,G7034,B2034,A3184,B6184,G7184,A3134,B6134,G7134,A3025,B6025,G7025,A3174,B6174,G7174,A6034,A6184,A6134,A6174,A6025,N7034,N7134,N7174,N7025";

    public static boolean ENABLE_CODEHASHOBSTATION_LOGGING = false;
    public static String TRINFOLIST_SEOUL_FILENAME = "TrInfoList";
    public static String TRINFOLIST_PUSAN_FILENAME = "PusanTrInfoList";

    public static boolean EXPIRY_CLEARING_TEST_MODE = false;

    public static double ML_DATA_CONTRACT_BREAKDOWN_TRIGGER_DELTA_DEFAULT = 0.1;

    public static String MATCHING_RESULT_SAVE_DIRECTORY = "matchingResult";

    public static boolean SQUARE_ETF_BASKET_BOOK = true;
    // OrderMatchingEngine에서 선물-현물 선후관계를 확인할 때
    // 해당 종목의 남은 수량 < ORDER_MATCHING_ENGINE_PRINT_STATUS_THRESHOLD * pdfAmount 인 경우 현재 상태를 출력한다.
    public static double ORDER_MATCHING_ENGINE_PRINT_STATUS_THRESHOLD = 1;

    public static boolean LOG_POLLING_FEED = false;

    public static boolean RUN_HEDGE_WITH_LP = false;

    public static boolean DETERMINE_ASK_TYPE_CODE_ON_FEP = false;

    public static boolean ORDER_TICKET_ENABLED = true;
    public static boolean UPDATE_G2_ACC_VOLUME_TO_DB = false;

    // 지정한 주기(분)마다 코스닥 옵션 자동 조정
    // 0 이하의 값이면 자동 조정 기능 비활성화
    public static int KOSDAQ_OPTION_AUTO_CONTROL_PERIOD = 10;

    public static long DETERMINE_TAKE_THRESHOLD_NANO_TIME = 1_500_000;

    // 해당되는 유형의 Price Tracker를 사용할지 여부 설정 (K200/KQ150 L&I ETF)
    public static boolean USE_K200_PRICE_TRACKER = false;
    public static boolean USE_KQ150_PRICE_TRACKER = false;
    // Price Tracker 정보를 DB에 업로드할지 여부 설정
    public static boolean UPDATE_PRICE_TRACKER_TO_DB = false;
    // Price Tracker에서 의미 있는 체결이라고 판단할 체결 수량의 최소값
    public static int PRICE_TRACKING_AMOUNT_THRESHOLD = 10_000;

    public static boolean AGGRESSIVE_FIT_TO_MARKET_CONTRACT = true;
    public static int FIT_TO_MARKET_CONTRACT_MAX_HIT_COUNT = 1;

    // 연휴일 때 시간 가치를 덜 감기 위해 사용하는 보정치
    // TimeProjectionCenter 참조
    public static double TIME_TILL_EXPIRY_CORRECTION_RATIO_ON_HOLIDAY = 0.25;

    public static boolean RECORD_BAS = false;
}
