package bulls.feed.current.data.equity;

import bulls.dateTime.DateCenter;
import bulls.dateTime.TimeCenter;
import bulls.db.influxdb.InfluxDBData;
import bulls.db.mongodb.DBData;
import bulls.db.mongodb.MongoDBCollectionName;
import bulls.db.mongodb.MongoDBDBName;
import bulls.db.rdb.SQLDBData;
import bulls.db.rdb.SQLDBDataConvertible;
import bulls.feed.abstraction.BidAskFillUpdater;
import bulls.feed.abstraction.Feed;
import bulls.feed.abstraction.종목정보Feed;
import bulls.feed.current.enums.FeedTRCode;
import bulls.feed.current.parser.equity.Equity_종목정보;
import bulls.log.DefaultLogger;
import bulls.staticData.EquityInfo;
import bulls.staticData.EquityInfoCenter;
import bulls.staticData.TempConf;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Equity_종목정보Feed extends Feed implements DBData, 종목정보Feed, SQLDBDataConvertible, InfluxDBData {
    private static final int priceDecimalPoint = 0;

    DBObject ob = new BasicDBObject();

    public Equity_종목정보Feed(FeedTRCode trCode, byte[] packet) {
        super(trCode, packet);

        String line = new String(rawPacket);

        String trCodeStr = Equity_종목정보.trCode.parser().parseStr(line);
        String isinCode = getRepresentingCode();
        String shortCode = Equity_종목정보.shortCode.parser().parseStr(rawPacket, isinCode).trim();
        String productName = getProdName().trim();
        String 거래정지여부 = new String(Equity_종목정보.거래정지여부.parser().parseByte(rawPacket));
        String 관리종목여부 = new String(Equity_종목정보.관리종목여부.parser().parseByte(rawPacket));
        String 증권그룹ID = new String(Equity_종목정보.증권그룹ID.parser().parseByte(rawPacket));
        long 상한가 = Equity_종목정보.상한가.parser().parseLong(rawPacket);
        long 하한가 = Equity_종목정보.하한가.parser().parseLong(rawPacket);
        long 기준가격 = Equity_종목정보.기준가격.parser().parseLong(rawPacket);
        String 전일종가구분코드 = new String(Equity_종목정보.전일종가구분코드.parser().parseByte(rawPacket));
        long 전일종가 = Equity_종목정보.전일종가.parser().parseLong(rawPacket);
        String KOSPI여부;
        int 시장구분 = Equity_종목정보.시장구분.parser().parseInt(rawPacket);
        if (시장구분 == 1)
            KOSPI여부 = "Y";
        else
            KOSPI여부 = "N";
//        String KOSPI여부 = new String(Equity_종목정보.KOSPI여부.parser().parseByte(rawPacket));
        long 전일누적체결수량 = Equity_종목정보.전일누적체결수량.parser().parseLong(rawPacket);
        long 전일누적거래대금 = Equity_종목정보.전일누적거래대금.parser().parseLong(rawPacket);
        long 상장주식수 = Equity_종목정보.상장주식수.parser().parseLong(rawPacket);
        String KOSPI200섹터업종 = new String(Equity_종목정보.KOSPI200섹터업종.parser().parseByte(rawPacket));
        Long 상한수량 = Equity_종목정보.상한수량.parser().parseLong(rawPacket);

        String ETP상품구분코드 = Equity_종목정보.ETP상품구분코드.parser().parseStr(rawPacket, "").trim();
        String 지수시장분류ID = Equity_종목정보.지수시장분류ID.parser().parseStr(rawPacket, "").trim();
        String 추적지수레버리지인버스구분코드 = Equity_종목정보.추적지수레버리지인버스구분코드.parser().parseStr(rawPacket, "").trim();
        String 참조지수레버리지인버스구분코드 = Equity_종목정보.참조지수레버리지인버스구분코드.parser().parseStr(rawPacket, "").trim();
        String 지수자산분류ID1 = Equity_종목정보.지수자산분류ID1.parser().parseStr(rawPacket, "").trim();
        String 지수자산분류ID2 = Equity_종목정보.지수자산분류ID2.parser().parseStr(rawPacket, "").trim();
        String KOSDAQ150지수종목여부 = Equity_종목정보.KOSDAQ150지수종목여부.parser().parseStr(rawPacket, "").trim();
        String 저유동성여부 = Equity_종목정보.저유동성여부.parser().parseStr(rawPacket, "").trim();
        String KRX300지수여부 = Equity_종목정보.KRX300지수여부.parser().parseStr(rawPacket, "").trim();

        double 전일시총 = 전일종가 * 상장주식수;

        DBObject ob = new BasicDBObject();
        ob.put("isinCode", isinCode);
        ob.put("shortCode", shortCode);
        ob.put("priceDivider", (int) Math.pow(10, priceDecimalPoint));
        ob.put("type", trCode);
        if (productName != null)
            ob.put("productName", productName);
        ob.put("KOSPI여부", KOSPI여부);
        ob.put("거래정지여부", 거래정지여부);
        ob.put("관리종목여부", 관리종목여부);
        ob.put("증권그룹ID", 증권그룹ID);
        ob.put("기준가격", 기준가격);
        ob.put("상한가", 상한가);
        ob.put("하한가", 하한가);
        ob.put("전일종가구분코드", 전일종가구분코드);
        ob.put("KOSPI200섹터업종", KOSPI200섹터업종);
        ob.put("상장주식수", 상장주식수);
        ob.put("전일종가", 전일종가);
        ob.put("전일누적체결수량", 전일누적체결수량);
        ob.put("전일누적거래대금", 전일누적거래대금);
        ob.put("전일시총", 전일시총);
        ob.put("상한수량", 상한수량);
        ob.put("ETP상품구분코드", ETP상품구분코드);
        ob.put("지수시장분류ID", 지수시장분류ID);
        ob.put("추적지수레버리지인버스구분코드", 추적지수레버리지인버스구분코드);
        ob.put("참조지수레버리지인버스구분코드", 참조지수레버리지인버스구분코드);
        ob.put("지수자산분류ID1", 지수자산분류ID1);
        ob.put("지수자산분류ID2", 지수자산분류ID2);
        ob.put("KOSDAQ150지수종목여부", KOSDAQ150지수종목여부);
        ob.put("저유동성여부", 저유동성여부);
        ob.put("KRX300지수여부", KRX300지수여부);


        if (증권그룹ID.equals("EW")) {
            //ELW의 경우에만 종료일 및 행사일 처리
            try {
                int ELW행사종료일 = Equity_종목정보.ELW행사종료일.parser().parseInt(rawPacket);
                double ELW행사가격 = Equity_종목정보.ELW행사가격.parser().parseLong(rawPacket) / 1000.0;
                ob.put("ELW행사종료일", ELW행사종료일);
                ob.put("ELW행사가격", ELW행사가격);
            } catch (NumberFormatException nfe) {
                DefaultLogger.logger.error(nfe.toString());
            }
        }

        ob.put("date", TimeCenter.Instance.getDateAsDateType());
        ob.put("lastUpdate", new Date());

        //DefaultLogger.logger.debug("유가증권 종목 정보 업데이트 : {}" ,  ob.toString());
        if (TempConf.UPDATE_FROM_FEED) {
            EquityInfo equityInfo = new EquityInfo();
            equityInfo.date = TimeCenter.Instance.getDateAsDateType();
            equityInfo.isinCode = isinCode;
            equityInfo.shortCode = shortCode;
            equityInfo.type = trCodeStr;
            equityInfo.productName = productName;
            equityInfo.KOSPI여부 = KOSPI여부;
            equityInfo.거래정지여부 = 거래정지여부;
            equityInfo.관리종목여부 = 관리종목여부;
            equityInfo.증권그룹ID = 증권그룹ID;
            equityInfo.기준가격 = 기준가격;
            equityInfo.상한가 = 상한가;
            equityInfo.하한가 = 하한가;
            equityInfo.전일종가구분코드 = 전일종가구분코드;
            equityInfo.KOSPI200섹터업종 = KOSPI200섹터업종;
            equityInfo.상장주식수 = 상장주식수;
            equityInfo.전일종가 = 전일종가;
            equityInfo.전일누적체결수량 = 전일누적체결수량;
            equityInfo.전일누적거래대금 = 전일누적거래대금;
            equityInfo.전일시총 = (long) 전일시총;
            //        매칭 정보 없음
            //        equityInfo.ELW행사종료일 = ELW행사종료일;
            //        equityInfo.ELW행사가격 = ELW행사가격;
            EquityInfoCenter.Instance.updateFromFeed(equityInfo);
        }

    }

    @Override
    public DBObject toDBObject() {
        return this.ob;
    }

    @Override
    public DBObject query() {
        String line = new String(rawPacket);

        String isinCode = Equity_종목정보.isinCode.parser().parseStr(line);
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
        return MongoDBCollectionName.EQUITY_INFO;
    }

    @Override
    public void updateBidAskFill(BidAskFillUpdater dc) {

    }

    @Override
    public String getRepresentingCode() {
        return new String(Equity_종목정보.isinCode.parser().parseByte(rawPacket));
    }

    @Override
    public String getProdName() {
        try {
            return new String(Equity_종목정보.productName.parser().parseByte(rawPacket), "EUC-KR").trim();
        } catch (UnsupportedEncodingException e) {
            return getRepresentingCode();
        }

    }

    @Override
    public SQLDBData[] getSQLDBDataArray() {
        SQLDBData data1 = new SQLDBData();
        String isinCode = getRepresentingCode();
        if (isinCode.equals("999999999999"))
            return null;
        String shortCode = Equity_종목정보.shortCode.parser().parseStr(rawPacket, "");
        String productName = Equity_종목정보.productName.parser().parseStr(rawPacket, "").trim();
        String 증권그룹ID = Equity_종목정보.증권그룹ID.parser().parseStr(rawPacket, "");
        char 관리종목여부 = (char) Equity_종목정보.관리종목여부.parser().parseSingleByte(rawPacket);
        char 거래정지여부 = (char) Equity_종목정보.거래정지여부.parser().parseSingleByte(rawPacket);
        char KOSPI200섹터업종 = (char) Equity_종목정보.KOSPI200섹터업종.parser().parseSingleByte(rawPacket);
        char 시가총액규모코드 = (char) Equity_종목정보.시가총액규모코드.parser().parseSingleByte(rawPacket);
        char KOSPI여부 = (char) Equity_종목정보.KOSPI여부.parser().parseSingleByte(rawPacket);
        int 기준가격 = Equity_종목정보.기준가격.parser().parseInt(rawPacket);
        char 전일종가구분코드 = (char) Equity_종목정보.전일종가구분코드.parser().parseSingleByte(rawPacket);
        int 전일종가 = Equity_종목정보.전일종가.parser().parseInt(rawPacket);
        long 전일누적체결수량 = Equity_종목정보.전일누적체결수량.parser().parseLong(rawPacket);
        long 전일누적거래대금 = Equity_종목정보.전일누적거래대금.parser().parseLong(rawPacket);
        int 상한가 = Equity_종목정보.상한가.parser().parseInt(rawPacket);
        int 하한가 = Equity_종목정보.하한가.parser().parseInt(rawPacket);
        int 대용가격 = Equity_종목정보.대용가격.parser().parseInt(rawPacket);
        long 액면가 = Equity_종목정보.액면가.parser().parseLong(rawPacket);
        int 발행가격 = Equity_종목정보.발행가격.parser().parseInt(rawPacket);
        String 상장일자str = Equity_종목정보.상장일자.parser().parseStr(rawPacket, "");
        LocalDate 상장일자 = DateCenter.Instance.parse_yyyyMMdd_toLocalDate(상장일자str);
        long 상장주식수 = Equity_종목정보.상장주식수.parser().parseLong(rawPacket);
        String ELW행사종료일str = Equity_종목정보.ELW행사종료일.parser().parseStr(rawPacket, "");
        LocalDate ELW행사종료일;
        if (ELW행사종료일str.trim().isEmpty())
            ELW행사종료일 = null;
        else
            ELW행사종료일 = DateCenter.Instance.parse_yyyyMMdd_toLocalDate(ELW행사종료일str);

        Long ELW행사가격 = Equity_종목정보.ELW행사가격.parser().parseLong(rawPacket);
        char ETF복제방법구분코드 = (char) Equity_종목정보.ETF복제방법구분코드.parser().parseSingleByte(rawPacket);
        char ETP상품구분코드 = (char) Equity_종목정보.ETP상품구분코드.parser().parseSingleByte(rawPacket);
        String 추적지수레버리지인버스구분코드 = Equity_종목정보.추적지수레버리지인버스구분코드.parser().parseStr(rawPacket, "").trim();
        String 지수자산분류ID1 = Equity_종목정보.지수자산분류ID1.parser().parseStr(rawPacket, "").trim();
        String 지수자산분류ID2 = Equity_종목정보.지수자산분류ID2.parser().parseStr(rawPacket, "").trim();
        char KOSDAQ150지수종목여부 = (char) Equity_종목정보.KOSDAQ150지수종목여부.parser().parseSingleByte(rawPacket);
        char 저유동성여부 = (char) Equity_종목정보.저유동성여부.parser().parseSingleByte(rawPacket);
        char KRX300지수여부 = (char) Equity_종목정보.KRX300지수여부.parser().parseSingleByte(rawPacket);
        long 상한수량 = Equity_종목정보.상한수량.parser().parseLong(rawPacket);
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        data1.sql = "INSERT INTO eqt_master (isincode,shortcode,korean,sec_group_id,kospi200_sector_code,is_index_comp,upper_limit_price,lower_limit_price,par_price,issue_price,listed_date,exercise_end_date,strike_price,etf_repl_method_code,etp_prod_code,tracking_idx_lev_inv_code,idx_asset_class_id1,idx_asset_class_id2,upper_limit_qty,last_update) " +
                " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)" +
                "ON CONFLICT (isincode) " +
                "DO UPDATE SET shortcode=excluded.shortcode, korean=excluded.korean, sec_group_id=excluded.sec_group_id, kospi200_sector_code=excluded.kospi200_sector_code, is_index_comp=excluded.is_index_comp, upper_limit_price=excluded.upper_limit_price, lower_limit_price=excluded.lower_limit_price, par_price=excluded.par_price, issue_price=excluded.issue_price, listed_date=excluded.listed_date, exercise_end_date=excluded.exercise_end_date,  strike_price=excluded.strike_price, etf_repl_method_code=excluded.etf_repl_method_code, etp_prod_code=excluded.etp_prod_code, tracking_idx_lev_inv_code=excluded.tracking_idx_lev_inv_code, idx_asset_class_id1=excluded.idx_asset_class_id1, idx_asset_class_id2=excluded.idx_asset_class_id2, upper_limit_qty=excluded.upper_limit_qty, last_update=excluded.last_update";

        data1.values = new Object[]{isinCode, shortCode, productName, 증권그룹ID, KOSPI200섹터업종, KOSPI여부, 상한가, 하한가, 액면가, 발행가격, 상장일자, ELW행사종료일, ELW행사가격, ETF복제방법구분코드, ETP상품구분코드, 추적지수레버리지인버스구분코드, 지수자산분류ID1, 지수자산분류ID2, 상한수량, ts};

        LocalDate dt = TimeCenter.Instance.today;
        SQLDBData data2 = new SQLDBData();
        data2.sql = "INSERT INTO eqt_master_history( dt, isincode,is_under_admin,is_halted,market_cap_code,base_price,prev_close_price_type,prev_close_price,prev_trd_volume,prev_trd_money,sub_price,listed_stock_qty,is_kosdaq150_comp,is_low_liquidity,is_krx300_comp)" +
                " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)" +
                "ON CONFLICT (dt,isincode) " +
                "DO UPDATE SET is_under_admin=excluded.is_under_admin,is_halted=excluded.is_halted,market_cap_code=excluded.market_cap_code,base_price=excluded.base_price,prev_close_price_type=excluded.prev_close_price_type,prev_close_price=excluded.prev_close_price,prev_trd_volume=excluded.prev_trd_volume,prev_trd_money=excluded.prev_trd_money,sub_price=excluded.sub_price,listed_stock_qty=excluded.listed_stock_qty,is_kosdaq150_comp=excluded.is_kosdaq150_comp,is_low_liquidity=excluded.is_low_liquidity,is_krx300_comp=excluded.is_krx300_comp";
        data2.values = new Object[]{dt, isinCode, 관리종목여부, 거래정지여부, 시가총액규모코드, 기준가격, 전일종가구분코드, 전일종가, 전일누적체결수량, 전일누적거래대금, 대용가격, 상장주식수, KOSDAQ150지수종목여부, 저유동성여부, KRX300지수여부};

        return new SQLDBData[]{
                data1, data2
        };
    }

    @Override
    public List<Point> toPoints() {
        return Collections.singletonList(Point
                .measurement("equityInfo")
                .addTag("isinCode", getRepresentingCode())
                .addField("기준가격", Equity_종목정보.기준가격.parser().parseLong(rawPacket))
                .addField("전일종가", Equity_종목정보.전일종가.parser().parseLong(rawPacket))
                .addField("전일누적체결수량", Equity_종목정보.전일누적체결수량.parser().parseLong(rawPacket))
                .time(TimeCenter.Instance.getLocalDateTimeFromFeedTimestamp(arrivalStamp).atZone(ZoneId.systemDefault()).truncatedTo(ChronoUnit.DAYS).toInstant(), WritePrecision.NS));
    }
}
