package bulls.feed.current.data.etf;

import bulls.dateTime.DateCenter;
import bulls.dateTime.TimeCenter;
import bulls.db.mongodb.DBData;
import bulls.db.mongodb.MongoDBCollectionName;
import bulls.db.mongodb.MongoDBDBName;
import bulls.db.rdb.SQLDBData;
import bulls.db.rdb.SQLDBDataConvertible;
import bulls.feed.abstraction.BidAskFillUpdater;
import bulls.feed.abstraction.Feed;
import bulls.feed.abstraction.종목정보Feed;
import bulls.feed.current.enums.FeedTRCode;
import bulls.feed.current.parser.etf.ELW_종목정보;
import bulls.staticData.ELW.ELWExtraInfo;
import bulls.staticData.ELW.ELWExtraInfoCenter;
import bulls.staticData.PredefinedIsinCode;
import bulls.staticData.TempConf;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Date;

public class ELW_종목정보Feed extends Feed implements DBData, 종목정보Feed, SQLDBDataConvertible {

    DBObject ob = new BasicDBObject();

    public ELW_종목정보Feed(FeedTRCode trCode, byte[] packet) {
        super(trCode, packet);

        String line = new String(rawPacket);

        String trCodeStr = ELW_종목정보.trCode.parser().parseStr(line);
        String isinCode = ELW_종목정보.isinCode.parser().parseStr(line);
        String code = getRepresentingCode();


        String 발행사;
        String 만기평가방식;
        try {
            발행사 = new String(ELW_종목정보.발행사.parser().parseByte(rawPacket), "EUC-KR").trim();
            만기평가방식 = new String(ELW_종목정보.만기평가방식.parser().parseByte(rawPacket), "EUC-KR").trim();
        } catch (UnsupportedEncodingException e) {
            발행사 = "";
            만기평가방식 = "";
        }
        String 기초자산1 = new String(ELW_종목정보.기초자산1.parser().parseByte(rawPacket)).trim();
        String 콜풋 = new String(ELW_종목정보.콜풋.parser().parseByte(rawPacket));
        int 최종거래일자 = ELW_종목정보.최종거래일자.parser().parseInt(rawPacket);
        double 전환비율 = ELW_종목정보.전환비율.parser().parseLong(rawPacket) / 1000000;
        long LP보유수량 = ELW_종목정보.LP보유수량.parser().parseLong(rawPacket);
        String ELW지수업종코드 = ELW_종목정보.ELW지수업종코드.parser().parseStr(rawPacket, "ELW지수업종코드");
        String 지수소속시장구분코드 = ELW_종목정보.지수소속시장구분코드.parser().parseStr(rawPacket, "지수소속시장구분코드");

        if (기초자산1.equals("")) {
            if (지수소속시장구분코드.equals("1") && ELW지수업종코드.equals("029"))
                기초자산1 = PredefinedIsinCode.KOSPI_200;
            else if (지수소속시장구분코드.equals("2") && ELW지수업종코드.equals("203"))
                기초자산1 = PredefinedIsinCode.KOSDAQ_150;
            else
                기초자산1 = "UNKNOWN_" + 지수소속시장구분코드 + "_" + ELW지수업종코드;
        }

        DBObject ob = new BasicDBObject();
        ob.put("isinCode", isinCode);

        ob.put("type", trCodeStr);

        ob.put("발행사", 발행사);
        ob.put("기초자산1", 기초자산1);
        ob.put("콜풋", 콜풋);
        ob.put("최종거래일자", 최종거래일자);
        ob.put("전환비율", 전환비율);
        ob.put("만기평가방식", 만기평가방식);
        ob.put("LP보유수량", LP보유수량);
        ob.put("date", TimeCenter.Instance.getDateAsDateType());
        ob.put("lastUpdate", new Date());
        //DefaultLogger.logger.debug("ELW 종목 정보 업데이트 : {}" ,  ob.toString());

        if(TempConf.UPDATE_FROM_FEED) {
            ELWExtraInfo elwExtraInfo = new ELWExtraInfo();
            elwExtraInfo.isinCode = isinCode;
            elwExtraInfo.type = trCodeStr;
            elwExtraInfo.발행사 = 발행사;
            elwExtraInfo.기초자산1 = 기초자산1;
            elwExtraInfo.콜풋 = 콜풋;
            elwExtraInfo.최종거래일자 = 최종거래일자;
            elwExtraInfo.전환비율 = 전환비율;
            elwExtraInfo.만기평가방식 = 만기평가방식;
            elwExtraInfo.LP보유수량 = LP보유수량;
            elwExtraInfo.date = TimeCenter.Instance.getDateAsDateType();
            ELWExtraInfoCenter.Instance.updateFromFeed(elwExtraInfo);
        }
    }

