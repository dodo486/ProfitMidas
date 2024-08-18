package bulls.hephaestus.collection;

import bulls.dateTime.TimeCenter;
import bulls.db.mongodb.DBCenter;
import bulls.db.mongodb.MongoDBCollectionName;
import bulls.db.mongodb.MongoDBDBName;
import bulls.hephaestus.document.PricingFactorDoc;
import bulls.json.DefaultMapper;
import bulls.log.DefaultLogger;
import bulls.staticData.AliasManager;
import bulls.staticData.FuturesInfo;
import bulls.staticData.FuturesInfoCenter;
import bulls.staticData.TempConf;
import org.bson.Document;

import java.time.LocalDate;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

public enum PricingFactor {
    Instance;
    private final ConcurrentHashMap<String, ConcurrentSkipListMap<Date, PricingFactorDoc>> divMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, ConcurrentSkipListMap<Date, PricingFactorDoc>> rateMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Double> recentRiskFreeRateMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Double> recentBorrowingRateMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, PricingFactorDoc> recentDivMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Double> derivDivMap = new ConcurrentHashMap<>(); // 파생상품 만기가 배당락일 이전인 경우 div 값, 아니면 0 으로 cache

    PricingFactor() {
        var col = DBCenter.Instance.findIterable(MongoDBDBName.PRICING_DATA, MongoDBCollectionName.PRICING_FACTOR);
        for (Document d : col) {
            PricingFactorDoc doc = DefaultMapper.getMongoDocumentMapper().fromJson(d.toJson(), PricingFactorDoc.class);
            if (doc.factorType.equals("rate")) {
                var m = rateMap.computeIfAbsent(doc.isinCode, (k) -> new ConcurrentSkipListMap<>());
                m.put(doc.rateDate, doc);
            } else {
                LocalDate divFallDate = TimeCenter.getDateAsLocalDateType(doc.divDate);
                if (TimeCenter.Instance.today.isBefore(divFallDate)) {
                    var m = divMap.computeIfAbsent(doc.isinCode, (k) -> new ConcurrentSkipListMap<>());
                    m.put(doc.divDate, doc);
                } else {
                    // 과거 배당 정보 스킵
                    DefaultLogger.logger.info("{} 과거 배당정보는 스킵합니다. {}", AliasManager.Instance.getKoreanFromIsin(doc.isinCode), doc.divDate);
                }
            }
        }
        var today = TimeCenter.Instance.getDateAsDateType();
        double rfr = TempConf.조달금리;
        rateMap.forEach((k, v) -> {
            var m = v.headMap(today, true);
            if (v.size() > 0) {
                double rf = v.lastEntry().getValue().riskFreeRate;
                double br = v.lastEntry().getValue().borrowingRate;
                recentRiskFreeRateMap.put(k, rf);
                recentBorrowingRateMap.put(k, br);

            } else {
                recentRiskFreeRateMap.put(k, rfr);
                recentBorrowingRateMap.put(k, 0.0);
            }
        });
        divMap.forEach((k, v) -> {
            var m = v.tailMap(today, false);
            if (v.size() > 0) {
                recentDivMap.put(k, v.firstEntry().getValue());
            }
        });
    }

    public static void main(String[] args) {
        System.out.println(TimeCenter.Instance.today.toString());
        double div = PricingFactor.Instance.getDivOfDeriv("KR4111P70002");
        System.out.println(div);
    }

    public double getDivOfEquity(String equityIsinCode) {
        PricingFactorDoc pf = recentDivMap.get(equityIsinCode);
        if (pf == null)
            return 0;
        return pf.div;
    }

    public double getDivMax(String isinCode) {
        PricingFactorDoc pf = recentDivMap.get(isinCode);
        if (pf == null)
            return 0;
        return pf.divMax;
    }

    public double getDivMin(String isinCode) {
        PricingFactorDoc pf = recentDivMap.get(isinCode);
        if (pf == null)
            return 0;
        return pf.divMin;
    }

    public double getBorrowingRate(String isinCode) {
        Double r = recentBorrowingRateMap.get(isinCode);
        if (r == null) {
            DefaultLogger.logger.info("PricingFactor BorrowingRate not found for {}. use {}% instead.", isinCode, 0);
            return 0;
        }
        return r;
    }

    public double getRiskFreeRate(String isinCode) {
        Double r = recentRiskFreeRateMap.get(isinCode);
        if (r == null) {
            double rfr = TempConf.조달금리;
            DefaultLogger.logger.info("PricingFactor RiskFreeRate not found for {}. use {}% instead.", isinCode, rfr * 100);
            return rfr;
        }
        return r;
    }

    //배당락 발생일
    Date getRecentDivFallDate(String isinCode) {
        PricingFactorDoc pf = recentDivMap.get(isinCode);
        if (pf == null) {
            DefaultLogger.logger.info("PricingFactor DivDate not found for {}. use {} instead.", isinCode, new Date(0));
            return new Date(0);
        }
        return pf.divDate;
    }

    public double getDivOfDeriv(String isinCode) {
        return derivDivMap.computeIfAbsent(isinCode, this::checkDivOfDeriv);
    }

    private double checkDivOfDeriv(String derivIsinCode) {
        FuturesInfo futuresInfo = FuturesInfoCenter.Instance.getFuturesInfo(derivIsinCode);
        if (futuresInfo == null)
            return 0;
        LocalDate expiryDate = TimeCenter.getDateAsLocalDateType(futuresInfo.만기);
        Date divFallDate = getRecentDivFallDate(futuresInfo.underlyingIsinCode);
        if (divFallDate == null)
            return 0;

        // 만기가 배당락일 이후인 경우 true
        if (expiryDate.isAfter(TimeCenter.getDateAsLocalDateType(divFallDate))) {
            double div = getDivOfEquity(futuresInfo.underlyingIsinCode);
            return div;
        }
        return 0;
    }
}
