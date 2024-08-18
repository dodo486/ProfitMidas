package bulls.db.mongodb;

public abstract class MongoDBCollectionName {
    public static final String CONSUME_STRATEGY = "consumeStrategy";
    public static final String BASKET_ELEMENT = "basketElement";
    public static final String BASKET_TRADING_PARAM = "tradingParam";
    public static final String CALENDAR = "calendar_new";
    public static final String PRICING_FACTOR = "pricingFactor";
    public static final String ORDER_LIMIT = "orderLimitPerItem";
    public static final String ORDER_LIMIT_RAW = "orderLimitRaw";
    public static final String ORDER_DEFER_LIMIT = "orderDeferLimit";
    public static final String BOOK_SUMMARY_LIVE = "bookSummaryLive";
    public static final String BOOK_MASTER = "bookMaster";
    public static final String BOOK_ITEM_CLOSE = "bookItemClose";
    public static final String BOOK_LIVE = "bookItemLive";
    public static final String TRANSFER_BOOK_ITEM_CLOSE = "transferBookItemClose";
    public static final String UNDERLYING_MERGED = "underlyingMerged";
    public static final String POS_TODAY = "pos";
    public static final String POS_HISTORY = "posHistory";
    public static final String WORKING_ORDER = "wo";
    public static final String EQUITY_CONTRACT_LIMIT = "equityContractLimit";
    public static final String DERIV_CONTRACT_LIMIT = "derivContractLimit";
    public static final String EQUITY_CONTRACT = "stockContract";
    public static final String DERIV_CONTRACT = "derivContract";
    public static final String LIVE_EQUITY_BALANCE = "liveEquityBalance";
    public static final String LIVE_DERIV_BALANCE = "liveDerivBalance";
    public static final String LIVE_BOOK_BALANCE = "liveBookBalance";
    public static final String VIRTUAL_EQUITY_BALANCE = "virtualEquityBalance";
    public static final String VIRTUAL_DERIV_BALANCE = "virtualDerivBalance";
    public static final String QUOTING_PURPOSE_DIC = "quotingPurposeDic";
    public static final String IOC_ORDER_LIMIT = "iocOrderLimit";
    public static final String FEED_CONF = "feedConf";
    public static final String FEED_CONF_RAW = "feedConfRaw";
    public static final String SO_DUTY = "soDuty";
    public static final String ISINCODE_TO_KOREAN = "isinCodeToKorean";

    public static final String ELW_INFO = "elwInfo";
    public static final String ELW_EXTRA_INFO = "elwExtraInfo";

    public static final String UPLOAD_SCHEMA = "uploadSchema";

    public static final String CANARY_ORDER_DATA = "canaryOrderData";

    public static final String MARKET_SHARE = "marketShare";

    public static final String TRANSFER_INFO = "transferInfo";

    public static final String TRANSFER_HISTORY = "transferHistory";

    public static final String STOCK_LP = "stockLP";
    public static final String HISTORICAL_VOL = "historicalVol";

    public static final String OPTION_CLOSING = "option";
    public static final String FUTURES_CLOSING = "futures";
    public static final String EQUITY_CLOSING = "equity";
    public static final String EQUITY_CLOSING_EXTENDED = "equityExtended";
    public static final String INDEX_CLOSING = "index";

    public static final String OPTION_ATM = "atm";

    public static final String PDF = "pdf";

    public static final String ETF_BATCH = "etfBatch";
    public static final String ETF_LP = "etfLP";

    public static final String HEDGE_ORDER_MATCHING_INFO = "matchingInfo";

    public static final String SERVER_MSG = "serverMsg";

    public static final String CUSTOM_INFO = "customInfo";

    public static final String HEDGE_INFO = "hedgeInfo";
    public static final String HEDGE_DETAILS = "hedgeDetails";

