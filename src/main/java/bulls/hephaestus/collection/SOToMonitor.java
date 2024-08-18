package bulls.hephaestus.collection;

import bulls.db.mongodb.DBCenter;
import bulls.db.mongodb.MongoDBCollectionName;
import bulls.db.mongodb.MongoDBDBName;
import org.bson.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


//주식옵션 의무 종목 과 잔고 있는 종목의 합집합
public enum SOToMonitor {
    Instance;

    private final HashMap<String, List<Integer>> strikesToMonitor = new HashMap<>();

    SOToMonitor() {
        var col = DBCenter.Instance.findIterable(MongoDBDBName.HEPHA, MongoDBCollectionName.STOCK_OPTION_TO_MONITOR);
        for (Document doc : col) {
            String isinCode = doc.getString("isinCode");
            List<Integer> strikes = (List<Integer>) doc.get("strikes");
            strikesToMonitor.put(isinCode, strikes);
        }
    }

    public List<Integer> getStrikes(String isinCode) {
        List<Integer> strikes = strikesToMonitor.get(isinCode);
        if (strikes == null)
            strikes = new ArrayList<>();
        return strikes;
    }
}
