package bulls.staticData.ELW;

import bulls.bs.CallPut;
import bulls.designTemplate.EarlyInitialize;
import bulls.exception.CodeNotFoundException;
import bulls.exception.NoClosingPriceException;
import bulls.log.DefaultLogger;
import bulls.staticData.*;
import bulls.staticData.ProdType.ProdType;
import bulls.staticData.tick.TickCalculatorCenter;
import org.apache.commons.math3.util.FastMath;

import java.util.*;
import java.util.stream.Collectors;

public enum ELWIdentifierCenter implements EarlyInitialize {
    Instance;


    final HashSet<ELWIdentifier> elwSet = new HashSet<>();
    final HashMap<ELWIdentifier, ELWIdentifier> parityMap = new HashMap<>();

    // key 가 종목코드가 아니라 ELWIdentifier 의 getKey()
    final HashMap<String, ELWIdentifier> keyToIdMap = new HashMap<>();
    // <종목 코드, ELWIdentifier>
    final HashMap<String, ELWIdentifier> codeToIdMap = new HashMap<>();
    final HashSet<String> stockELWUnderlyingSet = new HashSet<>();
    //주식옵션 종목코드
    final HashSet<String> stockELWCodeSet = new HashSet<>();
    //K200 지수옵션(미니포함) 종목코드
    final HashSet<String> k200ELWSet = new HashSet<>();
    final HashSet<String> kosdaq150ELWSet = new HashSet<>();

    final HashMap<ELWIssuer, HashMap<OptionIdentifier, ELWIdentifier>> elwOptionMapByIssuer = new HashMap<>();

    final HashMap<OptionIdentifier, HashMap<ELWIssuer, ELWIdentifier>> optElwMap = new HashMap<>();


    // <옵션코드, 언더라잉>
    final HashMap<String, String> elwCodeUnderlyingMap = new HashMap<>();


