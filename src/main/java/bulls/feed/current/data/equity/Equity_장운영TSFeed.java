package bulls.feed.current.data.equity;

import bulls.db.mongodb.DBData;
import bulls.db.mongodb.MongoDBDBName;
import bulls.feed.abstraction.BidAskFillUpdater;
import bulls.feed.abstraction.Feed;
import bulls.feed.current.enums.FeedTRCode;
import bulls.feed.current.parser.equity.Equity_장운영TS;
import bulls.feed.dc.enums.*;
import bulls.staticData.장운영TSCenter;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class Equity_장운영TSFeed extends Feed implements DBData {

    public Equity_장운영TSFeed(FeedTRCode trCode, byte[] packet) {
        super(trCode, packet);
    }

    @Override
    public String getDBName() {
        //어디다 넣을지 몰라 우선 HEPHA DB로 .
        return MongoDBDBName.HEPHA;
    }

    @Override
    public String getCollectionName() {
        //콜렉션 정해졌을떄 추가할 것.
        return null;
    }

    @Override
    public DBObject toDBObject() {
        String line = new String(rawPacket);
        String isinCode = Equity_장운영TS.isinCode.parser().parseStr(line);
        보드ID boardID = 보드ID.getEnum(Equity_장운영TS.보드Id.parser().parseStr(line));
        보드이벤트ID boardEventID = 보드이벤트ID.getEnum(Equity_장운영TS.보드이벤트Id.parser().parseStr(line));
        Integer boardEventStartTime = Equity_장운영TS.보드이벤트시작시간.parser().parseInt(rawPacket);
        세션ID sessionID = 세션ID.getEnum(Equity_장운영TS.세션ID.parser().parseInt(rawPacket));
        거래정지사유코드 stopCode = 거래정지사유코드.getEnum(Equity_장운영TS.거래정지사유코드.parser().parseInt(rawPacket));

        DBObject ob = new BasicDBObject();

        ob.put("isincode", isinCode);
        ob.put("boardID", boardID);
        ob.put("boardEventID", boardEventID);
        ob.put("boardEventStartTime", boardEventStartTime);
        ob.put("sessionID", sessionID);
        ob.put("stopCode", stopCode);

        return ob;

    }

    @Override
    public DBObject query() {
        String line = new String(rawPacket);
        String isinCode = Equity_장운영TS.isinCode.parser().parseStr(line);
        DBObject ob = new BasicDBObject();
        ob.put("isinCode", isinCode);
        return ob;
    }

    @Override
    public void updateBidAskFill(BidAskFillUpdater dc) {
        장운영TS ts = new 장운영TS();
        String line = new String(rawPacket);

        String isinCode = Equity_장운영TS.isinCode.parser().parseStr(line);

        String boardID = Equity_장운영TS.보드Id.parser().parseStr(line);
        ts.boardID = 보드ID.getEnum(boardID);

        String boardEventID = Equity_장운영TS.보드이벤트Id.parser().parseStr(line);
        ts.boardEventID = 보드이벤트ID.getEnum(boardEventID);

        ts.boardEventStartTime = Equity_장운영TS.보드이벤트시작시간.parser().parseInt(rawPacket);

        Integer sessionID = Equity_장운영TS.세션ID.parser().parseInt(rawPacket);
        ts.sessionID = 세션ID.getEnum(sessionID);

        Integer stopCodeInt = Equity_장운영TS.거래정지사유코드.parser().parseInt(rawPacket);
        ts.stopCode = 거래정지사유코드.getEnum(stopCodeInt);
        장운영TSCenter.Instance.updateTS(isinCode, ts);
    }
}
