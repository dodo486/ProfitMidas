package bulls.etf;


import bulls.db.mongodb.DBCenter;
import bulls.db.mongodb.MongoDBCollectionName;
import bulls.db.mongodb.MongoDBDBName;
import bulls.designTemplate.EarlyInitialize;
import bulls.staticData.ProdType.DerivativesUnderlyingType;
import bulls.staticData.ProdType.equity.IndexMarketType;
import org.bson.Document;

import java.util.HashMap;

// ETF_INFO table equivalent
public enum EtpInfoCenter implements EarlyInitialize {
    Instance;

    private final HashMap<String, ETPInfo> etfInfoMap = new HashMap<>();

    EtpInfoCenter() {
        Document query = new Document("ETP상품구분코드", "2");
        var col = DBCenter.Instance.findIterable(MongoDBDBName.BATCH, MongoDBCollectionName.EQUITY_INFO, ETPEquityInfo.class, query);
        for (ETPEquityInfo doc : col) {
            ETPInfo info = ETPInfo.create(doc);
            etfInfoMap.put(doc.isinCode, info);
        }
    }

    public ETPInfo getETPInfo(String etfCode) {
        return etfInfoMap.get(etfCode);
    }

    public IndexMarketType getMarketType(String isinCode) {
        ETPInfo info = etfInfoMap.get(isinCode);
        if (info == null)
            return IndexMarketType.ETP아님;
        return info.indexMarketType;
    }


    public DerivativesUnderlyingType getUnderlyingOfEtf(String isinCode) {

        if (!etfInfoMap.containsKey(isinCode))
            return DerivativesUnderlyingType.UNKNOWN;

        ETPInfo info = etfInfoMap.get(isinCode);

        return info.uType;
    }

    public String getIndexCodeOf(String etfCode) {
        DerivativesUnderlyingType type = getUnderlyingOfEtf(etfCode);
        return type.getUnderlyingIsinCode();
//        if (type == DerivativesUnderlyingType.K2I)
//            return TempConf.INDEX_ISIN_KOSPI200;
//        if (type == DerivativesUnderlyingType.KQI)
//            return TempConf.INDEX_ISIN_KOSDAQ150;
//        if( type == DerivativesUnderlyingType.XA1)
//            return TempConf.INDEX_ISIN_정보기술;
//        return null;
    }

    public int getMultiplier(String code) {
        if (!etfInfoMap.containsKey(code))
            return 1;

        ETPInfo info = etfInfoMap.get(code);
        return info.indexMultiplierType.getMultiplier();
    }

    public Double getFee(String code) {
        return 0d;
    }

}