    ELWIdentifierCenter() {
//        DBCollection col = MyMongoDB.Instance.getCollection(TempConf.DBNAME_BATCH, TempConf.COLLECTIONNAME_ELW_INFO);
//        BasicDBObject queryStockOption = new BasicDBObject();

        Collection<ELWExtraInfo> infos = ELWExtraInfoCenter.Instance.getAllELWExtraInfo();
        // 주식 옵션만 가져오자
//        queryStockOption.append("type", FeedTRCode.A0025.getTrCodeStr());
//        DBCursor result = col.find(queryStockOption);
        for (ELWExtraInfo info : infos) {
            CallPut cp = info.콜풋.equals("C") ? CallPut.CALL : CallPut.PUT;
            EquityInfo ei = EquityInfoCenter.Instance.getEquityInfo(info.isinCode);
            if (ei == null) {
                DefaultLogger.logger.error("EquityInfo of ELW {} not exists", info.isinCode);
                continue;
            }
            String elwShortCode = ei.shortCode;
            String isinCode = info.isinCode;
            String underlyingIsinCode = info.기초자산1;
            String maturityDate = info.최종거래일자 + "";
            Double strikePrice = ei.ELW행사가격;
            Double multiplier = 1.0;
            String type = info.type;

            ELWIssuer issuer = ELWIssuer.parse(info.발행사);

            // batch 종료 패킷
            if (underlyingIsinCode.equals("999999999999"))
                continue;
            String matDate = maturityDate;
            ELWIdentifier elwID = null;
            // 주식 ELW 초기화
            if (underlyingIsinCode.startsWith("KR7")) {
                int closingPrice;
                try {
                    closingPrice = ClosingPriceCenter.Instance.getClosingPrice(underlyingIsinCode);
                } catch (NoClosingPriceException e) {
                    DefaultLogger.logger.error("error found", e);
                    DefaultLogger.logger.error("{} {} {} {}", underlyingIsinCode, isinCode, type, maturityDate);
                    continue;
                }

                var strikeTickFunc = TickCalculatorCenter.Instance.getStrikeTickFunction(ProdType.KOSPIStockCallOptions);
                int atm = strikeTickFunc.getNearestNormalizedPrice(closingPrice);
                int strike = strikePrice.intValue();
                //String productName = issuer.toString() +"_"+ AliasManager.Instance.getKoreanFromIsin(underlyingIsinCode) +"_"+ maturityDate.substring(0,7)+"_"+cp +"_"+ strike;
                String productName = ei.productName;
                elwID = ELWIdentifier.createStockELW(issuer, underlyingIsinCode, strike, isinCode, elwShortCode, atm, productName, multiplier, matDate, cp);
                elwSet.add(elwID);
                keyToIdMap.put(elwID.getKey(), elwID);
                elwCodeUnderlyingMap.put(isinCode, underlyingIsinCode);
                codeToIdMap.put(isinCode, elwID);

                stockELWCodeSet.add(isinCode);
                stockELWUnderlyingSet.add(underlyingIsinCode);
//                DefaultLogger.logger.info("StockELW : {} ",elwID);
            } else if (underlyingIsinCode.equals(PredefinedIsinCode.KOSPI_200)) {
                int closingPrice;
                try {
                    closingPrice = ClosingPriceCenter.Instance.getClosingPrice(underlyingIsinCode);
                } catch (NoClosingPriceException e) {
                    DefaultLogger.logger.error("error found", e);
                    DefaultLogger.logger.error("{} {} {} {}", underlyingIsinCode, isinCode, elwShortCode, type, maturityDate);
                    continue;
                }

                var strikeFunc = TickCalculatorCenter.Instance.getStrikeTickFunction(ProdType.K200CallOption);
                int atm = strikeFunc.getNearestNormalizedPrice(closingPrice);
                int strike = (int) FastMath.round(strikePrice * 100);
                //String productName = issuer.toString() +"_"+ AliasManager.Instance.getKoreanFromIsin(underlyingIsinCode) +"_"+ maturityDate.substring(2,6)+"_"+cp +"_"+ strike;
                String productName = ei.productName;
                elwID = ELWIdentifier.createK200ELW(issuer, underlyingIsinCode, strike, isinCode, elwShortCode, atm, productName, multiplier, matDate, cp);
                elwSet.add(elwID);
                k200ELWSet.add(isinCode);
                keyToIdMap.put(elwID.getKey(), elwID);
                elwCodeUnderlyingMap.put(isinCode, underlyingIsinCode);
                codeToIdMap.put(isinCode, elwID);
//                DefaultLogger.logger.info("IndexELW : {} ",elwID);
            } else if (underlyingIsinCode.equals(PredefinedIsinCode.KOSDAQ_150)) {
                int closingPrice;
                try {
                    closingPrice = ClosingPriceCenter.Instance.getClosingPrice(underlyingIsinCode);
                } catch (NoClosingPriceException e) {
                    DefaultLogger.logger.error("error found", e);
                    DefaultLogger.logger.error("{} {} {} {}", underlyingIsinCode, isinCode, type, maturityDate);
                    continue;
                }

                var strikeFunc = TickCalculatorCenter.Instance.getStrikeTickFunction(ProdType.KQ150CallOption);
                int atm = strikeFunc.getNearestNormalizedPrice(closingPrice);
                int strike = (int) FastMath.round(strikePrice * 100);
                //String productName = issuer.toString() +"_"+ AliasManager.Instance.getKoreanFromIsin(underlyingIsinCode) +"_"+ maturityDate.substring(0,7)+"_"+cp +"_"+ strike;
                String productName = ei.productName;
                elwID = ELWIdentifier.createKosdaq150ELW(issuer, underlyingIsinCode, strike, isinCode, elwShortCode, atm, productName, multiplier, matDate, cp);
                elwSet.add(elwID);
                kosdaq150ELWSet.add(isinCode);
                keyToIdMap.put(elwID.getKey(), elwID);
                elwCodeUnderlyingMap.put(isinCode, underlyingIsinCode);
                codeToIdMap.put(isinCode, elwID);
            } else {
                DefaultLogger.logger.error("처리되지 않은 ELW 정보입니다");
            }
            if (elwID != null && elwID.listedOptionId != null) {
                if (!elwOptionMapByIssuer.containsKey(issuer))
                    elwOptionMapByIssuer.put(issuer, new HashMap<>());
                HashMap<OptionIdentifier, ELWIdentifier> elwOptMap = elwOptionMapByIssuer.get(issuer);
                if (!elwOptMap.containsKey(elwID.listedOptionId))
                    elwOptMap.put(elwID.listedOptionId, elwID);
                if (!optElwMap.containsKey(elwID.listedOptionId))
                    optElwMap.put(elwID.listedOptionId, new HashMap<>());
                optElwMap.get(elwID.listedOptionId).put(elwID.issuer, elwID);
            }
        }

        fillParity();
    }

