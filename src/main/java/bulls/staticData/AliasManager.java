package bulls.staticData;


import bulls.db.mongodb.DBCenter;
import bulls.db.mongodb.MongoDBCollectionName;
import bulls.db.mongodb.MongoDBDBName;
import bulls.designTemplate.EarlyInitialize;
import bulls.server.enums.ServerPurpose;
import bulls.staticData.ELW.ELWExtraInfo;
import bulls.staticData.ELW.ELWExtraInfoCenter;
import bulls.staticData.ELW.ELWIdentifier;
import bulls.staticData.ELW.ELWIdentifierCenter;
import org.bson.Document;

import java.util.Date;
import java.util.HashMap;

public enum AliasManager implements EarlyInitialize {
    Instance;

    private final HashMap<String, String> aliasToIsinMap;
    private final HashMap<String, String> isinToAliasMap;
    private final HashMap<String, String> isinToKoreanMap;
    private final HashMap<String, String> koreanToIsinMap;

    AliasManager() {
        aliasToIsinMap = new HashMap<>();
        isinToAliasMap = new HashMap<>();
        isinToKoreanMap = new HashMap<>();
        koreanToIsinMap = new HashMap<>();
        initIndexClosing();
        initStock();
        //옵션 Korean 설정시 인덱스/주식의 korean이 필요하므로 인덱스, 주식 먼저 초기화 해야함
        initDerivatives();
        //ELW Korean 설정시 인덱스/주식의 korean이 필요하므로 인덱스, 주식 먼저 초기화 해야함
        initELW();
        if (TempConf.SERVER_PURPOSE == ServerPurpose.MAIN_HEDGE)
            writeIsinCodeToKoreanToDB();
    }

    void initDerivatives() {
        for (FuturesInfo info : FuturesInfoCenter.Instance.getAllFuturesInfo()) {
            String isinCode = info.isinCode;
            String productName = info.productName;
            productName = productName.replace("( 10)", "").replace("(  10)", "").trim().intern();
//            DefaultLogger.logger.info("Init Deriv : {} {}", isinCode, isinCode);
            String shortCode = isinCode.substring(3, 11).intern();
            OptionIdentifier oid = OptionIdentifierCenter.Instance.getOptionId(isinCode);
            if (oid != null) {
                String uKorean = isinToKoreanMap.get(oid.getUnderlyingCode());
                if (uKorean != null)
                    productName = oid.getNormalizedOptionName(uKorean);
            }
            aliasToIsinMap.put(shortCode, isinCode);
            isinToAliasMap.put(isinCode, shortCode);
            isinToKoreanMap.put(isinCode, productName); // 파생은 isin -> 한글이름 테이블에만 넣자
        }
    }

    void initELW() {
        for (EquityInfo info : EquityInfoCenter.Instance.getAllEquityInfo()) {
            if (!info.증권그룹ID.equals("EW"))
                continue;
            String isinCode = info.isinCode;
            String shortCode = info.shortCode;
            String korean = info.productName;
            ELWExtraInfo eei = ELWExtraInfoCenter.Instance.getELWExtraInfo(isinCode);
            ELWIdentifier eid = ELWIdentifierCenter.Instance.getELWId(isinCode);
            if (eei != null && eid != null) {
                String uKorean = isinToKoreanMap.get(eei.기초자산1);
                if (uKorean != null)
                    korean = eid.getNormalizedOptionName(uKorean);
            }
            aliasToIsinMap.put(shortCode, isinCode);
            isinToAliasMap.put(isinCode, shortCode);
            isinToKoreanMap.put(isinCode, korean);
            koreanToIsinMap.put(korean, isinCode);
        }
    }

    void initStock() {
        for (EquityInfo info : EquityInfoCenter.Instance.getAllEquityInfo()) {
            if (!info.isinCode.startsWith("KR7"))
                continue;
            String isinCode = info.isinCode;
            String korean = info.productName;

            String shortCode = isinCode.substring(3, 9).intern();
            aliasToIsinMap.put(shortCode, isinCode);
            isinToAliasMap.put(isinCode, shortCode);
            isinToKoreanMap.put(isinCode, korean);
            koreanToIsinMap.put(korean, isinCode);
        }
    }

    void writeIsinCodeToKoreanToDB() {
        isinToKoreanMap.forEach((k, v) -> {
            Document query = new Document("isinCode", k);
            Document doc = new Document("isinCode", k);
            doc.put("korean", v);
            doc.put("lastUpdate", new Date());
            DBCenter.Instance.updateBulk(MongoDBDBName.MANAGE_DATA, MongoDBCollectionName.ISINCODE_TO_KOREAN, query, doc);
        });

    }

    public void initIndexClosing() {
        // 지수 Closing 가격을 얻기 위함...
        // 현재 지수 Closing 은 시세로부터 받는 수밖에 없는데 시세 패킷 내에서의 키와 지수의 IsinCode 값이 일치하지 않는다.
        // 따라서 지수 Closing 시세를 남기려면 Batch 의 IsinAlias 에 꼭 해당 지수의 isinCode 를 추가해 주어야 한다.
        // 추가해주면 알아서 다음날 종가부터 반영 된다.
        var col = DBCenter.Instance.findIterable(MongoDBDBName.MANAGE_DATA, MongoDBCollectionName.ISIN_ALIAS);
        for (Document doc : col) {
            String alias = doc.getString("alias").intern();
            String isinCode = doc.getString("isinCode").intern();
            String korean = doc.getString("name").intern();
            aliasToIsinMap.put(alias, isinCode);
            isinToAliasMap.put(isinCode, alias);
            isinToKoreanMap.put(isinCode, korean);
            koreanToIsinMap.put(korean, isinCode);
//            DefaultLogger.logger.info("Init Index : {} {}", alias, isinCode);
        }
    }

    public String getIsinFromAlias(String nick) {
        return aliasToIsinMap.get(nick);
    }

    public String getAliasFromIsin(String isin) {
        return isinToAliasMap.get(isin);
    }

    public String getKoreanFromIsin(String isin) {
        String korean = isinToKoreanMap.get(isin);
        if (korean == null)
            return isin;
        return korean;
    }

    public String getAliasFromKorean(String korean) {
        String isin = koreanToIsinMap.get(korean);

        if (isin == null)
            return null;

        return getAliasFromIsin(isin);
    }

    public String getIsinFromKorean(String korean) {
        return koreanToIsinMap.get(korean);
    }
}
