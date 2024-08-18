package bulls.staticData;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import bulls.bs.CallPut;
import bulls.dateTime.TimeCenter;
import bulls.db.mongodb.DBCenter;
import bulls.db.mongodb.MongoDBCollectionName;
import bulls.db.mongodb.MongoDBDBName;
import bulls.designTemplate.EarlyInitialize;
import bulls.feed.abstraction.민감도Feed;
import bulls.hephaestus.collection.ExpiryMaster;
import bulls.hephaestus.collection.LPDutyMaster;
import bulls.hephaestus.collection.LpCalendar;
import bulls.log.DefaultLogger;
import bulls.staticData.ProdType.DerivativesUnderlyingType;
import org.bson.Document;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

public enum MarketGreekCenter implements EarlyInitialize {
    Instance;

    // MarketGreekCenter에서 일중 Greek History를 들고 있을 종목 리스트
    private final Set<String> historyGreekIsinCodeSet = new HashSet<>();
    // marketGreeks 컬렉션에 저장할 종목 리스트
    private final Set<String> dbSaveIsinCodeSet = new HashSet<>();

    private final ConcurrentHashMap<String, GreekData> marketGreekMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, ConcurrentSkipListMap<Integer, GreekData>> historyGreekMap = new ConcurrentHashMap<>();

    final LpCalendar recentMonthlyCalendar;
    final LpCalendar nextMonthlyCalendar;

    MarketGreekCenter() {
        recentMonthlyCalendar = ExpiryMaster.Instance.getMonthlyCalendar(TimeCenter.Instance.today);
        nextMonthlyCalendar = ExpiryMaster.Instance.getNextMonthlyCalendar(TimeCenter.Instance.today);
        if (TempConf.MARKETGREEK_DB_CLEAR) {
            //당일 아닌 민감도는 삭제
            int yyyymmdd = TimeCenter.Instance.today.getYear() * 10000 + TimeCenter.Instance.today.getMonthValue() * 100 + TimeCenter.Instance.today.getDayOfMonth();
            DefaultLogger.logger.info("생성일자가 {}가 아닌 데이터는 삭제합니다.", yyyymmdd);
            DBCenter.Instance.removeAll(MongoDBDBName.PRICING_DATA, MongoDBCollectionName.MARKET_GREEKS,
                    new Document("생성일자", new Document("$ne", yyyymmdd)));
        }

        for (var entry : LPDutyMaster.Instance.getAllLPDutyOptCodeMap().entrySet()) {
            Set<String> optIsinCodeSet = entry.getValue();
            DerivativesUnderlyingType dut = DerivativesUnderlyingType.UNKNOWN;

            // 미니코스피때문에 underlyingIsinCode는 사용할 수 없고 dut를 가져와야함.
            for (String optIsinCode : optIsinCodeSet) {
                FuturesInfo info = FuturesInfoCenter.Instance.getFuturesInfo(optIsinCode);
                if (info != null) {
                    dut = info.기초자산ID;
                    break;
                }
            }

            if (dut == DerivativesUnderlyingType.UNKNOWN)
                continue;

            // 미니코스피옵션은 ATM +- 10까지 추가하기, 나머지 의무종목은 전부 추가하기.
            if (dut == DerivativesUnderlyingType.MKI) {
                List<OptionIdentifier> callList = OptionIdentifierCenter.Instance.getOptionsList(CallPut.CALL, dut, 0, -10, 10);
                for (OptionIdentifier id : callList)
                    historyGreekIsinCodeSet.add(id.getIsinCode());

                List<OptionIdentifier> putList = OptionIdentifierCenter.Instance.getOptionsList(CallPut.PUT, dut, 0, -10, 10);
                for (OptionIdentifier id : putList)
                    historyGreekIsinCodeSet.add(id.getIsinCode());

            } else {
                List<OptionIdentifier> callList = OptionIdentifierCenter.Instance.getOptionsList(CallPut.CALL, dut, 0);
                for (OptionIdentifier id : callList)
                    historyGreekIsinCodeSet.add(id.getIsinCode());

                List<OptionIdentifier> putList = OptionIdentifierCenter.Instance.getOptionsList(CallPut.PUT, dut, 0);
                for (OptionIdentifier id : putList)
                    historyGreekIsinCodeSet.add(id.getIsinCode());
            }
        }

        // DB에는 코스피, 미니코스피, 코스닥 근월물, 원월물 ATM +- 10까지만 저장한다.
        Set<DerivativesUnderlyingType> dbSaveDutSet = Set.of(DerivativesUnderlyingType.MKI, DerivativesUnderlyingType.K2I, DerivativesUnderlyingType.KQI);
        for (var dut : dbSaveDutSet) {
            List<OptionIdentifier> callList = OptionIdentifierCenter.Instance.getOptionsList(CallPut.CALL, dut, 0, -10, 10);
            for (OptionIdentifier id : callList)
                dbSaveIsinCodeSet.add(id.getIsinCode());

            List<OptionIdentifier> putList = OptionIdentifierCenter.Instance.getOptionsList(CallPut.PUT, dut, 0, -10, 10);
            for (OptionIdentifier id : putList)
                dbSaveIsinCodeSet.add(id.getIsinCode());
        }

        loadFromDB();
    }