    public static final String DECORATION_INFO = "decorationInfo";
    public static final String DUT_LIST = "dutList";
    public static final String ETF_NEUTRAL_POSITION = "etfNeutralPosition";
    public static final String ETF_EXCHANGE_HISTORY = "etfExchangeHistory";
    public static final String G2_ACC_TRADING_VOLUME = "g2AccTradingVolume";

    public static final String BAS = "bidAskStack";
    public static final String CONTRACT_APPLY = "contractApply";
    public static final String APPLY_CONTRACT_INFO = "applyContractInfo";

    public static final String LIVE_LIMIT_PRICE = "liveLimitPrice";

    public static final String DERIV_INFO = "futuresInfo";
    public static final String EQUITY_INFO = "equityInfo";

    public static final String KRX_NOTICE = "krxNotice";
    public static final String FILTERED_KRX_NOTICE = "filteredKrxNotice";

    public static final String VIRTUAL_CONTRACT = "manualContract";

    public static final String SO_PAYOFF = "soPayOff";

    public static final String SPREADER_ORDER_DATA = "spreaderOrderData";

    public static final String CONTRACT_BREAKDOWN_PARAMETER = "contractBreakdownParameter";
    public static final String CUSTOM_CONTRACT_BREAKDOWN_PARAMETER = "customContractBreakdownParameter";
    public static final String CONTRACT_BREAKDOWN_SINGLE_FUTURES = "contractBreakdownSingleFuturesParameter";

    public static final String ACCOUNT_BALANCE = "accountBalance";
    public static final String ACCOUNT_MASTER = "accountMaster";
    public static final String ACCOUNT_MARGIN_INFO = "accountMargin";

    public static final String CURRENT_BID_ASK = "currBidAsk";

    public static final String FUT_TRADE_BY_INVESTOR = "선물투자자별";
    public static final String OPT_TRADE_BY_INVESTOR = "옵션투자자별";
    public static final String PG_TRADE_BY_INVESTOR = "프로그램매매투자자별";
    public static final String ISIN_ALIAS = "isinAlias";

    public static final String MARKET_GREEKS = "marketGreeks";
    public static final String CUSTOM_SF_INDEX = "customSFIndex";
    public static final String CUSTOM_SF_INDEX_INFO = "customSFIndexInfo";
    public static final String CUSTOM_SF_INDEX_START_POINT = "customSFIndexStartPoint";
    public static final String INDEX_SUPPLEMENT = "indexSupplement";

    public static final String STOCK_OPTION_TO_MONITOR = "soToMonitor";
    public static final String STOCK_ACCOUNT_BALANCE = "stockAccountBalance";
    public static final String DERIV_ACCOUNT_BALANCE = "derivAccountBalance";
    public static final String FUTURES_DUTY_AMOUNT = "주식선물의무수량";
    public static final String OPTION_DUTY_AMOUNT = "주식옵션의무종목";

    public static final String FUTURES_DUTY_RULES = "LP의무규칙_선물";
    public static final String OPTION_DUTY_RULES = "LP의무규칙_옵션";

    public static final String TS_MINUTE = "tsMin";
    public static final String TS_ELW_BASIS_MINUTE = "tsElwBasisMin";
    public static final String TS_MARKET_VOL_MINUTE = "tsMarketVolMin";
    public static final String TS_MARKET_VOL_LAST = "tsMarketVolLast";

    public static final String OPTION_VOL = "option_vol";
    public static final String INDEX_OPTION_VOL = "indexOption_vol";
    public static final String MARKET_VOL = "market_vol";
    public static final String QUOTE_VOL = "quoteVol";

    public static final String STOCK_REINFORCEMENT = "stockLendingBorrowing";
    public static final String CODE_ACC_SERVER_ALLOCATION = "codeAccServerAlloc";

    public static final String ELW_MONITORING = "elw";
    public static final String CONTRACT_PROBABILITY_MONITORING = "contractProbability";

    public static final String PRICE_TRACKING_RESULT = "priceTrackingResult";
}
