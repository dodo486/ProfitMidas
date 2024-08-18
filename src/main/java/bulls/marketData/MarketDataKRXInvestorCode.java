package bulls.marketData;

import java.util.HashMap;

public enum MarketDataKRXInvestorCode {
    /**
     * 투자자코드	명칭		비고
     * 1000	금융투자업자		구:증권회사 및 선물회사
     * 1500	삭제(1000번으로 통합)		구:선물회사
     * 2000	보험회사
     * 3000	자산운용회사 및 투자회사
     * 3100	사모펀드
     * 4000	은행		자산운용회사의 신탁재산은 자산운용회사로 분류
     * 5000	기타금융기관		구:종합금융회사, 상호저축은행
     * 6000	연금, 기금 및 공제회
     * 6100	삭제		구:기타금융기관
     * 7000	국가/ 지방자치단체,국제기구 및 공익기관		구:국가, 지방자치단체 및 국제기구
     * 7100	기타법인		기존 7000번 분류를 세분화하여 추가확정
     * 8000	개인
     * 9000	외국인투자등록ID가 있는 외국인		거주, 비거주
     * 9001	외국인투자등록ID가 없는 외국인		거주, 비거주
     */
    금융투자업자("1000"),
    구_선물회사("1500"),
    보험회사("2000"),
    자산운용회사_및_투자회사("3000"),
    사모펀드("3100"),
    은행("4000"),
    기타금융기관("5000"),
    연금_기금_및_공제회("6000"),
    삭제("6100"),
    국가_지방자치단체_국제기구_및_공익기관("7000"),
    기타법인("7100"),
    개인("8000"),
    외국인투자등록ID가_있는_외국인("9000"),
    외국인투자등록ID가_없는_외국인("9001"),
    UNKNOWN("0000");
    static final HashMap<String, MarketDataKRXInvestorCode> memberCodeToEnumMap;

    static {
        memberCodeToEnumMap = new HashMap<>();
        for (MarketDataKRXInvestorCode mem : MarketDataKRXInvestorCode.values()) {
            memberCodeToEnumMap.put(mem.getCode(), mem);
        }
    }

    private final String code;

    MarketDataKRXInvestorCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static MarketDataKRXInvestorCode parse(String code) {
        return memberCodeToEnumMap.getOrDefault(code, MarketDataKRXInvestorCode.UNKNOWN);
    }

    public static String getName(String code) {
        MarketDataKRXInvestorCode ret = memberCodeToEnumMap.getOrDefault(code, MarketDataKRXInvestorCode.UNKNOWN);
        if (ret == UNKNOWN)
            return code;
        else
            return ret.toString();
    }
}

