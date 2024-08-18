package bulls.hephaestus.collection;

import bulls.db.mongodb.DBCenter;
import bulls.db.mongodb.MongoDBCollectionName;
import bulls.db.mongodb.MongoDBDBName;
import bulls.exception.NoBidAskDataException;
import bulls.hephaestus.document.StockLPDoc;

import java.util.HashMap;
import java.util.Set;

public enum StockLP {
    Instance;
    final HashMap<String, StockLPDoc> dutyInfoMap = new HashMap<>();

    StockLP() {
        var col = DBCenter.Instance.findIterable(MongoDBDBName.LP, MongoDBCollectionName.STOCK_LP, StockLPDoc.class);
        for (StockLPDoc doc : col) {
            dutyInfoMap.put(doc.isinCode, doc);
        }
    }

    public int getAmtOnDuty(String stockCode, int price) throws NoBidAskDataException {
        StockLPDoc doc = dutyInfoMap.get(stockCode);
        if (doc == null)
            return 0;

        if (price == 0)
            return 0;

        // 매도 수량 = 최우선 매수+ 1틱 , 매도 수량 = 최우선 매도 -1틱 을 금액으로 나누는데 가장 작은 bid 로 하면 보수적으로 다 커버 된다.
        return doc.dutyAmount * 10_500 / price + 1;
    }

    public Set<String> getIsinCodeList() {
        return dutyInfoMap.keySet();
    }

    public boolean isDuty(String isinCode) {
        return dutyInfoMap.containsKey(isinCode);
    }
}
