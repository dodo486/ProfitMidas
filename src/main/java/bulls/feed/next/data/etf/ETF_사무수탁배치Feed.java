package bulls.feed.next.data.etf;

import bulls.db.mongodb.DBData;
import bulls.db.mongodb.MongoDBCollectionName;
import bulls.db.mongodb.MongoDBDBName;
import bulls.feed.abstraction.BidAskFillUpdater;
import bulls.feed.abstraction.Feed;
import bulls.feed.current.enums.FeedTRCode;
import bulls.feed.current.parser.etf.ETF_사무수탁배치;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import java.util.Date;

public class ETF_사무수탁배치Feed extends Feed implements DBData {
    public ETF_사무수탁배치Feed(FeedTRCode trCode, byte[] packet) {
        super(trCode, packet);
    }

    @Override
    public void updateBidAskFill(BidAskFillUpdater dc) {

    }


    @Override
    public String getDBName() {
        return MongoDBDBName.BATCH;
    }

    @Override
    public String getCollectionName() {
        return MongoDBCollectionName.ETF_BATCH;
    }


    @Override
    public DBObject toDBObject() {
        String line = new String(rawPacket);
        String isinCode = ETF_사무수탁배치.isinCode.parser().parseStr(line);
        int cuAmt = ETF_사무수탁배치.ETF_CU수량.parser().parseInt(line);
        double nav = ETF_사무수탁배치.전일NAV.parser().parseDoubleInsertDot(rawPacket);


        DBObject ob = new BasicDBObject();
        ob.put("isinCode", isinCode);
        ob.put("cuAmt", cuAmt);
        ob.put("nav", nav);
        ob.put("lastUpdate", new Date());
        return ob;
    }

    @Override
    public DBObject query() {
        String line = new String(rawPacket);
        String isinCode = ETF_사무수탁배치.isinCode.parser().parseStr(line);
        DBObject ob = new BasicDBObject();
        ob.put("isinCode", isinCode);
        return ob;
    }

}
