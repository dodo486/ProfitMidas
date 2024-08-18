package bulls.dmaLog.marketshare;

import bulls.db.mongodb.MongoDBData;

public interface MarketShare extends MongoDBData {
    default String getDBName() {
        return "fepAnalytics";
    }
}
