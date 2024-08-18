package bulls.feed.current.data.kosdaqFuture;

import bulls.data.bidAsk.BidAskCore;
import bulls.data.bidAsk.BidAskFactory;
import bulls.dateTime.TimeCenter;
import bulls.db.mongodb.DBData;
import bulls.db.mongodb.MongoDBCollectionName;
import bulls.db.mongodb.MongoDBDBName;
import bulls.feed.abstraction.BidAskFillUpdater;
import bulls.feed.abstraction.Feed;
import bulls.feed.current.enums.FeedTRCode;
import bulls.feed.current.parser.kosdaqFuture.KQFutures_종가;
import bulls.staticData.TempConf;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import java.util.Date;

public class KQFutures_종가Feed extends Feed implements DBData {

    public KQFutures_종가Feed(FeedTRCode trCode, byte[] packet) {
        super(trCode, packet);
    }

    @Override
    public void updateBidAskFill(BidAskFillUpdater dc) {
        BidAskCore ba = getFullBidAsk(dc.getBidAskFactory());
        try {
            dc.updateDerivBidAskMap(ba.isinCode, ba);
        } catch (Exception e) {
            System.out.println(e);
        }

    }

    public byte[] getCodeByte() {
        return KQFutures_종가.isinCode.parser().parseByte(rawPacket);
    }

    public String getCode() {
        return new String(getCodeByte());
    }


    public Integer getPrice() {
        return KQFutures_종가.price.parser().parseIntWithLeadingSign(rawPacket);
    }

    public Integer getAskPrice() {
        return KQFutures_종가.askPrice.parser().parseIntWithLeadingSign(rawPacket);
    }

    public Integer getAskAmount() {
        return KQFutures_종가.askAmount.parser().parseInt(rawPacket);
    }

    public Integer getBidPrice() {
        return KQFutures_종가.bidPrice.parser().parseIntWithLeadingSign(rawPacket);
    }

    public Integer getBidAmount() {
        return KQFutures_종가.bidAmount.parser().parseInt(rawPacket);
    }

    public Integer getTotalContractAmount() {
        return KQFutures_종가.totalContractAmount.parser().parseInt(rawPacket);
    }

    public Long getTotalContractPrice() {
        return KQFutures_종가.totalContractPrice.parser().parseLong(rawPacket) * 1000;
    }


    @Override
    public String getDBName() {
        return MongoDBDBName.CLOSING_PRICE;
    }

    @Override
    public String getCollectionName() {
        return MongoDBCollectionName.FUTURES_CLOSING;
    }

    @Override
    public DBObject toDBObject() {

        String line = new String(rawPacket);

        String isinCode = KQFutures_종가.isinCode.parser().parseStr(line);
//        String priceStr =  KQFutures_종가.price.parser().parseStr(line) ;
//        if ( KQFutures_종가.priceSign.parser().parseStr(line).equals("-"))
//            priceStr = "-" + priceStr;
//
//        Integer price = null;
//        try {
//            price = Integer.parseInt(priceStr);
//        } catch (NumberFormatException e) {
//            DefaultLogger.logger.error("선물 종가 파싱 실패 [{}]", priceStr);
//        }
        Integer price = KQFutures_종가.price.parser().parseIntWithLeadingSign(rawPacket);
        DBObject ob = new BasicDBObject();
        ob.put("isinCode", isinCode);
        if (price != null) {
            ob.put("price", price);
        }
        Integer totalContractAmount = KQFutures_종가.totalContractAmount.parser().parseInt(rawPacket);
        Long totalContractPrice = KQFutures_종가.totalContractPrice.parser().parseLong(rawPacket) * 1000;
        if (totalContractAmount != null)
            ob.put("totalContractAmount", totalContractAmount);
        if (totalContractPrice != null)
            ob.put("totalContractPrice", totalContractPrice);
//        {date : {$gt : ISODate("2015-10-18T07:54:46.800Z") }})
//        ob.put("date", new Date(System.currentTimeMillis() / TempConf.DAY_IN_MILSEC  * TempConf.DAY_IN_MILSEC ));

        ob.put("date", TimeCenter.Instance.getDateAsDateType());
        ob.put("lastUpdate", new Date());
        return ob;
    }

    @Override
    public DBObject query() {
        String line = new String(rawPacket);

        String isinCode = KQFutures_종가.isinCode.parser().parseStr(line);

        DBObject ob = new BasicDBObject();
        ob.put("isinCode", isinCode);
        if (TempConf.FEED_TO_DB_SAVE_HISTORY)
            ob.put("date", TimeCenter.Instance.getDateAsDateType());
        return ob;
    }

    public BidAskCore getFullBidAsk(BidAskFactory factory) {
        int size = 13;
        BidAskCore fullBidAsk = factory.getBidAsk(DERIV_MAX_BIDASK_DEPTH);
        fullBidAsk.isinCode = getCode();

        for (int i = 0; i < DERIV_MAX_BIDASK_DEPTH; i++) {
            fullBidAsk.askAmount[i] = KQFutures_종가.askAmount.plus(i * size).parseInt(rawPacket);
            if (fullBidAsk.askAmount[i] == 0)
                break;
            fullBidAsk.askPrice[i] = KQFutures_종가.askPrice.plus(i * size).parseInt(rawPacket);
        }
        for (int i = 0; i < DERIV_MAX_BIDASK_DEPTH; i++) {
            fullBidAsk.bidAmount[i] = KQFutures_종가.bidAmount.plus(i * size).parseInt(rawPacket);
            if (fullBidAsk.bidAmount[i] == 0)
                break;
            fullBidAsk.bidPrice[i] = KQFutures_종가.bidPrice.plus(i * size).parseInt(rawPacket);
        }
        fullBidAsk.updateBidAskState();
        return fullBidAsk;
    }
}
