package bulls.json;

/**
 * pre-defined JsonValue for LP system
 * camel convention.
 */
public abstract class JsonValue {
    public static final String POSITION = "Position";
//    public static final String BOOK_MINI = "MINI";
//    public static final String SUMMARY_ALL = "ALL";
//    public static final String POSITION_SUMMARY = "Summary";

    public static final String TRCODE_HEARTBEAT = "HeartBeat";

    public static final String TRCODE_HOGA_INFO = "HogaInfo";
    public static final String TRCODE_HOGA_STOP = "HogaStop";
    public static final String TRCODE_HOGA_QUERY = "HogaQuery";
    public static final String TRCODE_HOGA_CLEAR = "HogaClear";

    public static final String TRCODE_HOGA_DODGE_INFO = "HogaDodgeInfo";
    public static final String TRCODE_LOGMSG = "LogMsg";
    public static final String TRCODE_RESET = "Reset";
    public static final String TRCODE_BOOK = "Book";


    public static final String TRCODE_HEDGE_INFO = "HedgeInfo";
    public static final String TRCODE_DELTA_HEDGE_INFO = "DeltaHedgeInfo";


    public static final String TRCODE_VIRTUAL_CONTRACT = "VirtualContract";
    public static final String TRCODE_INITIAL_CONTRACT = "InitialContract";
    public static final String TRCODE_GET_BIDASK = "GetBidAsk";
    public static final String TRCODE_RECEIVE_CONFIRM = "ReceiveConfirm";

    public static final String TRCODE_QUOTE_START = "QuoteStart";
    public static final String TRCODE_QUOTE_UPDATE = "QuoteUpdate";
    public static final String TRCODE_QUOTE_STOP = "QuoteStop";
    public static final String TRCODE_QUOTE_FADEOUT = "QuoteFadeOut";
    public static final String TRCODE_QUOTE_DETACH = "QuoteDetach";
    public static final String TRCODE_QUOTE_INFO = "QuotingInfo";
    public static final String TRCODE_CANCELLER_INFO = "CancellerInfo";
    public static final String TRCODE_CANCELLER_START = "CancellerStart";
    public static final String TRCODE_CANCELLER_STOP = "CancellerStop";

    public static final String TRCODE_MANUAL_ORDER = "ManualOrder";
    public static final String TRCODE_STOP_ORDER = "StopOrder";
    public static final String TRCODE_MANUAL_ORDER_HIT = "ManualOrderHit";
    public static final String TRCODE_ORDER_REMOVED = "OrderRemoved";
    public static final String TRCODE_WORKING_ORDER = "WorkingOrder";

    public static final String TRCODE_MANUAL_HEDGE = "ManualHedge";

    public static final String TRCODE_PANIC = "Panic";
    public static final String TRCODE_RESTORE_PANIC = "RestorePanic";
    public static final String TRCODE_EMERGENCY = "Emergency";
    public static final String TRCODE_REINFORCEMENT = "StockReinforcement";

    public static final String TRCODE_SERVER_CONTROL = "ServerControl";

    public static final String TRCODE_SERVER_STATE = "ServerState";

    public static final String TRCODE_GENERAL_ORDER_INFO = "CondOrderInfo";

    public static final String TRCODE_FEP_TRANSACTION_TRACK = "TransactionTrack";
    public static final String TRCODE_FEP_TPS_VIEW = "TPSView";
    public static final String TRCODE_FEP_UNFINISHED_ORDER_LIST = "unfinishedOrderList";
    public static final String TRCODE_FEP_TODAY_TOTAL_QUANTITY = "todayTotalQuantity";
    public static final String TRCODE_FEP_TODAY_TOTAL_PRICE = "todayTotalPrice";
    public static final String TRCODE_CANARY = "Canary";
    public static final String TRCODE_SPREAD_ORDER_INFO = "SpreadOrderInfo";
    public static final String TRCODE_SPREAD_ORDER_CANCEL = "SpreadOrderCancel";
    public static final String TRCODE_BASKET_CLEARING_INFO_REQUEST = "BasketClearingInfoRequest";
    public static final String TRCODE_BASKET_CLEARING_ORDER = "BasketClearingOrder";

    public static final String TRCODE_LOCAL_WO_REQUEST = "LocalWORequest";
    public static final String TRCODE_LOCAL_WO_CANCEL = "LocalWOCancel";

    public static final String TRCODE_ACCOUNT_POSITION_DATA_REQUEST = "AccountPositionDataRequest";
    public static final String TRCODE_BASKET_ORDER = "BasketOrder";

    public static final String TRCODE_BD_STATUS_REQUEST = "ContractBreakdownStatusRequest";
    public static final String TRCODE_BD_PARAMETER_UPDATE = "ContractBreakdownParameterUpdate";

    public static final String TRCODE_ETF_GROUP_REBALANCING_DATA_REQUEST = "EtfGroupRebalancingDataRequest";

    public static final String TRCODE_TO_BE_PDF_REQUEST = "ToBePdfRequest";
}
