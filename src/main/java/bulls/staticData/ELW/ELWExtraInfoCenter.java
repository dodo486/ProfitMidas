package bulls.staticData.ELW;

import bulls.dateTime.TimeCenter;
import bulls.db.mongodb.DBCenter;
import bulls.db.mongodb.MongoDBCollectionName;
import bulls.db.mongodb.MongoDBDBName;
import org.bson.Document;

import java.util.Collection;
import java.util.HashMap;

public enum ELWExtraInfoCenter {
    Instance;
    final HashMap<String, ELWExtraInfo> elwExtraInfoHashMap = new HashMap<>();

    ELWExtraInfoCenter() {
        int today = TimeCenter.Instance.today.getYear() * 10000 + TimeCenter.Instance.today.getMonthValue() * 100 + TimeCenter.Instance.today.getDayOfMonth();
        Document query = new Document("최종거래일자", new Document("$gte", today));
        var col = DBCenter.Instance.findIterable(MongoDBDBName.BATCH, MongoDBCollectionName.ELW_EXTRA_INFO, ELWExtraInfo.class, query);
        for (ELWExtraInfo doc : col) {
            doc.isinCode = doc.isinCode.intern();
            if (doc.type != null)
                doc.type = doc.type.intern();
            if (doc.발행사 != null)
                doc.발행사 = doc.발행사.intern();
            if (doc.기초자산1 != null)
                doc.기초자산1 = doc.기초자산1.intern();
            if (doc.콜풋 != null)
                doc.콜풋 = doc.콜풋.intern();
            if (doc.만기평가방식 != null)
                doc.만기평가방식 = doc.만기평가방식.intern();
            elwExtraInfoHashMap.put(doc.isinCode, doc);
        }
    }

    public void updateFromFeed(ELWExtraInfo info){
        elwExtraInfoHashMap.put(info.isinCode, info);
    }

    public Collection<ELWExtraInfo> getAllELWExtraInfo() {
        return elwExtraInfoHashMap.values();
    }

    public ELWExtraInfo getELWExtraInfo(String isinCode) {
        return elwExtraInfoHashMap.get(isinCode);
    }

    public static void main(String[] args) {
        ELWExtraInfo e = ELWExtraInfoCenter.Instance.getELWExtraInfo("KR7005930003");
        System.out.println(e.toString());
    }
}