    private void fillParity() {

        for (ELWIdentifier id : keyToIdMap.values()) {
            String key = id.getParityKey();
            ELWIdentifier parityELW = keyToIdMap.get(key);
//            DefaultLogger.logger.debug(" {} pairs to {}", id.getKey(), parityStockOption.getKey());
            if (parityELW != null)
                parityMap.put(id, parityELW);
        }
    }


    public ELWIdentifier getParityOption(ELWIdentifier id) {
        return parityMap.get(id);
    }

    public Set<ELWIdentifier> getAllOptionOnDutyOf(ELWIssuer issuer, String underlyingIsinCode, long expiryId) {
        return elwSet.stream()
                .filter(id -> id.issuer == issuer)
                .filter(id -> id.getUnderlyingCode().equals(underlyingIsinCode))
                .filter(id -> id.isOnDutyToday)
                .filter(id -> id.expiryId == expiryId)
                .collect(Collectors.toSet());
    }

    public Set<ELWIdentifier> getAllOptionOf(ELWIssuer issuer, String underlyingIsinCode, long expiryId) {
        return elwSet.stream()
                .filter(id -> id.issuer == issuer)
                .filter(id -> id.getUnderlyingCode().equals(underlyingIsinCode))
                .filter(id -> id.expiryId == expiryId)
                .collect(Collectors.toSet());
    }

    public boolean isStockELWUnderlying(String stockIsinCode) {
        return stockELWUnderlyingSet.contains(stockIsinCode);
    }

    public Set<String> getAllStockELWUnderlying() {
        return stockELWUnderlyingSet;
    }

    public ELWIdentifier getELWCode(ELWIssuer issuer, String underlyingIsinCode, char yearCode, char monthCode, int strike, CallPut callPut) throws CodeNotFoundException {

        String key = ELWIdentifier.getKey(issuer, underlyingIsinCode, yearCode, monthCode, strike, callPut, false);
        ELWIdentifier id = keyToIdMap.get(key);
        if (id == null) {
            throw new CodeNotFoundException(key + " 에 해당하는 옵션코드가 없습니다.");
        }
        return id;
    }

    public boolean isStockELW(String code) {
        return stockELWCodeSet.contains(code);
    }

    public boolean isIndexELW(String code) {
        return (k200ELWSet.contains(code) || kosdaq150ELWSet.contains(code));
    }

    public boolean isApptUnderlying(String elwCode, String stockCode) {
        String underLyingCode = elwCodeUnderlyingMap.get(elwCode);

        if (underLyingCode == null)
            return false;

        return underLyingCode.equals(stockCode);
    }

    public ELWIdentifier getELWId(String elwCode) {
        return codeToIdMap.get(elwCode);
    }

    public HashMap<ELWIssuer, ELWIdentifier> getELWIdMapOfOpt(OptionIdentifier oid) {
        return optElwMap.get(oid);
    }

    public Collection<ELWIdentifier> getELWOfOpt(OptionIdentifier oid) {
        return optElwMap.get(oid).values();
    }

    public Set<Map.Entry<String, ELWIdentifier>> getAllELWOf() {
        return codeToIdMap.entrySet();
    }

    public Set<String> getAllK200ELW() {
        return k200ELWSet;
    }

    public Set<String> getAllKosdaq150ELW() {
        return kosdaq150ELWSet;
    }
}
