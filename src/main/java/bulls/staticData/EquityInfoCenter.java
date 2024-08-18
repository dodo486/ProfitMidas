package bulls.staticData;

import bulls.dateTime.TimeCenter;
import bulls.db.mongodb.DBCenter;
import bulls.db.mongodb.MongoDBCollectionName;
import bulls.db.mongodb.MongoDBDBName;
import bulls.designTemplate.EarlyInitialize;
import bulls.exception.NoClosingPriceException;
import bulls.log.DefaultLogger;
import org.bson.Document;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.stream.Collectors;

public enum EquityInfoCenter implements EarlyInitialize {
    Instance;
    private final HashMap<String, EquityInfo> equityInfoMap = new HashMap<>();

    EquityInfoCenter() {
        //입수한지 30일 더 된 데이터는 무시(ELW 만기, 상장 폐지 등의 이유)
        Date dateCond = TimeCenter.getLocalDateAsDateType(TimeCenter.Instance.getDateAsLocalDateType().minusDays(30));
        Document query = new Document("date", new Document("$gte", dateCond));
        var col = DBCenter.Instance.findIterable(MongoDBDBName.BATCH, MongoDBCollectionName.EQUITY_INFO, EquityInfo.class, query);
        for (EquityInfo doc : col) {
            if (doc.isinCode.equals("999999999999"))
                continue;
            if (doc.isinCode != null)
                doc.isinCode = doc.isinCode.intern();
            if (doc.shortCode != null)
                doc.shortCode = doc.shortCode.intern();
            if (doc.type != null)
                doc.type = doc.type.intern();
            if (doc.productName != null)
                doc.productName = doc.productName.intern();
            if (doc.KOSPI여부 != null)
                doc.KOSPI여부 = doc.KOSPI여부.intern();
            if (doc.거래정지여부 != null)
                doc.거래정지여부 = doc.거래정지여부.intern();
            if (doc.관리종목여부 != null)
                doc.관리종목여부 = doc.관리종목여부.intern();
            if (doc.증권그룹ID != null)
                doc.증권그룹ID = doc.증권그룹ID.intern();
            if (doc.전일종가구분코드 != null)
                doc.전일종가구분코드 = doc.전일종가구분코드.intern();
            if (doc.KOSPI200섹터업종 != null)
                doc.KOSPI200섹터업종 = doc.KOSPI200섹터업종.intern();
            equityInfoMap.put(doc.isinCode, doc);
            DefaultLogger.logger.info("{}({}) 현물로딩기준가 : {}", doc.productName, doc.isinCode, doc.기준가격);
        }
    }

    public void updateFromFeed(EquityInfo info){
        equityInfoMap.put(info.isinCode, info);
    }

    public Collection<EquityInfo> getStockEquityInfo() {
        Date todayDate = TimeCenter.Instance.getDateAsDateType();
        return equityInfoMap.values().stream().filter(it -> it.증권그룹ID.equals("ST") && it.date.compareTo(todayDate) >= 0).collect(Collectors.toList());
    }

    public Collection<EquityInfo> getETFEquityInfo() {
        Date todayDate = TimeCenter.Instance.getDateAsDateType();
        return equityInfoMap.values().stream().filter(it -> it.증권그룹ID.equals("EF") && it.date.compareTo(todayDate) >= 0).collect(Collectors.toList());
    }

    public Collection<EquityInfo> getAllUnexpiredEquityInfo() {
        Date todayDate = TimeCenter.Instance.getDateAsDateType();
        return equityInfoMap.values().stream().filter(it -> it.date.compareTo(todayDate) >= 0).collect(Collectors.toList());
    }

    public Collection<EquityInfo> getAllEquityInfo() {
        return equityInfoMap.values();
    }

    public EquityInfo getEquityInfo(String isinCode) {
        return equityInfoMap.get(isinCode);
    }


    public static void main(String[] args) {
    }

    public int get기준가(String isinCode) throws NoClosingPriceException {

        int 기준가;
        EquityInfo e = EquityInfoCenter.Instance.getEquityInfo(isinCode);
        if (e != null) {
            기준가 = (int) e.기준가격;
        } else {
            기준가 = ClosingPriceCenter.Instance.getClosingPrice(isinCode);
        }

        return 기준가;
    }

    public int get상한가(String stockCode) {
        return (int) getEquityInfo(stockCode).상한가;
    }

    public int get하한가(String stockCode) {
        return (int) getEquityInfo(stockCode).하한가;
    }
}
