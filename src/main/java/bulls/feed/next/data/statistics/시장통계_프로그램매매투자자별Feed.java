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
import bulls.feed.current.parser.statistics.시장통계_프로그램매매투자자별;
import bulls.marketData.MarketDataKRXInvestorCode;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import java.time.LocalDateTime;

public class 시장통계_프로그램매매투자자별Feed extends Feed implements DBData, SQLDBDataConvertible {

    public 시장통계_프로그램매매투자자별Feed(FeedTRCode trCode, byte[] packet) {
        super(trCode, packet);
    }

    @Override
    public DBObject toDBObject() {

        String line = new String(rawPacket);

        String trCode = 시장통계_프로그램매매투자자별.trCode.parser().parseStr(line);
        int 산출시각 = 시장통계_프로그램매매투자자별.산출시각.parser().parseInt(rawPacket);
        String 투자자구분코드 = 시장통계_프로그램매매투자자별.투자자구분코드.parser().parseStr(line);
        long 매도차익체결수량 = 시장통계_프로그램매매투자자별.매도차익체결수량.parser().parseLong(rawPacket);
        long 매도차익거래대금 = 시장통계_프로그램매매투자자별.매도차익거래대금.parser().parseLong(rawPacket);
        long 매도비차익체결수량 = 시장통계_프로그램매매투자자별.매도비차익체결수량.parser().parseLong(rawPacket);
        long 매도비차익거래대금 = 시장통계_프로그램매매투자자별.매도비차익거래대금.parser().parseLong(rawPacket);
        long 매수차익체결수량 = 시장통계_프로그램매매투자자별.매수차익체결수량.parser().parseLong(rawPacket);
        long 매수차익거래대금 = 시장통계_프로그램매매투자자별.매수차익거래대금.parser().parseLong(rawPacket);
        long 매수비차익체결수량 = 시장통계_프로그램매매투자자별.매수비차익체결수량.parser().parseLong(rawPacket);
        long 매수비차익거래대금 = 시장통계_프로그램매매투자자별.매수비차익거래대금.parser().parseLong(rawPacket);


        DBObject ob = new BasicDBObject();
        ob.put("trCode", trCode);
        ob.put("산출시각", 산출시각);
        ob.put("투자자구분코드", 투자자구분코드);
        ob.put("매도차익체결수량", 매도차익체결수량);
        ob.put("매도차익거래대금", 매도차익거래대금);
        ob.put("매도비차익체결수량", 매도비차익체결수량);
        ob.put("매도비차익거래대금", 매도비차익거래대금);
        ob.put("매수차익체결수량", 매수차익체결수량);
        ob.put("매수차익거래대금", 매수차익거래대금);
        ob.put("매수비차익체결수량", 매수비차익체결수량);
        ob.put("매수비차익거래대금", 매수비차익거래대금);

        ob.put("date", TimeCenter.Instance.getDateAsDateType());

        //DefaultLogger.logger.debug("ELW 종목 정보 업데이트 : {}" ,  ob.toString());
        return ob;
    }

    @Override
    public DBObject query() {
        String line = new String(rawPacket);
        String trCode = 시장통계_프로그램매매투자자별.trCode.parser().parseStr(line);
        int 산출시각 = 시장통계_프로그램매매투자자별.산출시각.parser().parseInt(rawPacket);
        String 투자자구분코드 = 시장통계_프로그램매매투자자별.투자자구분코드.parser().parseStr(line);

        DBObject ob = new BasicDBObject();
        ob.put("trCode", trCode);
        ob.put("산출시각", 산출시각);
        ob.put("투자자구분코드", 투자자구분코드);
        ob.put("date", TimeCenter.Instance.getDateAsDateType());
        return ob;
    }

    @Override
    public String getDBName() {
        return MongoDBDBName.MARKET_STAT;
    }

    @Override
    public String getCollectionName() {
        return MongoDBCollectionName.PG_TRADE_BY_INVESTOR;
    }

    @Override
    public void updateBidAskFill(BidAskFillUpdater dc) {

    }

    @Override
    public SQLDBData[] getSQLDBDataArray() {
        int 산출시각 = 시장통계_프로그램매매투자자별.산출시각.parser().parseInt(rawPacket);
        int hour = 산출시각 / 10000;
        int min = (산출시각 % 10000) / 100;
        int sec = (산출시각 % 100);
        LocalDateTime dt = TimeCenter.Instance.today.atTime(hour, min, sec);
        Character market_type = (char) 시장통계_프로그램매매투자자별.시장구분.parser().parseSingleByte(rawPacket);
        String investor_code = 시장통계_프로그램매매투자자별.투자자구분코드.parser().parseStr(rawPacket, "");
        Long 매도차익체결수량 = 시장통계_프로그램매매투자자별.매도차익체결수량.parser().parseLong(rawPacket);
        Long 매도차익거래대금 = 시장통계_프로그램매매투자자별.매도차익거래대금.parser().parseLong(rawPacket);
        Long 매도비차익체결수량 = 시장통계_프로그램매매투자자별.매도비차익체결수량.parser().parseLong(rawPacket);
        Long 매도비차익거래대금 = 시장통계_프로그램매매투자자별.매도비차익거래대금.parser().parseLong(rawPacket);
        Long 매수차익체결수량 = 시장통계_프로그램매매투자자별.매수차익체결수량.parser().parseLong(rawPacket);
        Long 매수차익거래대금 = 시장통계_프로그램매매투자자별.매수차익거래대금.parser().parseLong(rawPacket);
        Long 매수비차익체결수량 = 시장통계_프로그램매매투자자별.매수비차익체결수량.parser().parseLong(rawPacket);
        Long 매수비차익거래대금 = 시장통계_프로그램매매투자자별.매수비차익거래대금.parser().parseLong(rawPacket);

        String sql = "INSERT INTO eqt_investor_pg_trd_stat(dt,market_type,investor_code,매도차익체결수량,매도차익거래대금,매도비차익체결수량,매도비차익거래대금,매수차익체결수량,매수차익거래대금,매수비차익체결수량,매수비차익거래대금) " +
                "VALUES(?,?,?,?,?,?,?,?,?,?,?)\n" +
                "ON CONFLICT (dt,market_type,investor_code) " +
                "DO UPDATE SET 매도차익체결수량=excluded.매도차익체결수량,매도차익거래대금=excluded.매도차익거래대금,매도비차익체결수량=excluded.매도비차익체결수량,매도비차익거래대금=excluded.매도비차익거래대금,매수차익체결수량=excluded.매수차익체결수량,매수차익거래대금=excluded.매수차익거래대금,매수비차익체결수량=excluded.매수비차익체결수량,매수비차익거래대금=excluded.매수비차익거래대금";


        MarketDataKRXInvestorCode code = MarketDataKRXInvestorCode.parse(investor_code);
        if (code != MarketDataKRXInvestorCode.UNKNOWN) {
            if (매도차익체결수량 == 0 && 매도비차익체결수량 == 0 && 매수차익체결수량 == 0 && 매수비차익체결수량 == 0)
                return null;
            SQLDBData data = new SQLDBData();
            data.sql = sql;
            data.values = new Object[]{
                    dt, market_type, investor_code, 매도차익체결수량, 매도차익거래대금, 매도비차익체결수량, 매도비차익거래대금, 매수차익체결수량, 매수차익거래대금, 매수비차익체결수량, 매수비차익거래대금
            };
            return new SQLDBData[]{data};
        }
        return null;
    }
}
