package bulls.dmaLog.marketshare;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import bulls.dmaLog.loader.DMALogDataCenter;
import bulls.websocket.asyncHandling.AsyncResponse;
import bulls.websocket.asyncHandling.AsyncWork;

public class MarketTradingValueShareDataProvider implements AsyncWork {
    @Override
    public AsyncResponse asyncWork(AsyncResponse obj) {
        DMALogDataCenter.Instance.loadDataAndNotify();

        ObjectNode inputNode = obj.getInputNode();
        String keyName = inputNode.get("key").asText();

        ObjectNode root = obj.getOutputNode();
        ArrayNode totalPriceList = root.putArray("totalPriceList");
        for (var entry : MarketShareCenter.Instance.getMarketTradingValueShareMap(keyName).entrySet())
            totalPriceList.add(entry.getValue().toObjectNode());

        root.put("trCode", "FEPServerResponse");
        root.put("action", keyName + "TotalPriceData");

        System.out.println("Send " + keyName + "TotalPriceData");

        return obj;
    }
}
