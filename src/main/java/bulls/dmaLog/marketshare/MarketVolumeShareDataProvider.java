package bulls.dmaLog.marketshare;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import bulls.dmaLog.loader.DMALogDataCenter;
import bulls.websocket.asyncHandling.AsyncResponse;
import bulls.websocket.asyncHandling.AsyncWork;

public class MarketVolumeShareDataProvider implements AsyncWork {
    @Override
    public AsyncResponse asyncWork(AsyncResponse obj) {
        DMALogDataCenter.Instance.loadDataAndNotify();

        ObjectNode root = obj.getOutputNode();
        ArrayNode volumeList = root.putArray("totalQuantityList");
        for (var entry : MarketShareCenter.Instance.getMarketVolumeShareMap().entrySet())
            volumeList.add(entry.getValue().toObjectNode());

        root.put("trCode", "FEPServerResponse");
        root.put("action", "todayTotalQuantityData");

        System.out.println("Send todayTotalQuantityData");
        return obj;
    }
}
