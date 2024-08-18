package bulls.feed.next.data.statistics;

import bulls.dateTime.TimeCenter;
import bulls.db.mongodb.DBData;
import bulls.db.mongodb.MongoDBCollectionName;
import bulls.db.mongodb.MongoDBDBName;
import bulls.db.rdb.SQLDBData;
import bulls.db.rdb.SQLDBDataConvertible;
import bulls.feed.abstraction.BidAskFillUpdater;
import bulls.feed.abstraction.Feed;
import bulls.feed.current.enums.FeedTRCode;
import bulls.feed.current.parser.statistics.시장통계_선물_투자자데이터;
import bulls.marketData.MarketDataKRXInvestorCode;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import java.time.LocalDateTime;

public class 시장통계_파생_투자자데이터Feed extends Feed implements DBData, SQLDBDataConvertible {

    public 시장통계_파생_투자자데이터Feed(FeedTRCode trCode, byte[] packet) {
        super(trCode, packet);
    }

    @Override
    public DBObject toDBObject() {
        String 데이터구분 = 시장통계_선물_투자자데이터.데이터구분.parser().parseStr(rawPacket, "");
        if (데이터구분.equals("99"))
            return null;
        String line = new String(rawPacket);
        String trCode = 시장통계_선물_투자자데이터.trCode.parser().parseStr(line);
        int 생성시각 = 시장통계_선물_투자자데이터.생성시각.parser().parseInt(rawPacket);
        String 상품ID = 시장통계_선물_투자자데이터.상품ID.parser().parseStr(line);
        String 투자자유형 = 시장통계_선물_투자자데이터.투자자유형.parser().parseStr(line);
        int 매수약정수량 = 시장통계_선물_투자자데이터.매수약정수량.parser().parseInt(rawPacket);
        int 매도약정수량 = 시장통계_선물_투자자데이터.매도약정수량.parser().parseInt(rawPacket);
        long 매수약정대금 = 시장통계_선물_투자자데이터.매수약정대금.parser().parseLong(rawPacket);
        long 매도약정대금 = 시장통계_선물_투자자데이터.매도약정대금.parser().parseLong(rawPacket);
        int 매수약정수량_SP = 시장통계_선물_투자자데이터.매수약정수량_SP.parser().parseInt(rawPacket);
        int 매도약정수량_SP = 시장통계_선물_투자자데이터.매도약정수량_SP.parser().parseInt(rawPacket);
        long 매수약정대금_SP = 시장통계_선물_투자자데이터.매수약정대금_SP.parser().parseLong(rawPacket);
        long 매도약정대금_SP = 시장통계_선물_투자자데이터.매도약정대금_SP.parser().parseLong(rawPacket);


        DBObject ob = new BasicDBObject();
        ob.put("date", TimeCenter.Instance.getDateAsDateType());
        ob.put("생성시각", 생성시각);
        ob.put("상품ID", 상품ID);
        ob.put("투자자유형", 투자자유형);
        ob.put("매수약정수량", 매수약정수량);
        ob.put("매도약정수량", 매도약정수량);
        ob.put("매수약정대금", 매수약정대금);
        ob.put("매도약정대금", 매도약정대금);
        ob.put("매수약정수량_SP", 매수약정수량_SP);
        ob.put("매도약정수량_SP", 매도약정수량_SP);
        ob.put("매수약정대금_SP", 매수약정대금_SP);
        ob.put("매도약정대금_SP", 매도약정대금_SP);

        //DefaultLogger.logger.debug("ELW 종목 정보 업데이트 : {}" ,  ob.toString());
        return ob;
    }

    @Override
    public DBObject query() {
        String line = new String(rawPacket);
        int 생성시각 = 시장통계_선물_투자자데이터.생성시각.parser().parseInt(rawPacket);
        String 상품ID = 시장통계_선물_투자자데이터.상품ID.parser().parseStr(line);
        String 투자자유형 = 시장통계_선물_투자자데이터.투자자유형.parser().parseStr(line);

        DBObject ob = new BasicDBObject();
        ob.put("date", TimeCenter.Instance.getDateAsDateType());
        ob.put("생성시각", 생성시각);
        ob.put("상품ID", 상품ID);
        ob.put("투자자유형", 투자자유형);
        return ob;
    }

