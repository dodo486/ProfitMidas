package bulls.staticData;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import bulls.dateTime.TimeCenter;
import bulls.db.mongodb.DBCenter;
import bulls.db.mongodb.MongoDBCollectionName;
import bulls.db.mongodb.MongoDBDBName;
import bulls.designTemplate.EarlyInitialize;
import bulls.exception.InvalidCodeException;
import bulls.exception.NoClosingPriceException;
import bulls.log.DefaultLogger;
import bulls.staticData.ProdType.ProdType;
import bulls.staticData.ProdType.ProdTypeCenter;
import org.bson.Document;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

/**
 * 주식의 경우는 그대로의 가격, 파생의 경우는 x100 의 가격으로 저장됨.
 */
public enum ClosingPriceCenter implements EarlyInitialize {
    Instance;

    // initiating 시점에만 writing. 동기화 안함.
    private final HashMap<String, Integer> closingPriceMap = new HashMap<>();
    private final HashMap<String, Integer> adjClosingPriceRawMap = new HashMap<>();
    private final HashMap<String, Double> adjClosingPriceMap = new HashMap<>();

    private final Table<LocalDate, String, Integer> closingPriceTable = HashBasedTable.create();

    ClosingPriceCenter() {
        loadCollection(MongoDBCollectionName.FUTURES_CLOSING);
        loadCollection(MongoDBCollectionName.OPTION_CLOSING);
        loadCollection(MongoDBCollectionName.EQUITY_CLOSING);
        loadIndex(MongoDBCollectionName.INDEX_CLOSING);

        DefaultLogger.logger.debug("Total count in String pool:{}", closingPriceMap.size());
    }

    private void loadCollection(String collectionName) {
        LocalDate mostRecentDate = LocalDate.MIN;

        Date dateCond = TimeCenter.getLocalDateAsDateType(TimeCenter.Instance.getDateAsLocalDateType().minusDays(30));
        Document query = new Document("date", new Document("$gte", dateCond));
        var col = DBCenter.Instance.findIterable(MongoDBDBName.CLOSING_PRICE, collectionName, query);
        for (Document doc : col) {
            String isinCode = doc.getString("isinCode").intern();
            Integer price = doc.getInteger("price");
            Date date = doc.getDate("date");

            LocalDate ldt;
            if (date == null)
                ldt = mostRecentDate;
            else
                ldt = TimeCenter.getDateAsLocalDateType(date);

            if (ldt.isAfter(mostRecentDate))
                mostRecentDate = ldt;

            closingPriceTable.put(ldt, isinCode, price);
            closingPriceMap.put(isinCode, price);
        }
    }

    //인덱스의 경우 D2011029 , KRD020020016 둘다 따로 종가 남김.
    private void loadIndex(String collectionName) {
        LocalDate mostRecentDate = LocalDate.MIN;

        var col = DBCenter.Instance.findIterable(MongoDBDBName.CLOSING_PRICE, collectionName);
        for (Document doc : col) {
            try {
                String indexIsin = doc.getString("indexIsin").intern();
                String indexCode = doc.getString("indexCode").intern();
                Integer price = doc.getInteger("price");
                Date date = doc.getDate("date");
                if (indexCode == null || indexIsin == null) {
                    DefaultLogger.logger.error("indexCode or indexIsin is null indexIsin={} indexCode={}", indexIsin, indexCode);
                    continue;
                }
                LocalDate ldt;
                if (date == null)
                    ldt = mostRecentDate;
                else
                    ldt = TimeCenter.getDateAsLocalDateType(date);

                if (ldt.isAfter(mostRecentDate))
                    mostRecentDate = ldt;

                if (ldt.equals(mostRecentDate)) {
                    closingPriceMap.put(indexCode, price);
                    closingPriceMap.put(indexIsin, price);
//                if (TempConf.INDEX_ISIN_KOSPI200.equals(indexIsin)) {
//                    closingPriceMap.put(TempConf.INDEX_KOSPI200, price); // KR49999999KP 를 위한 예외처리
//                }

                    if (PredefinedIsinCode.KOSPI_200_ALT.equals(indexIsin)) {
                        closingPriceMap.put(PredefinedIsinCode.KOSPI_200, price);
                    }
                }

                closingPriceTable.put(ldt, indexCode, price);
                closingPriceTable.put(ldt, indexIsin, price);
                if (PredefinedIsinCode.KOSPI_200_ALT.equals(indexIsin)) {
                    closingPriceTable.put(ldt, PredefinedIsinCode.KOSPI_200, price);
                }
            } catch (Exception e) {
                e.printStackTrace();
                DefaultLogger.logger.error(doc.toString());
            }
        }
    }

