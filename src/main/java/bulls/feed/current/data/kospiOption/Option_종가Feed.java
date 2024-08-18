package bulls.feed.current.data.kospiOption;

import bulls.data.PriceInfo;
import bulls.data.bidAsk.BidAskCore;
import bulls.data.bidAsk.BidAskFactory;
import bulls.dateTime.TimeCenter;
import bulls.db.mongodb.DBData;
import bulls.db.mongodb.MongoDBCollectionName;
import bulls.db.mongodb.MongoDBDBName;
import bulls.feed.abstraction.BidAskFillUpdater;
import bulls.feed.abstraction.Feed;
import bulls.feed.current.enums.FeedTRCode;
import bulls.feed.current.parser.kospiOption.Option_종가;
import bulls.staticData.TempConf;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import java.util.Date;

public class Option_종가Feed extends Feed implements DBData {

    public Option_종가Feed(FeedTRCode trCode, byte[] packet) {
        super(trCode, packet);
    }

    public byte[] getCodeByte() {
        return Option_종가.isinCode.parser().parseByte(rawPacket);
    }

    public String getCode() {
        return new String(getCodeByte());
    }

    public Integer getPrice() {
        return Option_종가.price.parser().parseInt(rawPacket);
    }

    public Integer getAskPrice() {
        return Option_종가.askPrice.parser().parseInt(rawPacket);
    }

    public Integer getAskAmount() {
        return Option_종가.askAmount.parser().parseInt(rawPacket);
    }

    public Integer getBidPrice() {
        return Option_종가.bidPrice.parser().parseInt(rawPacket);
    }

    public Integer getBidAmount() {
        return Option_종가.bidAmount.parser().parseInt(rawPacket);
    }

    public Integer getTotalContractAmount() {
        return Option_종가.totalContractAmount.parser().parseInt(rawPacket);
    }

    public Long getTotalContractPrice() {
        return Option_종가.totalContractPrice.parser().parseLong(rawPacket) * 1000;
    }


    @Override
    public void updateBidAskFill(BidAskFillUpdater dc) {
        BidAskCore ba = getFullBidAsk(dc.getBidAskFactory());
        dc.updateDerivBidAskMap(ba.isinCode, ba);

        PriceInfo info = new PriceInfo();
        info.feedStamp = arrivalStamp;
        info.isinCode = ba.isinCode;
        info.price = getPrice();
        info.totalPrice = getTotalContractPrice();
        info.totalAmount = getTotalContractAmount();
        // 종가의 체결 수량은 일단 1로 한다.
        info.amount = 1;
        dc.updatePriceInfo(info);
    }

    @Override
    public String getDBName() {
        return MongoDBDBName.CLOSING_PRICE;
    }

    @Override
    public String getCollectionName() {
        return MongoDBCollectionName.OPTION_CLOSING;
    }

    @Override
    public DBObject toDBObject() {

        String line = new String(rawPacket);

        // changed from exture+
        // leave data only when boardId == G1 (정규장)
        String isinCode = Option_종가.isinCode.parser().parseStr(line);
        int price = Option_종가.price.parser().parseInt(line);

        DBObject ob = new BasicDBObject();
        ob.put("isinCode", isinCode);
        ob.put("price", price);

        Integer totalContractAmount = Option_종가.totalContractAmount.parser().parseInt(rawPacket);
        Long totalContractPrice = Option_종가.totalContractPrice.parser().parseLong(rawPacket) * 1000;
        if (totalContractAmount != null)
            ob.put("totalContractAmount", totalContractAmount);
        if (totalContractPrice != null)
            ob.put("totalContractPrice", totalContractPrice);

        ob.put("date", TimeCenter.Instance.getDateAsDateType());
        ob.put("lastUpdate", new Date());
        return ob;
    }

    @Override
    public DBObject query() {
        String line = new String(rawPacket);
        String isinCode = Option_종가.isinCode.parser().parseStr(line);

        DBObject ob = new BasicDBObject();
        ob.put("isinCode", isinCode);
        if (TempConf.FEED_TO_DB_SAVE_HISTORY)
            ob.put("date", TimeCenter.Instance.getDateAsDateType());
        return ob;
    }

    public BidAskCore getFullBidAsk(BidAskFactory factory) {
//        매수1단계부호	X	1
//        매수1단계우선호가가격	9	5
//        매수1단계우선호가잔량	9	6
//        1 + 5 + 6 = 12
        int size = 12;
        BidAskCore fullBidAsk = factory.getBidAsk(DERIV_MAX_BIDASK_DEPTH);
        fullBidAsk.isinCode = getCode();
        for (int i = 0; i < DERIV_MAX_BIDASK_DEPTH; i++) {
            fullBidAsk.askAmount[i] = Option_종가.askAmount.plus(i * size).parseInt(rawPacket);
            if (fullBidAsk.askAmount[i] == 0)
                break;
            fullBidAsk.askPrice[i] = Option_종가.askPrice.plus(i * size).parseInt(rawPacket);
        }
        for (int i = 0; i < DERIV_MAX_BIDASK_DEPTH; i++) {
            fullBidAsk.bidAmount[i] = Option_종가.bidAmount.plus(i * size).parseInt(rawPacket);
            if (fullBidAsk.bidAmount[i] == 0)
                break;
            fullBidAsk.bidPrice[i] = Option_종가.bidPrice.plus(i * size).parseInt(rawPacket);
        }
        fullBidAsk.updateBidAskState();
        return fullBidAsk;
    }
}
