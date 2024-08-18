package bulls.feed.current.data.kospiOption;

import bulls.db.mongodb.DBAssigned;
import bulls.db.mongodb.DocumentConvertible;
import bulls.db.mongodb.MongoDBCollectionName;
import bulls.db.mongodb.MongoDBDBName;
import bulls.feed.abstraction.BidAskFillUpdater;
import bulls.feed.abstraction.Feed;
import bulls.feed.abstraction.민감도Feed;
import bulls.feed.current.enums.FeedTRCode;
import bulls.feed.current.parser.kospiOption.Option_민감도;
import bulls.staticData.MarketGreekCenter;
import org.bson.Document;

public class Option_민감도Feed extends Feed implements DocumentConvertible, DBAssigned, 민감도Feed {
    Document queryDoc;
    Document dataDoc;

    public Option_민감도Feed(FeedTRCode trCode, byte[] packet) {
        super(trCode, packet);
        queryDoc = new Document();
        // DB에 저장할 종목만 query를 채워준다.
        if (MarketGreekCenter.Instance.getDbSaveIsinCodeSet().contains(this.getCode())) {
            queryDoc.put("isinCode", this.getCode());
            queryDoc.put("생성시각", Option_민감도.생성시각.parser().parseInt(rawPacket));
            queryDoc.put("생성일자", Option_민감도.생성일자.parser().parseInt(rawPacket));
        }

        dataDoc = new Document();
        dataDoc.put("isinCode", this.getCode());
    }

    public String getCode() {
        return Option_민감도.isinCode.parser().parseStr(rawPacket, "");
    }

    public String get구분코드() {
        return Option_민감도.구분코드.parser().parseStr(rawPacket, "");
    }

    public double getDelta() {
        return Option_민감도.Delta.parser().parseFloatWithLeadingSign(rawPacket, 6);
    }

    public double getGamma() {
        return Option_민감도.Gamma.parser().parseFloatWithLeadingSign(rawPacket, 6);
    }

    public double getTheta() {
        return Option_민감도.Theta.parser().parseFloatWithLeadingSign(rawPacket, 6);
    }

    public double getRho() {
        return Option_민감도.Rho.parser().parseFloatWithLeadingSign(rawPacket, 6);
    }

    public double getVega() {
        return Option_민감도.Vega.parser().parseFloatWithLeadingSign(rawPacket, 6);
    }

    @Override
    public int getTimeInteger() {
        return Option_민감도.생성시각.parser().parseInt(rawPacket);
    }

    public int get생성일자() {
        return Option_민감도.생성일자.parser().parseInt(rawPacket);
    }

    @Override
    public void updateBidAskFill(BidAskFillUpdater dc) {
        MarketGreekCenter.Instance.update(this);
    }


    @Override
    public Document getDataDocument() {
        String line = new String(rawPacket);
        String type = Option_민감도.구분코드.parser().parseStr(line);
        dataDoc.append("생성시각", Option_민감도.생성시각.parser().parseInt(rawPacket))
                .append("생성일자", Option_민감도.생성일자.parser().parseInt(rawPacket))
                .append("구분코드", type)
                .append("Delta", getDelta())
                .append("Gamma", getGamma())
                .append("Theta", getTheta())
                .append("Rho", getRho())
                .append("Vega", getVega());
        return dataDoc;
    }

    @Override
    public Document getQueryDocument() {
        return queryDoc;
    }

    @Override
    public String getDBName() {
        return MongoDBDBName.PRICING_DATA;
    }

    @Override
    public String getCollectionName() {
        return MongoDBCollectionName.MARKET_GREEKS;
    }
}
