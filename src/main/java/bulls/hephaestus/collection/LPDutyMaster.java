package bulls.hephaestus.collection;

import bulls.bs.CallPut;
import bulls.dateTime.DateCenter;
import bulls.dateTime.TimeCenter;
import bulls.db.mongodb.DBCenter;
import bulls.db.mongodb.MongoDBCollectionName;
import bulls.db.mongodb.MongoDBDBName;
import bulls.hephaestus.collection.enums.LpCalendarType;
import bulls.staticData.FuturesInfo;
import bulls.staticData.FuturesInfoCenter;
import bulls.staticData.OptionIdentifier;
import bulls.staticData.OptionIdentifierCenter;
import bulls.staticData.ProdType.DerivativesProdClassType;
import bulls.staticData.ProdType.DerivativesUnderlyingType;
import org.bson.Document;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum LPDutyMaster {
    Instance;

    final ArrayList<LpCalendar> monthlyPeriod = new ArrayList<>();
    final ArrayList<LpCalendar> monthlyVkospiPeriod = new ArrayList<>();
    final ArrayList<LpCalendar> quarterlyPeriod = new ArrayList<>();
    final HashMap<String, RawLpDutyRuleFutures> lpRuleFutMap = new HashMap<>();
    final HashMap<String, RawLpDutyRuleOptions> lpRuleOptMap = new HashMap<>();
    final HashMap<String, LpDutyRuleFutures> lpDutyRuleFutMapPerItem = new HashMap<>();
    final HashMap<String, LpDutyRuleFutures> lpNonDutyRuleFutMapPerItem = new HashMap<>();
    final HashMap<String, LpDutyRuleOptions> lpDutyRuleOptMapPerItem = new HashMap<>();
    final HashMap<String, LpDutyRuleOptions> lpNonDutyRuleOptMapPerItem = new HashMap<>();
    final HashMap<String, Set<String>> lpDutyFutFindMap = new HashMap<>();
    final HashMap<String, Set<String>> lpNonDutyFutFindMap = new HashMap<>();
    final HashMap<String, Set<String>> lpDutyOptFindMap = new HashMap<>();
    final HashMap<String, Set<String>> lpNonDutyOptFindMap = new HashMap<>();

    final HashSet<DerivativesUnderlyingType> soDutSetOnDuty = new HashSet<>();
    final HashSet<String> soUnderlyingIsinCodeSetOnDuty = new HashSet<>();
    final HashSet<DerivativesUnderlyingType> soDutSetOnNonDuty = new HashSet<>();
    final HashSet<String> soUnderlyingIsinCodeSetOnNonDuty = new HashSet<>();

    final HashSet<String> sfUnderlyingIsinCodeSetOnDuty = new HashSet<>();
    final HashSet<String> sfUnderlyingIsinCodeSetOnNonDuty = new HashSet<>();

    LPDutyMaster() {
        Document query = new Document("dateType", "LP의무");
        var col = DBCenter.Instance.findIterable(MongoDBDBName.MANAGE_DATA, MongoDBCollectionName.CALENDAR, Document.class, query);
        for (Document d : col) {
            Integer beginInt = d.getInteger("beginDate");
            Integer endInt = d.getInteger("endDate");
            String target = d.getString("target");

            if (beginInt == null || endInt == null)
                continue;

            try {
                Date dateBegin = DateCenter.Instance.parse_yyyyMMdd(beginInt.toString());
                LocalDate ldBegin = TimeCenter.getDateAsLocalDateType(dateBegin);
                Date dateEnd = DateCenter.Instance.parse_yyyyMMdd(endInt.toString());
                LocalDate ldEnd = TimeCenter.getDateAsLocalDateType(dateEnd);

                String dateSubType = d.getString("dateSubType");

                switch (dateSubType) {
                    case "M" ->
                            monthlyPeriod.add(new LpCalendar(LpCalendarType.DUTY, dateSubType, target, ldBegin, ldEnd));
                    case "Q" ->
                            quarterlyPeriod.add(new LpCalendar(LpCalendarType.DUTY, dateSubType, target, ldBegin, ldEnd));
                    case "M_Vkospi" ->
                            monthlyVkospiPeriod.add(new LpCalendar(LpCalendarType.DUTY, dateSubType, target, ldBegin, ldEnd));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        //선물의무, 한화의무(Duty)와 타사의무(NonDuty)를 구분하며 저장
        var futuresCol = DBCenter.Instance.findIterable(MongoDBDBName.LP, MongoDBCollectionName.FUTURES_DUTY_RULES, RawLpDutyRuleFutures.class);
        for (RawLpDutyRuleFutures v : futuresCol) {
//            if (v.DutyEndDate.before(TimeCenter.Instance.getDateAsDateType()))
//                continue;
            lpRuleFutMap.put(getLpDutyRuleKey(v.underlyingID, v.대상월물번호), v);
            DerivativesUnderlyingType dut = DerivativesUnderlyingType.getTypeFromDutCode(v.underlyingID);
            FuturesInfo fi = FuturesInfoCenter.Instance.getFutures(dut, v.대상월물번호);
            if (fi == null)
                continue;

            if (v.isMyDuty)
                lpDutyRuleFutMapPerItem.put(fi.isinCode, new LpDutyRuleFutures(fi.isinCode, v));
            else
                lpNonDutyRuleFutMapPerItem.put(fi.isinCode, new LpDutyRuleFutures(fi.isinCode, v));

            if (DerivativesProdClassType.getTypeFromCode(dut) != DerivativesProdClassType.개별주식)
                continue;

            if (v.isMyDuty)
                sfUnderlyingIsinCodeSetOnDuty.add(fi.underlyingIsinCode);
            else
                sfUnderlyingIsinCodeSetOnNonDuty.add(fi.underlyingIsinCode);
        }

        for (var rule : lpDutyRuleFutMapPerItem.values()) {
            FuturesInfo info = FuturesInfoCenter.Instance.getFuturesInfo(rule.isinCode);
            if (info == null)
                continue;

            lpDutyFutFindMap.computeIfAbsent(info.underlyingIsinCode, k -> new HashSet<>()).add(info.isinCode);
        }
        for (var rule : lpNonDutyRuleFutMapPerItem.values()) {
            FuturesInfo info = FuturesInfoCenter.Instance.getFuturesInfo(rule.isinCode);
            if (info == null)
                continue;

            lpNonDutyFutFindMap.computeIfAbsent(info.underlyingIsinCode, k -> new HashSet<>()).add(info.isinCode);
        }

        //옵션의무, 한화의무(Duty)와 타사의무(NonDuty)를 구분하며 저장
        var optionsCol = DBCenter.Instance.findIterable(MongoDBDBName.LP, MongoDBCollectionName.OPTION_DUTY_RULES, RawLpDutyRuleOptions.class);
        for (RawLpDutyRuleOptions v : optionsCol) {
            String[] 의무수량StrArr = v.의무수량.split(",");
            v.의무수량Array = new int[의무수량StrArr.length];
            for (int i = 0; i < 의무수량StrArr.length; ++i) {
                v.의무수량Array[i] = Integer.parseInt(의무수량StrArr[i]);
            }
            String[] 의무수량MoneynessStrArr = v.의무수량Moneyness.split(",");
            v.의무수량MoneynessArray = new int[의무수량MoneynessStrArr.length];
            for (int i = 0; i < 의무수량MoneynessStrArr.length; ++i) {
                v.의무수량MoneynessArray[i] = Integer.parseInt(의무수량MoneynessStrArr[i]);
            }
            lpRuleOptMap.put(getLpDutyRuleKey(v.underlyingID, v.대상월물번호), v);
            DerivativesUnderlyingType dut = DerivativesUnderlyingType.getTypeFromDutCode(v.underlyingID);
            for (int i = 0; i < v.의무수량Array.length; ++i) {
                int amt = v.의무수량Array[i];
                int moneyness = v.의무수량MoneynessArray[i];
                List<OptionIdentifier> list = OptionIdentifierCenter.Instance.getOptionsList(CallPut.CALL, dut, v.대상월물번호, moneyness, moneyness);
                if (list != null && list.size() > 0) {
                    String isinCode = list.get(0).getIsinCode();
                    if (v.isMyDuty)
                        lpDutyRuleOptMapPerItem.put(isinCode, new LpDutyRuleOptions(isinCode, moneyness, amt, v));
                    else
                        lpNonDutyRuleOptMapPerItem.put(isinCode, new LpDutyRuleOptions(isinCode, moneyness, amt, v));
                }
                int putMoneyness = -moneyness;
                List<OptionIdentifier> putList = OptionIdentifierCenter.Instance.getOptionsList(CallPut.PUT, dut, v.대상월물번호, putMoneyness, putMoneyness);
                if (putList != null && putList.size() > 0) {
                    String isinCode = putList.get(0).getIsinCode();
                    if (v.isMyDuty)
                        lpDutyRuleOptMapPerItem.put(isinCode, new LpDutyRuleOptions(isinCode, putMoneyness, amt, v));
                    else
                        lpNonDutyRuleOptMapPerItem.put(isinCode, new LpDutyRuleOptions(isinCode, putMoneyness, amt, v));
                }
            }
            if (DerivativesProdClassType.getTypeFromCode(dut) != DerivativesProdClassType.개별주식)
                continue;

            if (v.isMyDuty) {
                soDutSetOnDuty.add(dut);
                soUnderlyingIsinCodeSetOnDuty.add(v.underlyingIsinCode);
            } else {
                soDutSetOnNonDuty.add(dut);
                soUnderlyingIsinCodeSetOnNonDuty.add(v.underlyingIsinCode);
            }
        }

        for (var rule : lpDutyRuleOptMapPerItem.values()) {
            FuturesInfo info = FuturesInfoCenter.Instance.getFuturesInfo(rule.isinCode);
            if (info == null)
                continue;

            lpDutyOptFindMap.computeIfAbsent(info.underlyingIsinCode, k -> new HashSet<>()).add(info.isinCode);
        }

        for (var rule : lpNonDutyRuleOptMapPerItem.values()) {
            FuturesInfo info = FuturesInfoCenter.Instance.getFuturesInfo(rule.isinCode);
            if (info == null)
                continue;

            lpNonDutyOptFindMap.computeIfAbsent(info.underlyingIsinCode, k -> new HashSet<>()).add(info.isinCode);
        }
    }

    public LpCalendar getMonthlyCalendar(LocalDate date) {
        for (LpCalendar lpCalendar : monthlyPeriod) {
            if (lpCalendar.contains(date))
                return lpCalendar;
        }
        return null;
    }

    public LpCalendar getMonthlyVkospiCalendar(LocalDate date) {
        for (LpCalendar lpCalendar : monthlyVkospiPeriod) {
            if (lpCalendar.contains(date))
                return lpCalendar;
        }
        return null;
    }

    public LpCalendar getQuarterlyCalendar(LocalDate date) {
        for (LpCalendar lpCalendar : quarterlyPeriod) {
            if (lpCalendar.contains(date))
                return lpCalendar;
        }
        return null;
    }


    public RawLpDutyRuleFutures getRawLpDutyRuleFut(DerivativesUnderlyingType dut, int maturityOrder) {
        return lpRuleFutMap.get(getLpDutyRuleKey(dut, maturityOrder));
    }

    public RawLpDutyRuleOptions getRawLpDutyRuleOpt(DerivativesUnderlyingType dut, int maturityOrder) {
        return lpRuleOptMap.get(getLpDutyRuleKey(dut, maturityOrder));
    }

    public Set<String> getLpDutyOptIsinCodesByUnderlyingIsinCode(String underlyingIsinCode) {
        return lpDutyOptFindMap.get(underlyingIsinCode);
    }

    public Set<String> getLpNonDutyOptIsinCodesByUnderlyingIsinCode(String underlyingIsinCode) {
        return lpNonDutyOptFindMap.get(underlyingIsinCode);
    }

    public HashMap<String, Set<String>> getAllLPDutyOptCodeMap() {
        return lpDutyOptFindMap;
    }

    public HashMap<String, Set<String>> getAllLPNonDutyOptCodeMap() {
        return lpNonDutyOptFindMap;
    }

    public Set<String> getLpDutyFutIsinCodesByUnderlyingIsinCode(String underlyingIsinCode) {
        return lpDutyFutFindMap.get(underlyingIsinCode);
    }

    public HashMap<String, Set<String>> getAllLPDutyFutCodeMap() {
        return lpDutyFutFindMap;
    }

    public LpDutyRuleFutures getLpDutyRuleFut(String isinCode) {
        return lpDutyRuleFutMapPerItem.get(isinCode);
    }

    public LpDutyRuleOptions getLpDutyRuleOpt(String isinCode) {
        return lpDutyRuleOptMapPerItem.get(isinCode);
    }

    private String getLpDutyRuleKey(String underlyingID, int maturityOrder) {
        return underlyingID + "_" + maturityOrder;
    }

    private String getLpDutyRuleKey(DerivativesUnderlyingType dut, int maturityOrder) {
        return dut.toString() + "_" + maturityOrder;
    }

    public Collection<LpDutyRuleFutures> getAllLpDutyRuleFut() {
        return lpDutyRuleFutMapPerItem.values();
    }

    public Collection<LpDutyRuleOptions> getAllLpDutyRuleOpt() {
        return lpDutyRuleOptMapPerItem.values();
    }

    public Collection<LpDutyRuleOptions> getAllLpDutyOrNonDutyRuleOpt() {
        Collection<LpDutyRuleOptions> duty = lpDutyRuleOptMapPerItem.values();
        Collection<LpDutyRuleOptions> nonDuty = lpNonDutyRuleOptMapPerItem.values();
        return Stream.concat(duty.stream(), nonDuty.stream()).collect(Collectors.toList());
    }

    public Collection<DerivativesUnderlyingType> getAllSODutOnDuty() {
        return soDutSetOnDuty;
    }

    public Collection<DerivativesUnderlyingType> getAllSODutOnNonDuty() {
        return soDutSetOnNonDuty;
    }

    public Set<String> getAllSFUnderlyingIsinCodeOnDuty() {
        return sfUnderlyingIsinCodeSetOnDuty;
    }

    public boolean isSOUnderlyingOnDuty(String underlyingIsinCode) {
        return soUnderlyingIsinCodeSetOnDuty.contains(underlyingIsinCode);
    }

    public boolean isSOUnderlyingOnNonDuty(String underlyingIsinCode) {
        return soUnderlyingIsinCodeSetOnNonDuty.contains(underlyingIsinCode);
    }

    public boolean isSFUnderlyingOnDuty(String underlyingIsinCode) {
        return sfUnderlyingIsinCodeSetOnDuty.contains(underlyingIsinCode);
    }

    public List<Integer> getStrikes(String underlyingIsinCode, boolean isDuty) {
        Set<Integer> strikes = new HashSet<>();
        Set<String> optIsinCodeSet = isDuty ? lpDutyOptFindMap.get(underlyingIsinCode) : lpNonDutyOptFindMap.get(underlyingIsinCode);
        if (optIsinCodeSet == null)
            return new ArrayList<>();

        for (String optIsinCode : optIsinCodeSet) {
            FuturesInfo info = FuturesInfoCenter.Instance.getFuturesInfo(optIsinCode);
            strikes.add(info.strikePrice.intValue());
        }

        return new ArrayList<>(strikes);
    }

    public Set<String> getAllSOUnderlyingIsinCodeOnDuty() {
        return soUnderlyingIsinCodeSetOnDuty;
    }

    public Set<String> getAllSOUnderlyingIsinCodeOnNonDuty() {
        return soUnderlyingIsinCodeSetOnNonDuty;
    }
}
