package bulls.feed.current.data.cmtyFuture;

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
import bulls.feed.current.parser.kospiFuture.선물_종목정보;
import bulls.feed.current.parser.kospiOption.Option_종목정보;
import bulls.staticData.FuturesInfo;
import bulls.staticData.FuturesInfoCenter;
import bulls.staticData.ProdType.DerivativesProdClassType;
import bulls.staticData.ProdType.DerivativesUnderlyingMarketType;
import bulls.staticData.ProdType.DerivativesUnderlyingType;
import bulls.staticData.TempConf;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import java.sql.Timestamp;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.Date;

public class 상품선물_종목정보Feed extends Feed implements DBData, 종목정보Feed, SQLDBDataConvertible {

    DBObject ob = new BasicDBObject();

    public 상품선물_종목정보Feed(FeedTRCode trCode, byte[] packet) {
        super(trCode, packet);

        //한글이 있는 피드의 경우 line.substring 사용시 잘못 파싱될 우려가 있으니 parseStr(rawPacket)을 사용
        //한글이 없는 경우라면 byte를 String으로 변환 후 substring 을 사용하는게 더 효율적임(byte->String 연산이 substring보다 무거움)
//        String line = new String(rawPacket);

        String trCodeStr = 선물_종목정보.trCode.parser().parseStr(rawPacket, "trCode");
        String isinCode = 선물_종목정보.isinCode.parser().parseStr(rawPacket, "isinCode");

        String productName = getProdName();
        String underlyingIsinCode = new String(선물_종목정보.underlyingIsinCode.parser().parseByte(rawPacket));
        if (underlyingIsinCode.equals("KR49999999KP"))
            underlyingIsinCode = "KRD020020016";
        else if (underlyingIsinCode.equals("KRD020021397"))
            underlyingIsinCode = "KRD020021394";
        double 기준가 = 선물_종목정보.기준가.parser().parseDoubleInsertDot(rawPacket, 3);

        int 잔존일수 = 선물_종목정보.잔존일수.parser().parseInt(rawPacket);
        double 전일종가 = 선물_종목정보.전일종가.parser().parseFloatWithLeadingSign(rawPacket, 3);
        double 전일시가 = 선물_종목정보.전일시가.parser().parseFloatWithLeadingSign(rawPacket, 3);
        double 전일고가 = 선물_종목정보.전일고가.parser().parseFloatWithLeadingSign(rawPacket, 3);
        double 전일저가 = 선물_종목정보.전일저가.parser().parseFloatWithLeadingSign(rawPacket, 3);

        double 가격제한1단계상한가 = 선물_종목정보.가격제한1단계상한가.parser().parseFloatWithLeadingSign(rawPacket, 3);
        double 가격제한1단계하한가 = 선물_종목정보.가격제한1단계하한가.parser().parseFloatWithLeadingSign(rawPacket, 3);
        double 가격제한2단계상한가 = 선물_종목정보.가격제한2단계상한가.parser().parseFloatWithLeadingSign(rawPacket, 3);
        double 가격제한2단계하한가 = 선물_종목정보.가격제한2단계하한가.parser().parseFloatWithLeadingSign(rawPacket, 3);
        double 가격제한3단계상한가 = 선물_종목정보.가격제한3단계상한가.parser().parseFloatWithLeadingSign(rawPacket, 3);
        double 가격제한3단계하한가 = 선물_종목정보.가격제한3단계하한가.parser().parseFloatWithLeadingSign(rawPacket, 3);

        int 만기일자 = 선물_종목정보.만기일자.parser().parseInt(rawPacket);
        String 기초자산ID = 선물_종목정보.기초자산ID.parser().parseStr(rawPacket, "");
        String 소속상품군 = "";//SF/SO에서만 유효한 field
        String 기초자산시장ID = 선물_종목정보.기초자산시장ID.parser().parseStr(rawPacket, "");
        long 미결제한도수량 = 선물_종목정보.미결제한도수량.parser().parseLong(rawPacket);
        long 전일거래수량 = 선물_종목정보.전일거래수량.parser().parseLong(rawPacket);
        long 전일거래대금 = 선물_종목정보.전일거래대금.parser().parseLong(rawPacket);
        long 전일미결제약정수량 = 선물_종목정보.전일미결제약정수량.parser().parseLong(rawPacket);

        // 무위험지표금리선물이 아니면 priceDivider를 100으로 설정
        var dut = DerivativesUnderlyingType.getTypeFromDutCode(기초자산ID);
        int priceDivider;
        if (dut == DerivativesUnderlyingType.RFR)
            priceDivider = (int) Math.pow(10, rfrPriceDecimalPoint);
        else
            priceDivider = (int) Math.pow(10, priceDecimalPoint);


        double CD금리;
        //지수선물의 겨우 CD값이 들어오지만 섹터 선물의 경우는 Filler이기 때문에 예외 처리 필요
        try {
            if (선물_종목정보.CD금리.parser().parseStr(rawPacket, "").trim().equals(""))
                CD금리 = 0;
            else
                CD금리 = 선물_종목정보.CD금리.parser().parseDoubleInsertDot(rawPacket, 3);
        } catch (NumberFormatException nfe) {
            CD금리 = 0;
        }
        double 거래단위 = 선물_종목정보.거래단위.parser().parseDoubleInsertDot(rawPacket, 8);
        double 거래승수 = 선물_종목정보.거래승수.parser().parseDoubleInsertDot(rawPacket, 8);
        Integer ATM구분 = 선물_종목정보.ATM구분코드.parser().parseInt(rawPacket);


        String spreadRecentIsin = new String(선물_종목정보.spreadRecentCode.parser().parseByte(rawPacket));
        String spreadNext = new String(선물_종목정보.spreadNextCode.parser().parseByte(rawPacket));

        ob.put("isinCode", isinCode);
        ob.put("기준가", 기준가);

        ob.put("type", trCodeStr);
        if (productName != null)
            ob.put("productName", productName);

        ob.put("underlyingIsinCode", underlyingIsinCode);

        ob.put("spreadRecentIsin", spreadRecentIsin);
        ob.put("spreadNext", spreadNext);
        ob.put("date", TimeCenter.Instance.getDateAsDateType());

        ob.put("matDate", 만기일자);
        // parsing 가능하면 date type 으로 만기를 추가해주자
        Date 만기 = null;
        try {
            만기 = DateCenter.Instance.parse_yyyyMMdd(만기일자 + "");
            ob.put("만기", 만기);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        ob.put("priceDivider", priceDivider);
        ob.put("기초자산ID", 기초자산ID);
        ob.put("소속상품군", 소속상품군);
        ob.put("기초자산시장ID", 기초자산시장ID);
        ob.put("미결제한도수량", 미결제한도수량);
        ob.put("전일거래수량", 전일거래수량);
        ob.put("전일거래대금", 전일거래대금);
        ob.put("전일미결제약정수량", 전일미결제약정수량);
        ob.put("CD금리", CD금리);
        ob.put("거래단위", 거래단위);
        ob.put("multiplier", 거래승수);
        ob.put("ATM구분", ATM구분);

        ob.put("잔존일수", 잔존일수);
        ob.put("전일종가", 전일종가);
        ob.put("전일고가", 전일고가);
        ob.put("전일시가", 전일시가);
        ob.put("전일저가", 전일저가);

        ob.put("가격제한1단계상한가", 가격제한1단계상한가);
        ob.put("가격제한1단계하한가", 가격제한1단계하한가);
        ob.put("가격제한2단계상한가", 가격제한2단계상한가);
        ob.put("가격제한2단계하한가", 가격제한2단계하한가);
        ob.put("가격제한3단계상한가", 가격제한3단계상한가);
        ob.put("가격제한3단계하한가", 가격제한3단계하한가);

        ob.put("date", TimeCenter.Instance.getDateAsDateType());
        ob.put("lastUpdate", new Date());
        //DefaultLogger.logger.debug("선물 종목 정보 업데이트 : {}" ,  ob.toString());

        if(TempConf.UPDATE_FROM_FEED) {
            FuturesInfo futuresInfo = new FuturesInfo();
            futuresInfo.isinCode = isinCode;
            futuresInfo.productName = productName;
            futuresInfo.underlyingIsinCode = underlyingIsinCode;
            futuresInfo.기준가 = 기준가;
            futuresInfo.type = trCodeStr;
            futuresInfo.date = TimeCenter.Instance.getDateAsDateType();
            futuresInfo.만기 = 만기;
            futuresInfo.matDate = String.valueOf(만기일자);
            futuresInfo.기초자산ID = DerivativesUnderlyingType.getTypeFromDutCode(기초자산ID);
            futuresInfo.prodClassType = DerivativesProdClassType.getTypeFromCode(futuresInfo.기초자산ID);
            futuresInfo.기초자산시장ID = DerivativesUnderlyingMarketType.getTypeFromCode(기초자산시장ID); //기초자산시장ID;
            futuresInfo.CD금리 = CD금리;
            futuresInfo.거래단위 = 거래단위;
            futuresInfo.multiplier = 거래승수;
            futuresInfo.ATM구분 = ATM구분;
            futuresInfo.priceDivider = priceDivider;
            futuresInfo.spreadRecentIsin = spreadRecentIsin;
            futuresInfo.spreadNext = spreadNext;
            futuresInfo.가격제한1단계상한가 = 가격제한1단계상한가;
            futuresInfo.가격제한1단계하한가 = 가격제한1단계하한가;
            //        매칭 정보 없음
            //        futuresInfo.strikePrice = strikePrice;
            //        futuresInfo.결제주 = 결제주;
            FuturesInfoCenter.Instance.updateFromFeed(futuresInfo);
        }

    }

    // 가격은 9자리 (정수 6, 소수 3)로 파싱
    // 하지만 무위험지표금리 선물을 제외하면 실제로 의미있는 자리수는 8자리 (정수 6, 소수 2)이고
    // 주문을 낼 때도 가격은 소수점 둘째자리까지 설정해서 내므로 기본 priceDivider를 100으로 설정한다.
    private static final int priceDecimalPoint = 2;
    private static final int rfrPriceDecimalPoint = 3;

    @Override
    public void updateBidAskFill(BidAskFillUpdater dc) {
        // batch
    }

    @Override
    public DBObject toDBObject() {
        if (this.ob.get("isinCode").equals("999999999999"))
            return null;
        return this.ob;
    }

    @Override
    public DBObject query() {
        String line = new String(rawPacket);

        String isinCode = 선물_종목정보.isinCode.parser().parseStr(line);

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
        return MongoDBCollectionName.DERIV_INFO;
    }

    @Override
    public String getRepresentingCode() {
        return new String(선물_종목정보.isinCode.parser().parseByte(rawPacket));
    }

    @Override
    public String getProdName() {
        return 선물_종목정보.productName.parser().parseStr(rawPacket, getRepresentingCode()).trim();
    }

    @Override
    public SQLDBData[] getSQLDBDataArray() {
        try {
            SQLDBData data1 = new SQLDBData();
            String isinCode = getRepresentingCode();
            if (isinCode.equals("999999999999"))
                return null;
            String code = 선물_종목정보.code.parser().parseStr(rawPacket, "");
            String productId = 선물_종목정보.productId.parser().parseStr(rawPacket, "").trim();
            Long 기준가 = 선물_종목정보.기준가.parser().parseLong(rawPacket);
            String productName = 선물_종목정보.productName.parser().parseStr(rawPacket, "").trim();
            Integer 결제월번호 = 선물_종목정보.결제월번호.parser().parseInt(rawPacket);
            String 만기일자str = Option_종목정보.matDate.parser().parseStr(rawPacket, "");
            LocalDate 만기일자 = DateCenter.Instance.parse_yyyyMMdd_toLocalDate(만기일자str);
            Double 거래승수 = 선물_종목정보.거래승수.parser().parseDoubleInsertDot(rawPacket, 8);
            String underlyingIsinCode = 선물_종목정보.underlyingIsinCode.parser().parseStr(rawPacket, "");
            Long underlyingClosingPrice = 선물_종목정보.underlyingClosingPrice.parser().parseLong(rawPacket);
            String 기초자산ID = 선물_종목정보.기초자산ID.parser().parseStr(rawPacket, "");
            String 소속상품군 = 선물_종목정보.소속상품군.parser().parseStr(rawPacket, "");
            String 기초자산시장ID = 선물_종목정보.기초자산시장ID.parser().parseStr(rawPacket, "");
            Long 미결제한도수량 = 선물_종목정보.미결제한도수량.parser().parseLong(rawPacket);
            Long 전일거래수량 = 선물_종목정보.전일거래수량.parser().parseLong(rawPacket);
            Long 전일거래대금 = 선물_종목정보.전일거래대금.parser().parseLong(rawPacket);
            String CD금리str = 선물_종목정보.CD금리.parser().parseStr(rawPacket).trim();
            Double CD금리 = null;
            if (!CD금리str.isEmpty())
                CD금리 = Double.parseDouble(CD금리str) / 1000.0;
            Integer 전일미결제약정수량 = 선물_종목정보.전일미결제약정수량.parser().parseInt(rawPacket);
            Double 거래단위 = 선물_종목정보.거래단위.parser().parseDoubleInsertDot(rawPacket, 8);
            Double ATMPrice = 선물_종목정보.ATMPrice.parser().parseFloatWithLeadingSign(rawPacket, 3);
            Integer 잔존일수 = 선물_종목정보.잔존일수.parser().parseInt(rawPacket);
            Double 전일종가 = 선물_종목정보.전일종가.parser().parseFloatWithLeadingSign(rawPacket, 3);
            Double 전일시가 = 선물_종목정보.전일시가.parser().parseFloatWithLeadingSign(rawPacket, 3);
            Double 전일고가 = 선물_종목정보.전일고가.parser().parseFloatWithLeadingSign(rawPacket, 3);
            Double 전일저가 = 선물_종목정보.전일저가.parser().parseFloatWithLeadingSign(rawPacket, 3);
            String spreadRecentCode = 선물_종목정보.spreadRecentCode.parser().parseStr(rawPacket, "").trim();
            String spreadNextCode = 선물_종목정보.spreadNextCode.parser().parseStr(rawPacket, "").trim();
            Character ATM구분코드 = (char) 선물_종목정보.ATM구분코드.parser().parseSingleByte(rawPacket);
            Double strikePrice = null;
            String strikePriceDisp = null;
            Timestamp ts = new Timestamp(System.currentTimeMillis());

            data1.sql = "INSERT INTO drv_master " +
                    "(isincode,shortcode,product_id,korean,matdate,underlying_isincode,underlying_id,prod_class_detail_type,underlying_mkt_id,open_int_limit,spread_recent,spread_next,strike_price,strike_price_disp,price_decimal_point,last_update) " +
                    " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)" +
                    "ON CONFLICT (isincode) " +
                    "DO UPDATE SET shortcode=excluded.shortcode,product_id=excluded.product_id,korean=excluded.korean,matdate=excluded.matdate,underlying_isincode=excluded.underlying_isincode,underlying_id=excluded.underlying_id,prod_class_detail_type=excluded.prod_class_detail_type,underlying_mkt_id=excluded.underlying_mkt_id,open_int_limit=excluded.open_int_limit,spread_recent=excluded.spread_recent,spread_next=excluded.spread_next,strike_price=excluded.strike_price,strike_price_disp=excluded.strike_price_disp,price_decimal_point=excluded.price_decimal_point,last_update=excluded.last_update";

            data1.values = new Object[]{isinCode, code, productId, productName, 만기일자, underlyingIsinCode, 기초자산ID, 소속상품군, 기초자산시장ID, 미결제한도수량, spreadRecentCode, spreadNextCode, strikePrice, strikePriceDisp, priceDecimalPoint, ts};


            LocalDate dt = TimeCenter.Instance.today;
            SQLDBData data2 = new SQLDBData();
            data2.sql = "INSERT INTO drv_master_history( dt, isincode,base_price,mat_month_order,multiplier,underlying_close_price,prev_trd_volume,prev_trd_money,cd_rate,prev_open_int,trd_unit,atm_price,remain_day,prev_close,prev_open,prev_high,prev_low,atm_type)" +
                    " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)" +
                    "ON CONFLICT (dt,isincode) " +
                    "DO UPDATE SET base_price=excluded.base_price,mat_month_order=excluded.mat_month_order,multiplier=excluded.multiplier,underlying_close_price=excluded.underlying_close_price,prev_trd_volume=excluded.prev_trd_volume,prev_trd_money=excluded.prev_trd_money,cd_rate=excluded.cd_rate,prev_open_int=excluded.prev_open_int,trd_unit=excluded.trd_unit,atm_price=excluded.atm_price,remain_day=excluded.remain_day,prev_close=excluded.prev_close,prev_open=excluded.prev_open,prev_high=excluded.prev_high,prev_low=excluded.prev_low,atm_type=excluded.atm_type";
            data2.values = new Object[]{dt, isinCode, 기준가, 결제월번호, 거래승수, underlyingClosingPrice, 전일거래수량, 전일거래대금, CD금리, 전일미결제약정수량, 거래단위, ATMPrice, 잔존일수, 전일종가, 전일시가, 전일고가, 전일저가, ATM구분코드};

            return new SQLDBData[]{
                    data1, data2
            };
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}