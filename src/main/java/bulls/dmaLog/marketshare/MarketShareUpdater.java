package bulls.dmaLog.marketshare;

import bulls.db.mongodb.DBCenter;

import java.util.List;

public interface MarketShareUpdater {
    List<MarketShare> getMarketShareList();

    void loadData();

    default void sendToDB() {
        DBCenter.Instance.updateBulk(getMarketShareList());
    }

    default void loadDataAndSendToDB() {
        loadData();
        sendToDB();
    }
}