    public Double getIndexPrice(String indexCode) throws InvalidCodeException, NoClosingPriceException {
        Double cp = adjClosingPriceMap.get(indexCode);
        if (cp != null)
            return cp;

        ProdType pt = ProdTypeCenter.Instance.getProdType(indexCode);
        if (pt == null) {
            String msg = String.format("%s 의 ProdType 을 찾을수 없습니다.", indexCode);
            throw new InvalidCodeException(msg);
        }

        if (!pt.isIndex()) {
            String msg = String.format("%s 는 Index Code 가 아닙니다.", indexCode);
            throw new InvalidCodeException(msg);
        }

        cp = getClosingPrice(indexCode) * 0.01;
        adjClosingPriceMap.put(indexCode, cp);
        return cp;
    }

    //소수점 없이 정수로 표현된 가격
    public int getAdjClosingPriceRaw(String code) throws NoClosingPriceException {
        Integer cp = adjClosingPriceRawMap.get(code);
        if (cp == null) {
            ProdType pt = ProdTypeCenter.Instance.getProdType(code);
            if (pt != null) {
                if (pt.isEquity()) {
                    int price = EquityInfoCenter.Instance.get기준가(code);
                    adjClosingPriceRawMap.put(code, price);
                    return price;
                } else if (pt.isDerivative()) {
                    FuturesInfo fi = FuturesInfoCenter.Instance.getFuturesInfo(code);
                    if (fi != null) {
                        int price = (int) (fi.기준가 * fi.priceDivider);
                        adjClosingPriceRawMap.put(code, price);
                        return price;
                    } else {
                        return 1;
                    }
                } else if (pt.isIndex()) {
                    return getClosingPrice(code);
                } else {
                    String msg = String.format("%s(%s) 종목의 종가가 없습니다.", AliasManager.Instance.getKoreanFromIsin(code), code);
                    throw new NoClosingPriceException(msg);
                }
            } else {
                String msg = String.format("%s(%s) 종목의 종가가 없습니다.", AliasManager.Instance.getKoreanFromIsin(code), code);
                throw new NoClosingPriceException(msg);
            }
        }
        return cp;
    }

    // 소수점 포함한 가격
    public Double getAdjClosingPrice(String code) throws NoClosingPriceException {
        Double cp = adjClosingPriceMap.get(code);
        if (cp == null) {
            ProdType pt = ProdTypeCenter.Instance.getProdType(code);
            if (pt != null) {
                if (pt.isEquity()) {
                    Double price = (double) EquityInfoCenter.Instance.get기준가(code);
                    adjClosingPriceMap.put(code, price);
                    return price;
                } else if (pt.isDerivative()) {
                    Double price = FuturesInfoCenter.Instance.get기준가(code);
                    adjClosingPriceMap.put(code, price);
                    return price;
                } else if (pt.isIndex()) {
                    return getClosingPrice(code) * 0.01;
                } else {
                    String msg = String.format("%s(%s) 종목의 종가가 없습니다.", AliasManager.Instance.getKoreanFromIsin(code), code);
                    throw new NoClosingPriceException(msg);
                }
            } else {
                String msg = String.format("%s(%s) 종목의 종가가 없습니다.", AliasManager.Instance.getKoreanFromIsin(code), code);
                throw new NoClosingPriceException(msg);
            }
        }
        return cp;
    }

    public Integer getClosingPrice(String code) throws NoClosingPriceException {
        Integer cp = closingPriceMap.get(code);
        if (cp == null) {
            String msg = String.format("%s(%s) 종목의 종가가 없습니다.", AliasManager.Instance.getKoreanFromIsin(code), code);
            throw new NoClosingPriceException(msg);
        }

        return cp;
    }

    public Integer getClosingPrice(LocalDate ld, String code) throws NoClosingPriceException {
        Integer cp = closingPriceTable.get(ld, code);
        if (cp == null) {
            String msg = String.format("%s(%s) 종목의 종가가 없습니다.", AliasManager.Instance.getKoreanFromIsin(code), code);
            throw new NoClosingPriceException(msg);
        }

        return cp;
    }

    public Set<String> getAllAvailableCode() {
        return closingPriceMap.keySet();
    }
}
