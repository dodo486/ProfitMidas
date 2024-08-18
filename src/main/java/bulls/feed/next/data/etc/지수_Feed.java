package bulls.feed.next.data.etc;

import bulls.data.bidAsk.BidAskCore;
import bulls.dateTime.TimeCenter;
import bulls.db.mongodb.DBData;
import bulls.db.mongodb.MongoDBCollectionName;
import bulls.db.mongodb.MongoDBDBName;
import bulls.feed.abstraction.BidAskFillUpdater;
import bulls.feed.abstraction.Feed;
import bulls.feed.current.enums.FeedTRCode;
import bulls.feed.current.parser.etc.지수;
import bulls.log.DefaultLogger;
import bulls.staticData.AliasManager;
import bulls.staticData.IndexCode;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import java.util.Date;

public class 지수_Feed extends Feed implements DBData {

    public static byte[] timeEnd = {'J', 'U', 'N', 'J', 'J', 'J'};

    public 지수_Feed(FeedTRCode trCode, byte[] packet) {
        super(trCode, packet);
    }

    // 지수의 경우는 단일 가격으로 나오지만 전략 적용의 편의를 위해 bid/ask 에 같은 단일 가격을 업데이트한다.
    // 수량은 만일의 경우의 null protection 과 divide by 0 을 막기 위해 일단 1로 셋팅.. 안쓸거다.. 쓰지 마라.
    // 거래량과 거래대금을 각각 bid수량1, ask수량1에 넣어주고 있음.
    @Override
    public void updateBidAskFill(BidAskFillUpdater dc) {
        BidAskCore ba = dc.getBidAskFactory().getBidAsk(1);
        ba.feedStamp = arrivalStamp;

        String code = getIndexCode();
        IndexCode ic = IndexCode.parseNormalPriceFeedTRWithIndustryCode(code);
        if (ic == null)
            return;

        ba.isinCode = ic.getIndexIsinCode();
        ba.askPrice[0] = getAskPrice();
        ba.bidPrice[0] = getBidPrice();
        ba.bidAmount[0] = getVolume();
        ba.askAmount[0] = getMoney();
        ba.updateBidAskState();
        dc.updateEquityBidAskMap(ba.isinCode, ba);
    }

    public String getIndexCode() {
        return new String(지수.indexCode.parser().parseByte(rawPacket));
    }

    public Integer getAskPrice() {
        return 지수.index.parser().parseInt(rawPacket);
    }

    public Integer getBidPrice() {
        return 지수.index.parser().parseInt(rawPacket);
    }

    public Integer getVolume() {
        return 지수.volume.parser().parseInt(rawPacket);
    }

    public Integer getMoney() {
        return 지수.money.parser().parseInt(rawPacket);
    }


    @Override
    public String getDBName() {
        return MongoDBDBName.CLOSING_PRICE;
    }

    @Override
    public String getCollectionName() {
        return MongoDBCollectionName.INDEX_CLOSING;
    }


    @Override
    public DBObject toDBObject() {

        byte[] timeBytes = 지수.time.parser().parseByte(rawPacket);

        for (int i = 0; i < 6; i++) {
            if (timeBytes[i] != timeEnd[i])
                break;

            if (i == 5) {
                return 지수Closing();
            }
        }

        return null;

    }

    public DBObject 지수Closing() {
        String line = new String(rawPacket);

        String indexCode = 지수.indexCode.parser().parseStr(line);
        String indexIsinCode = AliasManager.Instance.getIsinFromAlias(indexCode);
        if (indexIsinCode == null) {
            DefaultLogger.logger.error("IndexIsin이 정의되지 않은 지수({}) 종가는 무시합니다.[{}]", indexCode, line);
            return null;
        }
        Integer closingPrice = 지수.index.parser().parseInt(rawPacket);

        DBObject ob = new BasicDBObject();
        ob.put("indexCode", indexCode);
        if (indexIsinCode != null) {
            ob.put("indexIsin", indexIsinCode);
        }
        ob.put("price", closingPrice);

        ob.put("date", TimeCenter.Instance.getDateAsDateType());
        ob.put("lastUpdate", new Date());
        return ob;
    }


    @Override
    public DBObject query() {

        String indexCode = new String(지수.indexCode.parser().parseByte(rawPacket));

        DBObject ob = new BasicDBObject();
        ob.put("indexCode", indexCode);
        ob.put("date", TimeCenter.Instance.getDateAsDateType());
        return ob;
    }
}