    @Override
    public DBObject toDBObject() {
        return this.ob;
    }

    @Override
    public DBObject query() {
        String line = new String(rawPacket);

        String isinCode = ELW_종목정보.isinCode.parser().parseStr(line);
        DBObject ob = new BasicDBObject();
        ob.put("isinCode", isinCode);
        return ob;
    }

    @Override
    public String getDBName() {
        return MongoDBDBName.BATCH;
    }

    @Override
    public String getCollectionName() {
        return MongoDBCollectionName.ELW_EXTRA_INFO;
    }

    @Override
    public void updateBidAskFill(BidAskFillUpdater dc) {

    }

    @Override
    public String getRepresentingCode() {
        return new String(ELW_종목정보.isinCode.parser().parseByte(rawPacket));
    }

    @Override
    public String getProdName() {
        return ELW_종목정보.isinCode.parser().parseStr(rawPacket, getRepresentingCode());
    }

    @Override
    public SQLDBData[] getSQLDBDataArray() {
        SQLDBData data1 = new SQLDBData();
        String isinCode = getRepresentingCode();
        if (isinCode.equals("999999999999"))
            return null;
        String issuerCode = ELW_종목정보.발행사코드.parser().parseStr(rawPacket, "");
        String issuerName = ELW_종목정보.발행사.parser().parseStr(rawPacket, issuerCode).trim();
        String underlying1 = ELW_종목정보.기초자산1.parser().parseStr(rawPacket, "");
        char index_market_code = (char) ELW_종목정보.지수소속시장구분코드.parser().parseSingleByte(rawPacket);
        String elw_index_market_type_code = ELW_종목정보.ELW지수업종코드.parser().parseStr(rawPacket, "999");
        char callput = (char) ELW_종목정보.콜풋.parser().parseSingleByte(rawPacket);
        String lastTrdDateStr = ELW_종목정보.최종거래일자.parser().parseStr(rawPacket, "");
//        DefaultLogger.logger.info("Converting {} to LocalDate",lastTrdDateStr);
        LocalDate lastTrdDate = DateCenter.Instance.parse_yyyyMMdd_toLocalDate(lastTrdDateStr);
        float conv_rate = ELW_종목정보.전환비율.parser().parseInt(rawPacket) / 1000000.0f;
        String pricing_method_at_maturity = ELW_종목정보.만기평가방식.parser().parseStr(rawPacket, "").trim();
        long lp_holding_qty = ELW_종목정보.LP보유수량.parser().parseLong(rawPacket);
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        data1.sql = "INSERT INTO elw_master(  isincode,issuer_code,issuer_name,underlying1 ,index_market_code ,elw_index_market_type_code ,callput ,last_tradable_date ,  conv_rate ,pricing_method_at_maturity,last_update )" +
                " VALUES(?,?,?,?,?,?,?,?,?,?,?)" +
                "ON CONFLICT (isincode) " +
                "DO UPDATE SET issuer_code=excluded.issuer_code,issuer_name=excluded.issuer_name,underlying1=excluded.underlying1,index_market_code=excluded.index_market_code,elw_index_market_type_code=excluded.elw_index_market_type_code,callput=excluded.callput,last_tradable_date=excluded.last_tradable_date,conv_rate=excluded.conv_rate,pricing_method_at_maturity=excluded.pricing_method_at_maturity,last_update=excluded.last_update";
        data1.values = new Object[]{isinCode, issuerCode, issuerName, underlying1, index_market_code, elw_index_market_type_code, callput, lastTrdDate, conv_rate, pricing_method_at_maturity, ts};

        LocalDate dt = TimeCenter.Instance.today;
        SQLDBData data2 = new SQLDBData();
        data2.sql = "INSERT INTO elw_master_history( dt, isincode,lp_holding_qty)" +
                " VALUES(?,?,?)" +
                "ON CONFLICT (dt,isincode) " +
                "DO UPDATE SET lp_holding_qty=excluded.lp_holding_qty";
        data2.values = new Object[]{dt, isinCode, lp_holding_qty};

        return new SQLDBData[]{
                data1, data2
        };
    }
}
