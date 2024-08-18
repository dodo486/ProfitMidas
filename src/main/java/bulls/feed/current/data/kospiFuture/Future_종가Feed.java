package bulls.feed.current.data.kospiFuture;

import bulls.data.bidAsk.BidAskCore;
import bulls.data.bidAsk.BidAskFactory;
import bulls.dateTime.TimeCenter;
import bulls.db.mongodb.DBData;
import bulls.db.mongodb.MongoDBCollectionName;
import bulls.db.mongodb.MongoDBDBName;
import bulls.feed.abstraction.BidAskFillUpdater;
import bulls.feed.abstraction.Feed;
import bulls.feed.current.enums.FeedTRCode;
import bulls.feed.current.parser.kospiFuture.Future_종가;
import bulls.staticData.TempConf;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import java.util.Date;

public class Future_종가Feed extends Feed implements DBData {

    public Future_종가Feed(FeedTRCode trCode, byte[] packet) {
        super(trCode, packet);
    }

    @Override
    public void updateBidAskFill(BidAskFillUpdater dc) {
        BidAskCore ba = getFullBidAsk(dc.getBidAskFactory());
        dc.updateDerivBidAskMap(ba.isinCode, ba);
    }

    public byte[] getCodeByte() {
        return Future_종가.isinCode.parser().parseByte(rawPacket);
    }

    public String getCode() {
        return new String(getCodeByte());
    }


    public Integer getPrice() {
        return Future_종가.price.parser().parseIntWithLeadingSign(rawPacket);
    }

    public Integer getAskPrice() {
        return Future_종가.askPrice.parser().parseIntWithLeadingSign(rawPacket);
    }

    public Integer getAskAmount() {
        return Future_종가.askAmount.parser().parseInt(rawPacket);
    }

    public Integer getBidPrice() {
        return Future_종가.bidPrice.parser().parseIntWithLeadingSign(rawPacket);
    }

    public Integer getBidAmount() {
        return Future_종가.bidAmount.parser().parseInt(rawPacket);
    }

    public Integer getTotalContractAmount() {
        return Future_종가.totalContractAmount.parser().parseInt(rawPacket);
    }

    public Long getTotalContractPrice() {
        return Future_종가.totalContractPrice.parser().parseLong(rawPacket) * 1000;
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

        String isinCode = Future_종가.isinCode.parser().parseStr(line);
//        String priceStr =  Future_종가.price.parser().parseStr(line) ;
//        if ( Future_종가.priceSign.parser().parseStr(line).equals("-"))
//            priceStr = "-" + priceStr;
//
//        Integer price = null;
//        try {
//            price = Integer.parseInt(priceStr);
//        } catch (NumberFormatException e) {
//            DefaultLogger.logger.error("선물 종가 파싱 실패 [{}]", priceStr);
//        }
        Integer price = Future_종가.price.parser().parseIntWithLeadingSign(rawPacket);
        DBObject ob = new BasicDBObject();
        ob.put("isinCode", isinCode);
        if (price != null) {
            ob.put("price", price);
        }
        Integer totalContractAmount = Future_종가.totalContractAmount.parser().parseInt(rawPacket);
        Long totalContractPrice = Future_종가.totalContractPrice.parser().parseLong(rawPacket) * 1000;
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

        String isinCode = Future_종가.isinCode.parser().parseStr(line);

        DBObject ob = new BasicDBObject();
        ob.put("isinCode", isinCode);
        if (TempConf.FEED_TO_DB_SAVE_HISTORY)
            ob.put("date", TimeCenter.Instance.getDateAsDateType());
        return ob;
    }

    public BidAskCore getFullBidAsk(BidAskFactory factory) {
        try {
            int size = 12;
            BidAskCore fullBidAsk = factory.getBidAsk(DERIV_MAX_BIDASK_DEPTH);
            fullBidAsk.isinCode = getCode();
            for (int i = 0; i < DERIV_MAX_BIDASK_DEPTH; i++) {
                fullBidAsk.askAmount[i] = Future_종가.askAmount.plus(i * size).parseInt(rawPacket);
                if (fullBidAsk.askAmount[i] == 0)
                    break;
                fullBidAsk.askPrice[i] = Future_종가.askPrice.plus(i * size).parseIntWithLeadingSign(rawPacket);
            }
            for (int i = 0; i < DERIV_MAX_BIDASK_DEPTH; i++) {
                fullBidAsk.bidAmount[i] = Future_종가.bidAmount.plus(i * size).parseInt(rawPacket);
                if (fullBidAsk.bidAmount[i] == 0)
                    break;
                fullBidAsk.bidPrice[i] = Future_종가.bidPrice.plus(i * size).parseIntWithLeadingSign(rawPacket);
            }
            fullBidAsk.updateBidAskState();
            return fullBidAsk;
        } catch (Exception e) {
            System.out.println("dgdgd");
            return null;
        }
    }
}
