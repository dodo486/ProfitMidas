package bulls.dmaLog.marketshare;

import java.util.ArrayList;
import java.util.List;

public enum MarketVolumeShareUpdater implements MarketShareUpdater {
    Instance;

    private List<MarketShare> marketShareList;

    public List<MarketShare> getMarketShareList() {
        return marketShareList;
    }

    public void loadData() {
        marketShareList = new ArrayList<>(MarketShareCenter.Instance.getMarketVolumeShareMap().values());
    }
}