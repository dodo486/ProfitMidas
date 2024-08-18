package bulls.dmaLog;

public final class FEPConstantValue {
    public static final String ORDER_ID_ZERO = "0000000000";
    public static final String ORDER_ID_NEW = "          ";

    public static final double SEC_TO_NANO = 1000000000.0;

    public static final int DEFAULT_BUFFER_SIZE = 8192;
    public static final int DEFAULT_COUNT_SIZE = 50;

    public static final int QUEUE_PROCESS_COUNT = 200 * 2;
    public static final int TPS_WARNING_THRESHOLD = 300;

    public static final String EQUITY_SERVER_TYPE = "ls";
    public static final String DERIV_SERVER_TYPE = "lf";

    public static final int MINIMUM_TPS_GRAPH_RECORD_THRESHOLD = 20;
}
