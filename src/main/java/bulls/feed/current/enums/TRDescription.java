package bulls.feed.current.enums;

import bulls.server.enums.ServerLocation;
import bulls.tool.conf.KrxConfiguration;

import java.util.*;

public enum TRDescription {
    종목배치(ServerLocation.SEOUL, "종목배치"), 공시(ServerLocation.SEOUL, "공시"),
    // 기본 시세
    지수(ServerLocation.SEOUL, "시세"), 코스피(ServerLocation.SEOUL, "시세"), 코스닥(ServerLocation.SEOUL, "시세"), ETF(ServerLocation.SEOUL, "시세"), 해외지수ETF(ServerLocation.SEOUL, "시세"), ELW(ServerLocation.SEOUL, "시세"),
    K200선물(ServerLocation.SEOUL, "시세"), K200옵션(ServerLocation.SEOUL, "시세"),
    미니K200선물(ServerLocation.SEOUL, "시세"), 미니K200옵션(ServerLocation.SEOUL, "시세"),
    K200위클리옵션(ServerLocation.SEOUL, "시세"),
    코스닥150선물(ServerLocation.SEOUL, "시세"), 코스닥150옵션(ServerLocation.SEOUL, "시세"),
    KRX300선물(ServerLocation.SEOUL, "시세"),
    주식선물(ServerLocation.SEOUL, "시세"), 주식옵션(ServerLocation.SEOUL, "시세"),
    섹터지수선물(ServerLocation.SEOUL, "시세"), 변동성지수선물(ServerLocation.SEOUL, "시세"), 상품선물(ServerLocation.SEOUL, "시세"), 유로스톡스50선물(ServerLocation.SEOUL, "시세"),
    // 민감도
    ELW_민감도(ServerLocation.SEOUL, "민감도"), K200옵션_민감도(ServerLocation.SEOUL, "민감도"), 미니K200옵션_민감도(ServerLocation.SEOUL, "민감도"), 코스닥150옵션_민감도(ServerLocation.SEOUL, "민감도"), 주식옵션_민감도(ServerLocation.SEOUL, "민감도"), K200위클리옵션_민감도(ServerLocation.SEOUL, "민감도"),
    //종가
    코스피_종가(ServerLocation.SEOUL, "종가"), ELW_종가(ServerLocation.SEOUL, "종가"), 코스닥_종가(ServerLocation.SEOUL, "종가"),
    K200선물_종가(ServerLocation.SEOUL, "종가"), K200옵션_종가(ServerLocation.SEOUL, "종가"),
    미니K200선물_종가(ServerLocation.SEOUL, "종가"), 미니K200옵션_종가(ServerLocation.SEOUL, "종가"),
    K200위클리옵션_종가(ServerLocation.SEOUL, "종가"),
    코스닥150선물_종가(ServerLocation.SEOUL, "종가"), 코스닥150옵션_종가(ServerLocation.SEOUL, "종가"),
    KRX300선물_종가(ServerLocation.SEOUL, "종가"),
    주식선물_종가(ServerLocation.SEOUL, "종가"), 주식옵션_종가(ServerLocation.SEOUL, "종가"),
    섹터지수선물_종가(ServerLocation.SEOUL, "종가"), 변동성지수선물_종가(ServerLocation.SEOUL, "종가"), 상품선물_종가(ServerLocation.SEOUL, "종가"), 유로스톡스50선물_종가(ServerLocation.SEOUL, "종가"),
    //통계
    코스피_통계(ServerLocation.SEOUL, "통계"), 코스닥_통계(ServerLocation.SEOUL, "통계"), ELW_통계(ServerLocation.SEOUL, "통계"), K200선물_통계(ServerLocation.SEOUL, "통계"), 주식선물_통계(ServerLocation.SEOUL, "통계"), 섹터지수선물_통계(ServerLocation.SEOUL, "통계"), 코스닥150선물_통계(ServerLocation.SEOUL, "통계"), 미니K200선물_통계(ServerLocation.SEOUL, "통계"), KRX300선물_통계(ServerLocation.SEOUL, "통계"), K200옵션_통계(ServerLocation.SEOUL, "통계"), K200위클리옵션_통계(ServerLocation.SEOUL, "통계"), 주식옵션_통계(ServerLocation.SEOUL, "통계"), 미니K200옵션_통계(ServerLocation.SEOUL, "통계"), 코스닥150옵션_통계(ServerLocation.SEOUL, "통계"),
    // 부산
    부산_K200선물(ServerLocation.PUSAN, "시세"), 부산_미니K200선물(ServerLocation.PUSAN, "시세"), 부산_K200옵션(ServerLocation.PUSAN, "시세"), 부산_K200위클리옵션(ServerLocation.PUSAN, "시세"), 부산_미니K200옵션(ServerLocation.PUSAN, "시세"),
    Unidentified(ServerLocation.NA, "NA");

    public static HashMap<String, TRDescription> map;

    static {
        map = new HashMap<>();
        for (TRDescription value : values()) {
            map.put(value.getTrDescriptionName(), value);
        }
    }

    public static TRDescription getValue(String trDesc) {
        return map.get(trDesc);
    }


    final ServerLocation location;
    final String feedType;

    TRDescription(ServerLocation location, String feedType) {
        this.location = location;
        this.feedType = feedType;
    }

    public ServerLocation getLocation() {
        return location;
    }

    public String getFeedType() {
        return feedType;
    }

    public String getTrDescriptionName() {
        return super.toString();
    }

    public static Set<TRDescription> getTrDescriptionList(ServerLocation location, String feedType) {
        HashSet<TRDescription> set = new HashSet<>();
        for (TRDescription value : values()) {
            if (value.location == location && (value.feedType.equals(feedType) || feedType.equals("전체")))
                set.add(value);
        }
        return set;
    }

    public static List<TRDescription> getTRDescriptionList(KrxConfiguration conf) {
        List<TRDescription> trDescriptionList = new ArrayList<>();

        String[] trStrList = conf.getStringList("NonBlockingTRList", ",");

        if (trStrList == null)
            return trDescriptionList;
        for (String trStr : trStrList) {
            TRDescription trDescription = TRDescription.valueOf(trStr);
            trDescriptionList.add(trDescription);
        }

        return trDescriptionList;
    }

    @Override
    public String toString() {
        return super.toString() + "/" + location + "/" + feedType;
    }
}

