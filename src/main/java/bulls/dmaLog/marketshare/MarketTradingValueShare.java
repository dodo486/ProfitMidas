package bulls.dmaLog.marketshare;

import com.fasterxml.jackson.databind.node.ObjectNode;
import bulls.dateTime.TimeCenter;
import bulls.designTemplate.JsonConvertible;
import bulls.dmaLog.TradeDMALog;
import bulls.staticData.FuturesInfoCenter;
import bulls.staticData.TempConf;
import org.bson.Document;

import java.util.Date;

public class MarketTradingValueShare implements MarketShare, JsonConvertible {
    private final String keyName;
    private final String key;

    private long totalPrice;

    public MarketTradingValueShare(String keyName, String key) {
        this.keyName = keyName;
        this.key = key;
    }

    public void update(TradeDMALog log) {
        double multiplier = FuturesInfoCenter.Instance.getPureMultiplier(log.getIsinCode());
        totalPrice += log.getKrxExecutionQuantity() * log.getOrderPrice() * multiplier;
    }

    public long getTotalPrice() {
        return totalPrice;
    }

    public Document getDataDocument() {
        Document obj = new Document();

        obj.put(keyName, key);
        obj.put("totalPrice", totalPrice);
        obj.put("location", TempConf.FEP_LOCATION.getLocationString());
        obj.put("date", TimeCenter.Instance.getDateAsDateType());
        obj.put("lastUpdate", new Date());

        return obj;
    }

    public Document getQueryDocument() {
        Document obj = new Document();

        obj.put(keyName, key);
        obj.put("location", TempConf.FEP_LOCATION.getLocationString());
        obj.put("date", TimeCenter.Instance.getDateAsDateType());

        return obj;
    }

    public String getCollectionName() {
        return keyName + "TotalPrice";
    }

    @Override
    public void fillObjectNode(ObjectNode node) {
        node.put("location", TempConf.FEP_LOCATION.getLocationString());
        node.put(keyName, key);
        node.put("totalPrice", totalPrice);
    }
}
