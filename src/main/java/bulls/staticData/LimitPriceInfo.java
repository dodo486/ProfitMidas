package bulls.staticData;

import bulls.db.mongodb.DBCenter;
import bulls.db.mongodb.MongoDBCollectionName;
import bulls.db.mongodb.MongoDBDBName;
import bulls.db.mongodb.MongoDBData;
import org.bson.Document;

public class LimitPriceInfo implements MongoDBData {
    private final String isinCode;
    private int upperLimitPrice;
    private int lowerLimitPrice;

    public LimitPriceInfo(String isinCode, int upperLimitPrice, int lowerLimitPrice) {
        if (upperLimitPrice < lowerLimitPrice) {
            int temp;
            temp = upperLimitPrice;
            upperLimitPrice = lowerLimitPrice;
            lowerLimitPrice = temp;
        }

        this.isinCode = isinCode;
        this.upperLimitPrice = upperLimitPrice;
        this.lowerLimitPrice = lowerLimitPrice;
    }

    public String getIsinCode() {
        return isinCode;
    }

    public int getLowerLimitPrice() {
        return lowerLimitPrice;
    }

    public int getUpperLimitPrice() {
        return upperLimitPrice;
    }

    public LimitPriceInfo updateLowerLimitPrice(int lowerLimitPrice) {
        this.lowerLimitPrice = lowerLimitPrice;
        return this;
    }

    public LimitPriceInfo updateUpperLimitPrice(int upperLimitPrice) {
        this.upperLimitPrice = upperLimitPrice;
        return this;
    }

    public void updateToDB() {
        DBCenter.Instance.updateBulk(this);
    }

    @Override
    public String getDBName() {
        return MongoDBDBName.PRICE;
    }

    @Override
    public String getCollectionName() {
        return MongoDBCollectionName.LIVE_LIMIT_PRICE;
    }

    @Override
    public Document getDataDocument() {
        return new Document()
                .append("isinCode", isinCode)
                .append("upperLimitPrice", upperLimitPrice)
                .append("lowerLimitPrice", lowerLimitPrice);
    }

    @Override
    public Document getQueryDocument() {
        return new Document("isinCode", isinCode);
    }
}
