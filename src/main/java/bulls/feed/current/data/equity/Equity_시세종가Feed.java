package bulls.feed.current.data.equity;

import bulls.data.PriceInfo;
import bulls.data.bidAsk.BidAskCore;
import bulls.dateTime.TimeCenter;
import bulls.db.mongodb.DBData;
import bulls.db.mongodb.MongoDBCollectionName;
import bulls.db.mongodb.MongoDBDBName;
import bulls.db.rdb.SQLDBData;
import bulls.db.rdb.SQLDBDataConvertible;
import bulls.feed.abstraction.BidAskFillUpdater;
import bulls.feed.abstraction.Feed;
import bulls.feed.current.enums.FeedTRCode;
import bulls.feed.current.parser.equity.Equity_시세종가;
import bulls.staticData.ProdType.ProdTypeCenter;
import bulls.staticData.TempConf;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import java.util.Date;

public class Equity_시세종가Feed extends Feed implements DBData, SQLDBDataConvertible {

    public Equity_시세종가Feed(FeedTRCode trCode, byte[] packet) {
        super(trCode, packet);
    }

    public byte[] getIsinByte() {
        return Equity_시세종가.isinCode.parser().parseByte(rawPacket);
    }

    public String getIsinCode() {
        return new String(getIsinByte());
    }

    public Integer getPrice() {
        return Equity_시세종가.현재가.parser().parseInt(rawPacket);
    }

    public Integer getHighPrice() {
        return Equity_시세종가.고가.parser().parseInt(rawPacket);
    }

    public Integer getLowPrice() {
        return Equity_시세종가.저가.parser().parseInt(rawPacket);
    }

    public Integer getOpenPrice() {
        return Equity_시세종가.시가.parser().parseInt(rawPacket);
    }

    public Long getAccQty() {
        return Equity_시세종가.누적체결수량.parser().parseLong(rawPacket);
    }

    public Long getAccAmt() {
        return Equity_시세종가.누적거래대금.parser().parseLong(rawPacket);
    }

    public boolean isHalted() {
        return Equity_시세종가.거래정지여부.parser().parseSingleByte(rawPacket) == (byte) 'Y';
    }

    public Long getRegularAccQty() {
        return Equity_시세종가.정규장체결수량.parser().parseLong(rawPacket);
    }

    public Long getRegularAccAmt() {
        return Equity_시세종가.정규장거래대금.parser().parseLong(rawPacket);
    }

    @Override
    public void updateBidAskFill(BidAskFillUpdater dc) {
        String code = getIsinCode();

        PriceInfo info = new PriceInfo();
        info.feedStamp = arrivalStamp;
        info.isinCode = code;
        info.price = getPrice();
        info.amount = 1;
        info.totalPrice = getAccAmt();
        info.totalAmount = getAccQty();
        dc.updatePriceInfo(info);

        BidAskCore ba = dc.getBidAskFactory().getBidAsk(ETC_MAX_BIDASK_DEPTH);
        ba.askPrice[0] = info.price;
        ba.bidPrice[0] = info.price;
        ba.askAmount[0] = 1;
        ba.bidAmount[0] = 1;
        ba.isinCode = code;
        ba.updateBidAskState();
        dc.updateEquityBidAskMap(code, ba);
    }

