package bulls.staticData.ELW;

import bulls.dateTime.TimeCenter;
import bulls.db.mongodb.DBCenter;
import bulls.db.mongodb.MongoDBCollectionName;
import bulls.db.mongodb.MongoDBDBName;
import org.bson.Document;

import java.util.Collection;
import java.util.HashMap;

public enum ELWInfoCenter {
    Instance;
    final HashMap<String, ELWInfo> ELWInfoHashMap = new HashMap<>();

    ELWInfoCenter() {
        int today = TimeCenter.Instance.today.getYear() * 10000 + TimeCenter.Instance.today.getMonthValue() * 100 + TimeCenter.Instance.today.getDayOfMonth();
        Document query = new Document("matDate", new Document("$gte", today));
        var col = DBCenter.Instance.findIterable(MongoDBDBName.BATCH, MongoDBCollectionName.ELW_INFO, ELWInfo.class, query);
        for (ELWInfo doc : col) {
            ELWInfoHashMap.put(doc.isinCode, doc);
        }
    }

    public Collection<ELWInfo> getAllELWInfo() {
        return ELWInfoHashMap.values();
    }

    public ELWInfo getELWInfo(String isinCode) {
        return ELWInfoHashMap.get(isinCode);
    }

    public static void main(String[] args) {
        ELWInfo e = ELWInfoCenter.Instance.getELWInfo("KR7005930003");
        System.out.println(e.toString());
    }
}
