package bulls.feed.current.data.etc;

import bulls.dateTime.TimeCenter;
import bulls.db.mongodb.DBData;
import bulls.db.mongodb.MongoDBCollectionName;
import bulls.db.mongodb.MongoDBDBName;
import bulls.feed.abstraction.BidAskFillUpdater;
import bulls.feed.abstraction.Feed;
import bulls.feed.current.enums.FeedTRCode;
import bulls.feed.current.parser.etc.공시;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class 공시_Feed extends Feed implements DBData {
    public 공시_Feed(FeedTRCode trCode, byte[] packet) {
        super(trCode, packet);
    }

    @Override
    public void updateBidAskFill(BidAskFillUpdater dc) {
        //batch
    }

    @Override
    public String getDBName() {
        return MongoDBDBName.NOTICE;
    }

    @Override
    public String getCollectionName() {
        return MongoDBCollectionName.KRX_NOTICE;
    }


    @Override
    public DBObject toDBObject() {
        String trCode = 공시.trCode.parser().parseStr(rawPacket, "");
        String isinCode = 공시.isinCode.parser().parseStr(rawPacket, "");
        Integer noticeIndex = 공시.noticeIndex.parser().parseInt(rawPacket);
        //Integer noticeTotalPage = 공시.noticeTotalPage.parser().parseInt(rawPacket);
        //Integer noticeCurrPage = 공시.noticeCurrPage.parser().parseInt(rawPacket);
        Integer noticeDate = 공시.noticeDate.parser().parseInt(rawPacket);

        String marketType = 공시.marketType.parser().parseStr(rawPacket, "");
        String productName = 공시.productName.parser().parseStr(rawPacket, "").trim();
        String title;
        try {
            title = 공시.title.parser().parseStr(rawPacket, "").trim();
        } catch (Exception e) {
            try {
                title = new String(rawPacket, 공시.title.parser().getOffset(), Math.min(rawPacket.length - 공시.title.parser().getOffset(), 공시.title.parser().getLength()), "EUC-KR");
            } catch (Exception ee) {
                title = "타이틀 파싱 실패";
            }
        }


        DBObject ob = new BasicDBObject();
        ob.put("trCode", trCode);
        ob.put("isinCode", isinCode);
        ob.put("noticeIndex", noticeIndex);
        //ob.put("noticeTotalPage", noticeTotalPage);
        //ob.put("noticeCurrPage", noticeCurrPage);
        ob.put("noticeDate", noticeDate);
        ob.put("marketType", marketType);
        ob.put("productName", productName);
        ob.put("title", title);
        //ob.put("content", content);
        ob.put("lastUpdate", TimeCenter.Instance.getDateTimeAsDateType());
        return ob;

    }

    @Override
    public DBObject query() {
        Integer noticeIndex = 공시.noticeIndex.parser().parseInt(rawPacket);
        Integer noticeDate = 공시.noticeDate.parser().parseInt(rawPacket);
        //Integer noticeCurrPage = 공시.noticeCurrPage.parser().parseInt(rawPacket);

        DBObject ob = new BasicDBObject();
        ob.put("noticeDate", noticeDate);
        ob.put("noticeIndex", noticeIndex);
        //ob.put("noticeCurrPage", noticeCurrPage);
        return ob;
    }
}