    @Override
    public DBObject toDBObject() {

        String line = new String(rawPacket);

        String boardId = Equity_시세종가.보드ID.parser().parseStr(line);

        // 시세종가 데이터는 G3, G4로 들어온다. 그런데 가끔 G1으로 들어오는 패킷도 있어서 이것도 잡아줘야 함.
        if (!boardId.equals("G1") && !boardId.equals("G3") && !boardId.equals("G4"))
            return null;

        String isinCode = Equity_시세종가.isinCode.parser().parseStr(line);
        Integer price = Equity_시세종가.현재가.parser().parseInt(line);


        DBObject ob = new BasicDBObject();
        ob.put("isinCode", isinCode);
        ob.put("price", price);

        Integer highPrice = Equity_시세종가.고가.parser().parseInt(rawPacket);
        Integer lowPrice = Equity_시세종가.저가.parser().parseInt(rawPacket);
        Integer openPrice = Equity_시세종가.시가.parser().parseInt(rawPacket);
        // DB에서 필요한 정보는 정규장 정보이므로 정규장에 대한 누적체결수량과 누적거래대금을 보냄
        Long regularContractAmount = Equity_시세종가.정규장체결수량.parser().parseLong(rawPacket);
        Long regularContractPrice = Equity_시세종가.정규장거래대금.parser().parseLong(rawPacket);

        Long totalContractAmount = Equity_시세종가.누적체결수량.parser().parseLong(rawPacket);
        Long totalContractPrice = Equity_시세종가.누적거래대금.parser().parseLong(rawPacket);

        if (highPrice != null)
            ob.put("highPrice", highPrice);
        if (lowPrice != null)
            ob.put("lowPrice", lowPrice);
        if (openPrice != null)
            ob.put("openPrice", openPrice);
        if (regularContractAmount != null)
            ob.put("regularContractAmount", regularContractAmount);
        if (regularContractPrice != null)
            ob.put("regularContractPrice", regularContractPrice);
        if (totalContractAmount != null)
            ob.put("totalContractAmount", totalContractAmount);
        if (totalContractPrice != null)
            ob.put("totalContractPrice", totalContractPrice);

        ob.put("date", TimeCenter.Instance.getDateAsDateType());
        ob.put("lastUpdate", new Date());
        return ob;
    }

    // IsinCode 가 겹치면 덮어쓴다.
    @Override
    public DBObject query() {
        String line = new String(rawPacket);

        // changed from exture+
        // leave data only when boardId == G1 (정규장)
        String boardId = Equity_시세종가.보드ID.parser().parseStr(line);

        // 시세종가 데이터는 G3, G4로 들어온다. 그런데 가끔 G1으로 들어오는 패킷도 있어서 이것도 잡아줘야 함.
        if (!boardId.equals("G1") && !boardId.equals("G3") && !boardId.equals("G4"))
            return null;

        String isinCode = Equity_시세종가.isinCode.parser().parseStr(line);

        DBObject ob = new BasicDBObject();
        ob.put("isinCode", isinCode);
        if (TempConf.FEED_TO_DB_SAVE_HISTORY)
            ob.put("date", TimeCenter.Instance.getDateAsDateType());
        return ob;
    }

    @Override
    public String getDBName() {
        return MongoDBDBName.CLOSING_PRICE;
    }

    @Override
    public String getCollectionName() {
        return MongoDBCollectionName.EQUITY_CLOSING_EXTENDED;
    }


    @Override
    public SQLDBData[] getSQLDBDataArray() {
        String code = getIsinCode();
        if (code.equals("999999999999"))
            return null;
        SQLDBData data = new SQLDBData();
        String table;
        if (ProdTypeCenter.Instance.getProdType(code).isEquityELW()) {
            table = "elw_basic_ts_day";
        } else {
            table = "eqt_basic_ts_day";
        }

        data.sql = "INSERT INTO " + table + "(t,code,o,h,l,c,accQty,accAmt,isHalted) VALUES(?,?,?,?,?,?,?,?,?)\n" +
                "ON CONFLICT (t,code) " +
                "DO UPDATE SET o=excluded.o,h=excluded.h,l=excluded.l,c=excluded.c,accQty=excluded.accQty,accAmt=excluded.accAmt,isHalted=excluded.isHalted";
        data.values = new Object[]{
                TimeCenter.Instance.today,
                code,
                getOpenPrice(),
                getHighPrice(),
                getLowPrice(),
                getPrice(),
                getRegularAccQty(),
                getRegularAccAmt(),
                isHalted()
        };
        return new SQLDBData[]{
                data
        };
    }
}
