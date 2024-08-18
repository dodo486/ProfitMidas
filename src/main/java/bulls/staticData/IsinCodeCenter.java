package bulls.staticData;

import bulls.bs.CallPut;
import bulls.bs.OptionCodeInfo;
import bulls.dateTime.TimeCenter;
import bulls.exception.IllegalTrCodeException;
import bulls.hephaestus.collection.ExpiryMaster;
import bulls.hephaestus.collection.LpCalendar;
import bulls.order.enums.LongShort;
import bulls.staticData.ProdType.DerivativesUnderlyingType;
import bulls.staticData.ProdType.ProdType;
import bulls.staticData.ProdType.ProdTypeCenter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class IsinCodeCenter {

    private static final HashMap<String, Integer> propertyCode = new HashMap<>();
    private static final HashMap<String, Integer> futureOptionCode = new HashMap<>();
    public static Map<String, String> optionHumanCodeMap = new HashMap<>();
    public static Map<String, String> opCodeMap = new HashMap<>();
    public static Map<String, String> miniToBigOpCodeMap = new HashMap<>();
    public static Map<String, String> miniToBigCodeMap = new HashMap<>();
    public static Map<String, Integer> strikePriceMap = new HashMap<>();

    public static String recentK200FuturesCode;
    public static String recentKQ150FuturesCode;
    public static String nextKQ150FuturesCode;
    public static String recentKRX300Code;
    public static String recentMini200Code;
    public static String nextMini200Code;
    public static String nextK200FuturesCode;
    //	public static String recentKospi200Code;
    public static byte[] recentKospi200CodeByte;
    public static int recentKospi200DayToMaturity;

    public static String CODETYPE_FUTURE = "Future";
    public static String CODETYPE_CALL = "CallOption";
    public static String CODETYPE_PUT = "PutOption";
    public static String CODETYPE_SPREAD = "FutureSpread";
    public static String CODETYPE_FUTURE_CALL = "FutureCallOption";
    public static String CODETYPE_FUTURE_PUT = "FuturePutOption";
    public static String CODETYPE_FLEX = "FlexFuture";

    public static char recentOptionExpiryMonthCode;
    public static char recentOptionExpiryYearCode;
    public static long recentOptionExpiryId;


    public static char nextOptionExpiryMonthCode;
    public static char nextOptionExpiryYearCode;
    public static long nextOptionExpiryId;


    static {
        propertyCode.put("Option", 4);
        propertyCode.put("Future", 4);

        futureOptionCode.put(CODETYPE_FUTURE, 1);
        futureOptionCode.put(CODETYPE_CALL, 2);
        futureOptionCode.put(CODETYPE_PUT, 3);
        futureOptionCode.put(CODETYPE_SPREAD, 4);
        futureOptionCode.put(CODETYPE_FUTURE_CALL, 5);
        futureOptionCode.put(CODETYPE_FUTURE_PUT, 6);
        futureOptionCode.put(CODETYPE_FLEX, 7);

        LpCalendar month = ExpiryMaster.Instance.getMonthlyCalendar(TimeCenter.Instance.today);
        LpCalendar quarter = ExpiryMaster.Instance.getQuarterlyCalendar(TimeCenter.Instance.today);
        LpCalendar nextMonth = ExpiryMaster.Instance.getNextMonthlyCalendar(TimeCenter.Instance.today);


        int recentMonthByQuarter = quarter.month;
        int recentYearByQuarter = quarter.year;
//		int maturityMonth  = IsinCodeCenter.getFutureExpireMonth();

//        recentK200FuturesCode = IsinCodeCenter.getFuturesCode(recentYearByQuarter, recentMonthByQuarter, FuturesCodeIdentifier.K200);
//        recentKQ150FuturesCode = IsinCodeCenter.getFuturesCode(recentYearByQuarter, recentMonthByQuarter, FuturesCodeIdentifier.KQ150);
//        recentKRX300Code = IsinCodeCenter.getFuturesCode(recentYearByQuarter, recentMonthByQuarter, FuturesCodeIdentifier.KRX300);

        recentK200FuturesCode = IsinCodeCenter.getFuturesCode(recentYearByQuarter, recentMonthByQuarter, FuturesCodeIdentifier.K200);
        recentKQ150FuturesCode = IsinCodeCenter.getFuturesCode(recentYearByQuarter, recentMonthByQuarter, FuturesCodeIdentifier.KQ150);
        recentKRX300Code = IsinCodeCenter.getFuturesCode(recentYearByQuarter, recentMonthByQuarter, FuturesCodeIdentifier.KRX300);
        recentKospi200CodeByte = recentK200FuturesCode.getBytes();


        recentOptionExpiryMonthCode = month.monthCode;
        recentOptionExpiryYearCode = month.yearCode;
        recentOptionExpiryId = ExpiryCenter.Instance.getExpiryId(month);
//		recentKospi200OptionExpireMonth = getOptionExpireMonth();
//		recentOptionExpiryMonthCode =  getMonthCode(recentKospi200OptionExpireMonth).charAt(0);
//		recentOptionExpiryYearCode = getCurrentYearCode(recentKospi200OptionExpireMonth).charAt(0);

        recentMini200Code = IsinCodeCenter.getMiniFuturesCode(recentOptionExpiryYearCode, recentOptionExpiryMonthCode);

        // 옵션 차월만기
        nextOptionExpiryYearCode = month.nextYearCode;
        nextOptionExpiryMonthCode = month.nextMonthCode;
        nextOptionExpiryId = ExpiryCenter.Instance.getExpiryId(nextMonth);

        nextMini200Code = IsinCodeCenter.getMiniFuturesCode(nextOptionExpiryYearCode, nextOptionExpiryMonthCode);

        int nextFutureExpireMonth = quarter.nextMonth;
        int nextFuturesExpiryYear = quarter.nextYear;

        nextK200FuturesCode = IsinCodeCenter.getFuturesCode(nextFuturesExpiryYear, nextFutureExpireMonth, FuturesCodeIdentifier.K200);
        nextKQ150FuturesCode = IsinCodeCenter.getFuturesCode(nextFuturesExpiryYear, nextFutureExpireMonth, FuturesCodeIdentifier.KQ150);
    }

    public static Set<String> getK200OptionSet(int atm, int nFromAtm, char year, char month) {
        Set<String> optionCodeSet = new HashSet<>();
        int strikeMin = ((atm - 250 * nFromAtm) / 100);
        int strikeMax = ((atm + 250 * nFromAtm) / 100);
        int strike = strikeMin;
        while (strike <= strikeMax) {
            String callCode = IsinCodeCenter.getOptionCode(strike, year, month, CallPut.CALL);
            String putCode = IsinCodeCenter.getOptionCode(strike, year, month, CallPut.PUT);
            optionCodeSet.add(callCode);
            optionCodeSet.add(putCode);
            if (strike % 5 != 0) {
                strike += 3;
            } else {
                strike += 2;
            }
        }

        return optionCodeSet;
    }

    // 최근월물, 2째주 목요일 기준
    public static boolean isMaturity(Calendar oCalendar) {
        if (oCalendar.get(Calendar.DAY_OF_WEEK) != Calendar.THURSDAY)
            return false;

        int day = oCalendar.get(Calendar.DAY_OF_MONTH);

        return (day - 7) >= 1 && (day - 7) <= 7;
    }

    @Deprecated
    public static int getOptionExpireMonth() {
        // 2번째 목요일이 지났으면 최근월물은 현재 달, 그렇지 않으면 다음달
        GregorianCalendar oCalendar = new GregorianCalendar();

        if (!isMaturity(oCalendar)) {
            while (true) {
                oCalendar.add(Calendar.DATE, 1);

                // 주말은 건너뛴다
                if (oCalendar.get(Calendar.DAY_OF_WEEK) != Calendar.THURSDAY)
                    continue;

                while (true) {
                    if (isMaturity(oCalendar))
                        break;

                    oCalendar.add(Calendar.DATE, 7);
                }

                break;
            }
        }

        int optionExpireMonth = oCalendar.get(Calendar.MONTH) + 1; // Jan-0, Feb-1, ...

        return optionExpireMonth;
    }

    // 미니 콜 -> 원지수 풋, 미니 풋 -> 원지수 콜
    public static String getMiniToBigPairOptionCode(String code) {
        if (miniToBigOpCodeMap.containsKey(code))
            return miniToBigOpCodeMap.get(code);

        OptionIdentifier miniId = OptionIdentifierCenter.Instance.getOptionId(code);
        if (miniId == null || !miniId.isMini)
            return null;

        OptionIdentifier bigId = miniId.representOptId;
        if (bigId == null)
            return null;

        OptionIdentifier bigPairId = bigId.parityOptId;
        if (bigPairId == null)
            return null;

        String bigPairOptionCode = bigPairId.getIsinCode();
        miniToBigOpCodeMap.put(code, bigPairOptionCode);

        return bigPairOptionCode;
    }

    // 미니 콜 -> 원지수 콜, 미니 풋 -> 원지수 풋
    public static String getMiniToBigOptionCode(String code) {
        if (miniToBigCodeMap.containsKey(code))
            return miniToBigCodeMap.get(code);

        OptionIdentifier miniId = OptionIdentifierCenter.Instance.getOptionId(code);
        if (miniId == null || !miniId.isMini)
            return null;

        OptionIdentifier bigId = miniId.representOptId;
        if (bigId == null)
            return null;

        String bigOptionCode = bigId.getIsinCode();
        miniToBigCodeMap.put(code, bigOptionCode);

        return bigOptionCode;
    }

    // 콜 -> 풋, 풋 -> 콜
    public static String getPairOptionCode(String code) {
        if (opCodeMap.containsKey(code))
            return opCodeMap.get(code);

        OptionIdentifier id = OptionIdentifierCenter.Instance.getOptionId(code);
        if (id == null)
            return null;

        OptionIdentifier pairId = id.parityOptId;
        if (pairId == null)
            return null;

        String pairOptionCode = pairId.getIsinCode();
        opCodeMap.put(code, pairOptionCode);

        return pairOptionCode;
    }

    /**
     * @param strike  either 25500 or 255 work
     * @param year
     * @param month
     * @param callPut
     * @return 9digit option isinCode
     */
    public static String getOptionCode(int strike, char year, char month, CallPut callPut) {
        OptionCodeInfo info = new OptionCodeInfo(strike, year, month, callPut);
        String optionCode = optionCodeMap.get(info);
        if (optionCode != null)
            return optionCode;

        synchronized (optionCodeMap) {
            StringBuilder optionCodeName = new StringBuilder();
            optionCodeName.append(getNationCode());
            optionCodeName.append(propertyCode.get("Option").toString());
            optionCodeName.append(callPut.getCode());
            optionCodeName.append("01");
            optionCodeName.append(year);
            optionCodeName.append(month);

            String priceString = strike + "";
            int length = priceString.length();
            while (length < 3) {
                priceString = "0" + priceString;
                length = priceString.length();
            }

            optionCodeName.append(priceString, 0, 3);
            optionCodeName.append(getParityDigit(optionCodeName.toString()));
            optionCode = optionCodeName.toString();

            optionCodeMap.put(info, optionCode);
            return optionCode;
        }
    }

    public static final HashMap<OptionCodeInfo, String> optionCodeMap = new HashMap<>();
    public static HashMap<Integer, String> recentCallMap = new HashMap<>();
    public static HashMap<Integer, String> recentPutMap = new HashMap<>();
    public static HashMap<Integer, String> recentMiniCallMap = new HashMap<>();
    public static HashMap<Integer, String> recentMiniPutMap = new HashMap<>();

    public static String getRecentCallCode(int strike) {
        String callCode = recentCallMap.get(strike);
        if (callCode != null)
            return callCode;

        callCode = getOptionCode(CODETYPE_CALL, strike, recentOptionExpiryYearCode, recentOptionExpiryMonthCode);
        recentCallMap.put(strike, callCode);
        return callCode;
    }

    public static String getRecentPutCode(int strike) {
        String putCode = recentPutMap.get(strike);
        if (putCode != null)
            return putCode;

        putCode = getOptionCode(CODETYPE_PUT, strike, recentOptionExpiryYearCode, recentOptionExpiryMonthCode);
        recentPutMap.put(strike, putCode);
        return putCode;
    }

    public static String getRecentMiniCallCode(int strike) {
        String callCode = recentMiniCallMap.get(strike);
        if (callCode != null)
            return callCode;

        callCode = getMiniOptionCode(CODETYPE_CALL, strike, recentOptionExpiryYearCode, recentOptionExpiryMonthCode);
        recentMiniCallMap.put(strike, callCode);
        return callCode;
    }

    public static String getRecentMiniPutCode(int strike) {
        String putCode = recentMiniPutMap.get(strike);
        if (putCode != null)
            return putCode;

        putCode = getMiniOptionCode(CODETYPE_PUT, strike, recentOptionExpiryYearCode, recentOptionExpiryMonthCode);
        recentMiniPutMap.put(strike, putCode);
        return putCode;
    }

    public static String getParityDigit(String code) {
        String num = "";
        String double_num = "";

        for (int i = 0; i < code.length(); i++) {
            if (code.charAt(i) >= 'A')
                num += (code.charAt(i) - 55);
            else
                num += code.charAt(i);
        }

        int count = 0;
        for (int i = num.length() - 1; i >= 0; i--) {
            if (count++ % 2 == 0) {
                double_num = (Integer.parseInt(((Character) num.charAt(i)).toString()) * 2) + double_num;
            } else
                double_num = num.charAt(i) + double_num;
        }

        int sum = 0;
        for (int i = 0; i < double_num.length(); i++) {
            sum += Integer.parseInt(((Character) double_num.charAt(i)).toString());
        }

        return ((Integer) ((10 - (sum % 10)) % 10)).toString();
    }

    public static int getMonth(String code) {
        if (code.charAt(7) >= 'A')
            return code.charAt(7) - 55;
        else
            return code.charAt(7) - 48;
    }

    public static int getYear(String code) {
        char yearCode = code.charAt(6);

        if (yearCode < 46)
            return yearCode - 65 + 2006;
        else
            return yearCode - 66 + 2006;
    }

    public static double getPrice(String code) {

        if (code.length() != 12) {

            System.err.println("IsinCodeCenter.getPrice(Invalid isinCode): " + code);
            return -1;
        }

        String priceString = code.substring(8, 11);
        if (priceString.charAt(2) == '2' || priceString.charAt(2) == '7') {

            priceString += ".5";
        }

        return Double.parseDouble(priceString);
    }

    // 4th Position. 2009 - D, 2010 - E, ...
    public static String getCurrentYearCode(int expireMonth) {
        Calendar now = new GregorianCalendar();
        Character r = null;

        int year = now.get(Calendar.YEAR);
        int asciiAdder;
        if (year < 2019) {
            asciiAdder = 66;
        } else {
            asciiAdder = 67;
        }
        if (now.get(Calendar.MONTH) > expireMonth) {
            r = (char) (now.get(Calendar.YEAR) - 2006 + 1 + asciiAdder);
        } else {
            r = (char) (now.get(Calendar.YEAR) - 2006 + asciiAdder);
        }
        return r.toString();
    }


    // 4th Position. 2009 - D, 2010 - E, ...
    public static char getNextYearChar(int currYear) {
        Character r;

        int asciiAdder;
        if (currYear < 2018) {
            asciiAdder = 66;
        } else {
            asciiAdder = 67;
        }

        r = (char) (currYear - 2006 + asciiAdder + 1);

        return r;
    }


    // 4th Position. 2009 - D, 2010 - E, ...
    public static String getYearCode(int year) {
        int asciiAdder;
        if (year < 2019) {
            asciiAdder = 66;
        } else {
            asciiAdder = 67;
        }
        Character r = (char) (year - 2006 + asciiAdder);


        return r.toString();
    }

    // 4th Position. 2009 - D, 2010 - E, ...
    public static char getYearChar(int year) {
        int asciiAdder;
        if (year < 2019) {
            asciiAdder = 66;
        } else {
            asciiAdder = 67;
        }
        return (char) (year - 2006 + asciiAdder);

    }

    // 10 - A, 11 - B, 12 - C
    public static String getMonthCode(Integer expireMonth) {
        if (expireMonth >= 10) {
            Character r = (char) (expireMonth + 55);
            return r.toString();
        }

        return expireMonth.toString();
    }

    public static char getMonthChar(Integer expireMonth) {
        Character r;
        if (expireMonth >= 10) {
            r = (char) (expireMonth + 55);
        } else {
            r = (char) (expireMonth + 48);
        }
        return r;
    }

    @Deprecated
    public static String getMiniFuturesCode(char yearCode, char monthCode) {
        StringBuilder futuresCode = new StringBuilder();

        futuresCode.append("KR");
        futuresCode.append("4105");
        futuresCode.append(yearCode);
        futuresCode.append(monthCode);
        futuresCode.append("000");

        futuresCode.append(getParityDigit(futuresCode.toString()));

        return futuresCode.toString();
    }

    @Deprecated
    public static String getMiniOptionCode(String type, int price, char yearCode, char monthCode) {
        StringBuilder optionCodeName = new StringBuilder();

        optionCodeName.append("KR");
        optionCodeName.append(propertyCode.get("Option").toString());
        optionCodeName.append(futureOptionCode.get(type).toString());
        optionCodeName.append("05");
        optionCodeName.append(yearCode);
        optionCodeName.append(monthCode);
        String priceString = price + "";
        int length = priceString.length();
        while (length < 3) {
            priceString = "0" + priceString;
            length = priceString.length();
        }

        optionCodeName.append(priceString, 0, 3);
        optionCodeName.append(getParityDigit(optionCodeName.toString()));

        return optionCodeName.toString();
    }

    @Deprecated
    public static String getOptionCode(String type, int price, char yearCode, char monthCode) {
        StringBuilder optionCodeName = new StringBuilder();

        optionCodeName.append(getNationCode());
        optionCodeName.append(propertyCode.get("Option").toString());
        optionCodeName.append(futureOptionCode.get(type).toString());
        optionCodeName.append("01");
        optionCodeName.append(yearCode);
        optionCodeName.append(monthCode);

        String priceString = price + "";
        int length = priceString.length();
        while (length < 3) {
            priceString = "0" + priceString;
            length = priceString.length();
        }

        optionCodeName.append(priceString, 0, 3);
        optionCodeName.append(getParityDigit(optionCodeName.toString()));
        return optionCodeName.toString();
    }

    @Deprecated
    public static String getFutureName(Integer expireMonth) {
        StringBuilder futureCode = new StringBuilder();

        futureCode.append(getNationCode());
        futureCode.append(propertyCode.get(CODETYPE_FUTURE).toString());
        futureCode.append(futureOptionCode.get(CODETYPE_FUTURE).toString());
        futureCode.append("01");
        futureCode.append(getCurrentYearCode(expireMonth));
        futureCode.append(getMonthCode(expireMonth));

        futureCode.append("000");
        futureCode.append(getParityDigit(futureCode.toString()));
        return futureCode.toString();
    }

    @Deprecated
    public static String getFuturesCode(int month, FuturesCodeIdentifier futCodeId) {
        StringBuilder futureCode = new StringBuilder();

        futureCode.append(getNationCode());
        futureCode.append(propertyCode.get(CODETYPE_FUTURE).toString());
        futureCode.append(futureOptionCode.get(CODETYPE_FUTURE).toString());
        futureCode.append(futCodeId.getCodeID());
        futureCode.append(getCurrentYearCode(month));
        futureCode.append(getMonthCode(month));

        futureCode.append("000");
        futureCode.append(getParityDigit(futureCode.toString()));
        return futureCode.toString();
    }

    @Deprecated
    public static String getFuturesCode(int year, int month, FuturesCodeIdentifier futCodeId) {
        StringBuilder futureCode = new StringBuilder();

        futureCode.append(getNationCode());
        futureCode.append(propertyCode.get(CODETYPE_FUTURE).toString());
        futureCode.append(futureOptionCode.get(CODETYPE_FUTURE).toString());
        futureCode.append(futCodeId.getCodeID());
        futureCode.append(getYearCode(year));
        futureCode.append(getMonthCode(month));

        futureCode.append("000");
        futureCode.append(getParityDigit(futureCode.toString()));
        return futureCode.toString();
    }

    private static String getNationCode() {
        return "KR";
    }

    public static boolean isK200Option(String code) {
        return code.indexOf("4201") == 2 || code.indexOf("4301") == 2;
    }

    public static boolean isOption(String code) {
        return code.indexOf("4201") == 2 || code.indexOf("4301") == 2
                || code.indexOf("4205") == 2 || code.indexOf("4305") == 2;
    }

    public static boolean isMiniOption(String code) {
        return code.indexOf("4205") == 2 || code.indexOf("4305") == 2;
    }

    public static boolean isFuture(String code) {
        return code.indexOf("4101") == 2 || code.indexOf("4105") == 2;
    }

    /**
     * @param optionCode - should be 12 digit option isinCode. this method does not check any isinCode test;
     * @return
     */
    @Deprecated
    public static CallPut getCallPut(String optionCode) {
        if (optionCode.charAt(3) == '2')
            return CallPut.CALL;
        else
            return CallPut.PUT;
    }

    public static boolean isSectorFutures(String code) {
        return code.indexOf("41A") == 2;
    }

    @Deprecated
    public static boolean isKQFutures(String code) {
        return code.indexOf("4106") == 2;
    }

    public static boolean isCash(String code) {
        return code.equals(PredefinedIsinCode.CODE_CASH) || code.equals(PredefinedIsinCode.CODE_CASH2);
    }

    // 합성 선물 구성시 옵션 정보로 선물의 롱숏 판별
    // Call : 옵션 매수 - 선물 매도, 옵션 매도 - 선물 매수 -> Call 옵션의 반대 방향
    // Put : 옵션 매수 - 선물 매수, 옵션 매도 - 선물 매도 -> Put 옵션과 같은 방향
    public static LongShort getSyntheticFuturesLongShort(String indexOptionCode, LongShort longShort) throws IllegalTrCodeException {
        ProdType pType = ProdTypeCenter.Instance.getProdType(indexOptionCode);

        if (pType.isK200MiniCall() || pType.isK200Call()) {
            return longShort.opposite();
        } else if (pType.isK200MiniPut() || pType.isK200Put()) {
            return longShort;
        } else {
            throw new IllegalTrCodeException("종목코드가 지수옵션이 아님:" + indexOptionCode);
        }
    }

    // mini - 원지수 , 원지수 - mini 맵
    public static ConcurrentHashMap<String, String> hedgeCodeMap = new ConcurrentHashMap<>();

    /**
     * @param optionCode
     * @return 25250 not 252.50
     */
    public static Integer getStrikeFromCode(String optionCode) {
        Integer priceInt = strikePriceMap.get(optionCode);

        if (priceInt != null)
            return priceInt;

        String priceStr = optionCode.substring(8, 11);
        priceInt = Integer.parseInt(priceStr) * 100;


        Integer strikePrice;
        if (priceInt % 500 != 0)
            strikePrice = (priceInt + 50);
        else
            strikePrice = priceInt;

        strikePriceMap.put(optionCode, strikePrice);

        return strikePrice;
    }

    public static String getRecentFuturesOf(String indexCode) {
        if (indexCode.equals(PredefinedIsinCode.KOSPI_200))
            return recentK200FuturesCode;
        if (indexCode.equals(PredefinedIsinCode.KOSDAQ_150))
            return recentKQ150FuturesCode;

        List<DerivativesUnderlyingType> dutList = DerivativesUnderlyingType.getTypeListFromIsinCode(indexCode);
        if (dutList.size() == 0)
            return null;

        DerivativesUnderlyingType dut = dutList.get(0);
        FuturesInfo info = FuturesInfoCenter.Instance.getFutures(dut, 0);
        if (info == null)
            return null;

        return info.isinCode;
    }

    public static String getNextFuturesOf(String indexCode) {
        if (indexCode.equals(PredefinedIsinCode.KOSPI_200))
            return nextK200FuturesCode;
        if (indexCode.equals(PredefinedIsinCode.KOSDAQ_150))
            return nextKQ150FuturesCode;

        List<DerivativesUnderlyingType> dutList = DerivativesUnderlyingType.getTypeListFromIsinCode(indexCode);
        if (dutList.size() == 0)
            return null;

        DerivativesUnderlyingType dut = dutList.get(0);
        FuturesInfo info = FuturesInfoCenter.Instance.getFutures(dut, 1);
        if (info == null)
            return null;

        return info.isinCode;
    }
}