    void loadFromDB() {
        int lastTime = 0;

        LocalDate date = TimeCenter.Instance.getDateAsLocalDateType();
        int dateInt = date.getYear() * 1_0000 + date.getMonthValue() * 100 + date.getDayOfMonth();
        for (String isinCode : historyGreekIsinCodeSet) {
            var timeMap = historyGreekMap.computeIfAbsent(isinCode, k -> new ConcurrentSkipListMap<>());

            Document query = new Document("isinCode", isinCode).append("생성일자", dateInt);
            var col = DBCenter.Instance.findIterable(MongoDBDBName.PRICING_DATA, MongoDBCollectionName.MARKET_GREEKS, query);
            for (Document d : col) {
                GreekData data = GreekData.of(d);
                timeMap.put(data.time, data);
            }

            if (timeMap.size() > 0) {
                Integer lastKey = timeMap.lastKey();
                if (lastKey != null && lastTime < lastKey)
                    lastTime = lastKey;
            }
        }

        if (lastTime == 0)
            return;

        Document query = new Document("생성시각", lastTime).append("생성일자", dateInt);
        var col = DBCenter.Instance.findIterable(MongoDBDBName.PRICING_DATA, MongoDBCollectionName.MARKET_GREEKS, query);
        for (Document d : col) {
            String isinCode = d.getString("isinCode");
            GreekData data = GreekData.of(d);
            marketGreekMap.put(isinCode, data);
        }
    }

    public void update(민감도Feed feed) {
        if (!TempConf.MARKETGREEKCENTER_ENABLED)
            return;

        String isinCode = feed.getCode();
        if (!recentMonthlyCalendar.isRecentProduct(isinCode) && !nextMonthlyCalendar.isRecentProduct(isinCode))
            return;

        GreekData data = GreekData.of(feed);
        marketGreekMap.put(isinCode, data);

        // 의무종목만 history 들고 있기
        if (historyGreekIsinCodeSet.contains(isinCode)) {
            int hhmmssxx = feed.getTimeInteger();
            ConcurrentSkipListMap<Integer, GreekData> m = historyGreekMap.computeIfAbsent(isinCode, (k) -> new ConcurrentSkipListMap<>());
            m.put(hhmmssxx, data);
        }
    }

    public GreekData getLastGreekData(String isinCode) {
        return marketGreekMap.get(isinCode);
    }

    public GreekData getHistoryGreekData(String isinCode, int hhmmssxx) {
        ConcurrentSkipListMap<Integer, GreekData> m = historyGreekMap.computeIfAbsent(isinCode, (k) -> new ConcurrentSkipListMap<>());
        Integer key = m.floorKey(hhmmssxx);
        if (key == null)
            return null;
        return m.get(key);
    }

    public double getCurrentDelta(String isinCode) {
        //민감도 입수 전이라면 0으로 처리한다.
        GreekData data = marketGreekMap.get(isinCode);
        if (data == null)
            return 0;
        return data.delta;
    }

    public void getStatus(ArrayNode dataList) {
        marketGreekMap.forEach((k, v) -> {
            ObjectNode n = new ObjectNode(JsonNodeFactory.instance);
            n.put("code", k);
            n.put("korean", AliasManager.Instance.getKoreanFromIsin(k));
            n.put("time", v.time);
            n.put("delta", v.delta);
            n.put("gamma", v.gamma);
            n.put("vega", v.vega);
            n.put("rho", v.rho);
            n.put("theta", v.theta);
            dataList.add(n);
        });
        DefaultLogger.logger.info("\n{}", this);
    }

    public Set<String> getDbSaveIsinCodeSet() {
        return dbSaveIsinCodeSet;
    }

    //    public Double getGammaAdjustedDelta(String code) {
//        Double pureDelta = marketDeltaMap.get(code);
//        Double gamma = marketGammaMap.get(code);
//        if (pureDelta == null || gamma == null)
//            return 0.0;
//        return 0.0;
//    }
}
