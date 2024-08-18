package bulls.staticData;

import bulls.bs.CallPut;
import bulls.dateTime.TimeCenter;
import bulls.db.mongodb.DBCenter;
import bulls.db.mongodb.MongoDBCollectionName;
import bulls.db.mongodb.MongoDBDBName;
import bulls.designTemplate.EarlyInitialize;
import bulls.exception.NoClosingPriceException;
import bulls.json.DefaultMapper;
import bulls.log.DefaultLogger;
import bulls.server.ServerMessageSender;
import bulls.staticData.ProdType.DerivativesProdClassType;
import bulls.staticData.ProdType.DerivativesUnderlyingType;
import org.bson.Document;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public enum FuturesInfoCenter implements EarlyInitialize {
    Instance;

    HashMap<String, Double> mulMap;
    public final HashMap<String, FuturesInfo> futuresInfoMap = new HashMap<>();

    FuturesInfoCenter() {
        loadWith(TimeCenter.Instance.today);
    }

    // 이 날짜 이후의 데이터 로드
    public void loadWith(LocalDate localDate) {
        boolean useCustomMaturityDate = TempConf.FORCE_FUTURES_MATURITY_ENABLED;
        if (useCustomMaturityDate) {
            int customMaturityDate = TempConf.FORCE_FUTURES_MATURITY_DATE;
            int year = customMaturityDate / 10000;
            int month = (customMaturityDate % 10000) / 100;
            int day = customMaturityDate % 100;
            if (year < 1900 || year > 2100 || month < 0 || month > 12 || day < 0 || day > 31)
                DefaultLogger.logger.error("FuturesInfoCenter 강제만기설정 옵션이 켜졌지만 날짜가 잘못되어 불러오지 않습니다 : {}", customMaturityDate);
            else {
                localDate = LocalDate.of(year, month, day);
                DefaultLogger.logger.info("FuturesInfoCenter 만기가 {}로 설정되었습니다", localDate);
            }
        }

        mulMap = new HashMap<>();

        Document query = new Document();
        Document q1 = new Document("만기", new Document("$gte", TimeCenter.getLocalDateAsDateType(localDate)));
        LocalDate nextDate = localDate.plusDays(7 * 30);
        Document q2 = new Document("만기", new Document("$lte", TimeCenter.getLocalDateAsDateType(nextDate)));
        ArrayList<Document> queryList = new ArrayList<>();
        queryList.add(q1);
        queryList.add(q2);
        query.append("$and", queryList);

        var col = DBCenter.Instance.findIterable(MongoDBDBName.BATCH, MongoDBCollectionName.DERIV_INFO, Document.class, query);
        for (Document doc : col) {
            try {
                FuturesInfo info = DefaultMapper.getMongoDocumentMapper().fromJson(doc.toJson(), FuturesInfo.class);
                if (info == null)
                    continue;
                if (info.기초자산ID == null) {
                    DefaultLogger.logger.error("기초자산ID null. 신규 추가된 기초자산이 없는지 확인");
                    ServerMessageSender.writeServerMessage(this.getClass(), "OracleArena", "FuturesInfo초기화오류", "기초자산ID null : " + info.isinCode);
                }

                // to elim KR49999999KP
                if (info.underlyingIsinCode.equals(PredefinedIsinCode.KOSPI_200_ALT))
                    info.underlyingIsinCode = PredefinedIsinCode.KOSPI_200;

                info.isinCode = info.isinCode.intern();
                info.matDate = info.matDate.intern();
                if (info.underlyingIsinCode != null)
                    info.underlyingIsinCode = info.underlyingIsinCode.intern();
                if (info.type != null)
                    info.type = info.type.intern();
                if (info.spreadRecentIsin != null)
                    info.spreadRecentIsin = info.spreadRecentIsin.intern();
                if (info.spreadNext != null)
                    info.spreadNext = info.spreadNext.intern();

                info.prodClassType = DerivativesProdClassType.getTypeFromCode(info.기초자산ID);
                if (info.isinCode != null && info.multiplier != null)
                    mulMap.put(info.isinCode, info.multiplier);
                futuresInfoMap.put(info.isinCode, info);

                DefaultLogger.logger.info("{}({}) 파생로딩기준가: {}", info.productName, info.isinCode, info.기준가);
            } catch (Exception e) {
                DefaultLogger.logger.error("파생 로딩 실패 {}", doc.toJson());
                e.printStackTrace();
            }
        }
    }

    public String getIsinCodeFrom(String shortDerivCode) {
        Set<String> set = futuresInfoMap.keySet();
        for (String isin : set) {
            String shortCode = isin.substring(3, 11);
            if (shortCode.equals(shortDerivCode))
                return isin;
        }

        return null;
    }

    public void updateFromFeed(FuturesInfo info){
        futuresInfoMap.put(info.isinCode, info);
    }

    public double get기준가(String isinCode) throws NoClosingPriceException {
        FuturesInfo info = getFuturesInfo(isinCode);
        if (info == null)
            return ClosingPriceCenter.Instance.getClosingPrice(isinCode);

        return info.기준가;
    }

    public FuturesInfo getFuturesInfo(String isinCode) {
        return futuresInfoMap.get(isinCode);
    }

    public double getPureMultiplier(String code) {
        return mulMap.computeIfAbsent(code, k -> {
            DefaultLogger.logger.error("{} 종목의 승수 없음", code);
            return 1d;
        });
    }

    public Collection<FuturesInfo> getAllFuturesInfo() {
        return futuresInfoMap.values();
    }

    public Collection<FuturesInfo> getAllUnexpiredFuturesInfo() {
        Date todayDate = TimeCenter.Instance.getDateAsDateType();
        return futuresInfoMap.values().stream().filter(it -> it.만기.compareTo(todayDate) >= 0).collect(Collectors.toList());
    }

    public List<FuturesInfo> getAllRecentAndNextFuturesInfo() {
        Date todayDate = TimeCenter.Instance.getDateAsDateType();
        HashMap<String, List<FuturesInfo>> futMap = new HashMap<>();
        ArrayList<FuturesInfo> ret = new ArrayList<>();
        //선물 추가
        futuresInfoMap.values().stream()
                .filter(fi -> fi.isinCode.startsWith("KR41") && fi.만기.compareTo(todayDate) >= 0)
                .sorted(Comparator.comparing(FuturesInfo::getMaturityAscii))
                .forEach(it -> {
                    String key = it.isinCode.substring(0, 6);
                    if (!futMap.containsKey(key))
                        futMap.put(key, new ArrayList<>());
                    if (futMap.get(key).size() < 2) {
                        //근월물 원월물 총 2개만 추가
                        futMap.get(key).add(it);
                        ret.add(it);
                    }
                });
        //옵션 추가
        HashMap<String, HashMap<String, List<FuturesInfo>>> optMap = new HashMap<>();
        futuresInfoMap.values().stream()
                .filter(fi -> (fi.isinCode.startsWith("KR42") || fi.isinCode.startsWith("KR43")) && fi.만기.compareTo(todayDate) >= 0)
                .sorted(Comparator.comparing(FuturesInfo::getMaturityAscii))
                .forEach(it -> {
                    String typeKey = it.isinCode.substring(0, 6);
                    String monthKey = it.isinCode.substring(6, 8);
                    if (!optMap.containsKey(typeKey))
                        optMap.put(typeKey, new HashMap<>());
                    HashMap<String, List<FuturesInfo>> tmpMap = optMap.get(typeKey);
                    if (!tmpMap.containsKey(monthKey))
                        tmpMap.put(monthKey, new ArrayList<>());
                    tmpMap.get(monthKey).add(it);
                });
        optMap.values().forEach(map -> {
            SortedSet<String> keys = new TreeSet<>(map.keySet());
            int i = 0;
            for (String key : keys) {
                List<FuturesInfo> l = map.get(key);
                ret.addAll(l);
                i++;
                //근월,원월 총 2개월물만 추가
                if (i == 2)
                    break;
            }
        });
        //스프레드는 최근월-차월
        HashSet<String> spreadPrefixSet = new HashSet<>();
        futuresInfoMap.values().stream()
                .filter(fi -> fi.isinCode.startsWith("KR44") && fi.만기.compareTo(todayDate) >= 0)
                .sorted(Comparator.comparing(v -> v.isinCode))
                .forEach(it -> {
                    String key = it.isinCode.substring(0, 6);
                    if (!spreadPrefixSet.contains(key)) {
                        spreadPrefixSet.add(key);
                        ret.add(it);
                    }
                });
        return ret;
    }

    public List<FuturesInfo> getRecentAndNextFutures(DerivativesUnderlyingType dut) {
        Date todayDate = TimeCenter.Instance.getDateAsDateType();
        ArrayList<FuturesInfo> ret = new ArrayList<>();
        //선물 추가
        futuresInfoMap.values().stream()
                .filter(fi -> fi.기초자산ID == dut && fi.isinCode.startsWith("KR41") && fi.만기.compareTo(todayDate) >= 0)
                .sorted(Comparator.comparing(FuturesInfo::getMaturityAscii))
                .forEach(ret::add);
        return ret;
    }

    public FuturesInfo getNextFuturesOf(DerivativesUnderlyingType dut) {
        return futuresInfoMap.values().stream()
                .filter(fInfo -> fInfo.기초자산ID.equals(dut) && fInfo.isinCode.contains("KR41"))
                .sorted(Comparator.comparing(FuturesInfo::getMaturityAscii))
                .skip(1)
                .findFirst()
                .orElse(null);
    }

    public FuturesInfo getMostRecentFuturesOf(String underlyingIsinCode) {
        return futuresInfoMap.values().stream()
                .filter(fInfo -> fInfo.underlyingIsinCode.equals(underlyingIsinCode) && fInfo.isinCode.contains("KR41"))
                .min(Comparator.comparing(FuturesInfo::getMaturityAscii))
                .orElse(null);
    }

    public FuturesInfo getMostRecentFuturesOf(DerivativesUnderlyingType dut) {
        return futuresInfoMap.values().stream()
                .filter(fInfo -> fInfo.기초자산ID.equals(dut) && fInfo.isinCode.contains("KR41"))
                .min(Comparator.comparing(FuturesInfo::getMaturityAscii))
                .orElse(null);
    }

    /**
     * 기초자산ID와 월물번호로 선물의 FuturesInfo를 조회
     *
     * @param dut            기초자산ID
     * @param maturityNumber 0부터 시작하는 월물번호(근월물=0). DB에 입력되어 있는 종목 정보 기준임. 월물이 1,2,3,6,9월 존재하고 현재 근월물이 1월이라면 maturityNumber 4는 5월이 아니라 9월을 의미.
     * @return 해당되는 FuturesInfo
     */
    public FuturesInfo getFutures(DerivativesUnderlyingType dut, int maturityNumber) {
        ArrayList<FuturesInfo> fi = new ArrayList<>();
        futuresInfoMap.values().stream()
                .filter(fInfo -> fInfo.기초자산ID.equals(dut) && fInfo.isinCode.contains("KR41"))
                .sorted(Comparator.comparing(FuturesInfo::getMaturityAscii))
                .forEach(fi::add);

        if (fi.size() <= maturityNumber)
            return null;

        return fi.get(maturityNumber);
    }

    /**
     * 해당 종목의 월물 번호를 반환.
     *
     * @param info FuturesInfo
     * @return 해당 종목의 월물 번호
     */
    public int getMaturityNumber(FuturesInfo info) {
        DerivativesUnderlyingType dut = info.기초자산ID;
        ArrayList<FuturesInfo> fi = new ArrayList<>();
        futuresInfoMap.values().stream()
                .filter(fInfo -> fInfo.기초자산ID.equals(dut) && fInfo.isinCode.contains("KR41"))
                .sorted(Comparator.comparing(FuturesInfo::getMaturityAscii))
                .forEach(fi::add);

        for (int i = 0; i < fi.size(); i++)
            if (fi.get(i).isinCode.equals(info.isinCode))
                return i;

        return -1;
    }


    /**
     * 옵션 종류, 기초자산ID, 월물번호로 ATM 옵션의 FuturesInfo 조회
     *
     * @param dut            기초자산ID
     * @param callPut        옵션 종류
     * @param maturityNumber 0부터 시작하는 월물번호(근월물=0). DB에 입력되어 있는 종목 정보 기준임. 월물이 1,2,3,6,9월 존재하고 현재 근월물이 1월이라면 maturityNumber 4는 5월이 아니라 9월을 의미.
     * @return 주어진 조건에 맞는 ATM 옵션의 FuturesInfo
     */
    public FuturesInfo getATMOption(DerivativesUnderlyingType dut, CallPut callPut, int maturityNumber) {
        if (maturityNumber < 0)
            return null;

        String filterString;
        if (callPut == CallPut.CALL)
            filterString = "KR42";
        else
            filterString = "KR43";

        ArrayList<FuturesInfo> infoList = new ArrayList<>();
        futuresInfoMap.values().stream()
                .filter(fInfo -> fInfo.기초자산ID.equals(dut) && fInfo.isinCode.contains(filterString) && fInfo.ATM구분 != null && fInfo.ATM구분 == 1)
                .sorted(Comparator.comparing(FuturesInfo::getMaturityAscii))
                .forEach(infoList::add);

        if (infoList.size() <= maturityNumber)
            return null;

        return infoList.get(maturityNumber);
    }
}
