package bulls.dmaLog.marketshare;

import java.util.ArrayList;
import java.util.List;

public enum MarketTradingValueShareUpdater implements MarketShareUpdater {
    Instance;

    private List<MarketShare> marketShareList;
    private String keyName;

    public List<MarketShare> getMarketShareList() {
        return marketShareList;
    }

    public MarketTradingValueShareUpdater setKeyName(String keyName) {
        this.keyName = keyName;
        return this;
    }

    public void loadData() {
        marketShareList = new ArrayList<>();
        System.out.println("keyName : " + keyName);
        for (var entry : MarketShareCenter.Instance.getMarketTradingValueShareMap(keyName).entrySet())
            marketShareList.add(entry.getValue());
    }
}