package bulls.feed.current.data.equity;

import bulls.data.PriceInfo;
import bulls.data.bidAsk.BidAskCore;
import bulls.dateTime.TimeCenter;
import bulls.db.mongodb.DBData;
import bulls.db.mongodb.MongoDBCollectionName;
import bulls.db.mongodb.MongoDBDBName;
import bulls.feed.abstraction.BidAskFillUpdater;
import bulls.feed.abstraction.Feed;
import bulls.feed.current.enums.FeedTRCode;
import bulls.feed.current.parser.equity.Equity_종가;
import bulls.staticData.TempConf;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import java.util.Date;

public class Equity_종가Feed extends Feed implements DBData {


    public Equity_종가Feed(FeedTRCode trCode, byte[] packet) {
        super(trCode, packet);
    }

    public byte[] getIsinByte() {
        return Equity_종가.isinCode.parser().parseByte(rawPacket);
    }

    public String getIsinCode() {
        return new String(getIsinByte());
    }

    public Integer getPrice() {
        return Equity_종가.price.parser().parseInt(rawPacket);
    }

    @Override
    public void updateBidAskFill(BidAskFillUpdater dc) {
        String code = getIsinCode();

        PriceInfo info = new PriceInfo();
        info.feedStamp = arrivalStamp;
        info.isinCode = code;
        info.price = getPrice();
        info.amount = 1;
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

        String boardId = Equity_종가.boardId.parser().parseStr(line);

        if (!boardId.equals("G1"))
            return null;

        String isinCode = Equity_종가.isinCode.parser().parseStr(line);
        Integer price = Equity_종가.price.parser().parseInt(line);


        DBObject ob = new BasicDBObject();
        ob.put("isinCode", isinCode);
        ob.put("price", price);

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
        String boardId = Equity_종가.boardId.parser().parseStr(line);

        if (!boardId.equals("G1"))
            return null;

        String isinCode = Equity_종가.isinCode.parser().parseStr(line);

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
        return MongoDBCollectionName.EQUITY_CLOSING;
    }
}