    @Override
    public String getDBName() {
        return MongoDBDBName.MARKET_STAT;
    }

    @Override
    public String getCollectionName() {
        return MongoDBCollectionName.FUT_TRADE_BY_INVESTOR;
    }

    @Override
    public void updateBidAskFill(BidAskFillUpdater dc) {

    }

    @Override
    public SQLDBData[] getSQLDBDataArray() {
        String 데이터구분 = 시장통계_선물_투자자데이터.데이터구분.parser().parseStr(rawPacket, "");
        if (데이터구분.equals("99"))
            return null;
        int 생성날짜 = 시장통계_선물_투자자데이터.생성일자.parser().parseInt(rawPacket);
        int yyyy = 생성날짜 / 10000;
        int mm = (생성날짜 % 10000) / 100;
        int dd = (생성날짜 % 100);
        int 생성시각 = 시장통계_선물_투자자데이터.생성시각.parser().parseInt(rawPacket);
        int hour = 생성시각 / 10000;
        int min = (생성시각 % 10000) / 100;
        int sec = (생성시각 % 100);
        LocalDateTime dt = LocalDateTime.of(yyyy, mm, dd, hour, min, sec);
        String product_id = 시장통계_선물_투자자데이터.상품ID.parser().parseStr(rawPacket, "").trim();
        String investor_code = 시장통계_선물_투자자데이터.투자자유형.parser().parseStr(rawPacket, "");
        int 매수약정수량 = 시장통계_선물_투자자데이터.매수약정수량.parser().parseInt(rawPacket);
        int 매도약정수량 = 시장통계_선물_투자자데이터.매도약정수량.parser().parseInt(rawPacket);
        long 매수약정대금 = 시장통계_선물_투자자데이터.매수약정대금.parser().parseLong(rawPacket);
        long 매도약정대금 = 시장통계_선물_투자자데이터.매도약정대금.parser().parseLong(rawPacket);
        int 매수약정수량_SP = 시장통계_선물_투자자데이터.매수약정수량_SP.parser().parseInt(rawPacket);
        int 매도약정수량_SP = 시장통계_선물_투자자데이터.매도약정수량_SP.parser().parseInt(rawPacket);
        long 매수약정대금_SP = 시장통계_선물_투자자데이터.매수약정대금_SP.parser().parseLong(rawPacket);
        long 매도약정대금_SP = 시장통계_선물_투자자데이터.매도약정대금_SP.parser().parseLong(rawPacket);

        String sql = "INSERT INTO fut_investor_trading_stat(dt,product_id,investor_code,data_type,bid_vol,bid_money,ask_vol,ask_money,bid_vol_sp,bid_money_sp,ask_vol_sp,ask_money_sp) VALUES(?,?,?,?,?,?,?,?,?,?,?,?)\n" +
                "ON CONFLICT (dt,product_id,investor_code) " +
                "DO UPDATE SET data_type=excluded.data_type,bid_vol=excluded.bid_vol,bid_money=excluded.bid_money,ask_vol=excluded.ask_vol,ask_money=excluded.ask_money,bid_vol_sp=excluded.bid_vol_sp,bid_money_sp=excluded.bid_money_sp,ask_vol_sp=excluded.ask_vol_sp,ask_money_sp=excluded.ask_money_sp";


        MarketDataKRXInvestorCode code = MarketDataKRXInvestorCode.parse(investor_code);
        if (code != MarketDataKRXInvestorCode.UNKNOWN) {
            if (매수약정수량 == 0 && 매도약정수량 == 0)
                return null;
            SQLDBData data = new SQLDBData();
            data.sql = sql;
            data.values = new Object[]{
                    dt, product_id, investor_code, 데이터구분,
                    매수약정수량, 매수약정대금, 매도약정수량, 매도약정대금, 매수약정수량_SP, 매수약정대금_SP, 매도약정수량_SP, 매도약정대금_SP
            };
            return new SQLDBData[]{data};
        }
        return null;
    }

}
