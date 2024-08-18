package bulls.staticData;

import java.util.HashMap;

public enum IndexCode {
    KRX300("KRD020022087", "AA011300", "AB011300"),
    KOSPI200("KRD020020016", "D2011029", "D3011029", "KR49999999KP"),
    KOSDAQ150("KRD020021378", "T9012203", "U4012203"),
    KOSPI200_커뮤니케이션서비스("KRDCOMMSVCXX", "N5011150", "N6011150"),
    KOSPI200_건설("KRD020020339", "N5011151", "N6011151"),
    KOSPI200_중공업("KRD020020347", "N5011152", "N6011152"),
    KOSPI200_철강소재("KRD020020354", "N5011153", "N6011153"),
    KOSPI200_에너지화학("KRD020020362", "N5011154", "N6011154"),
    KOSPI200_정보기술("KRD020020370", "N5011155", "N6011155"),
    KOSPI200_금융("KRD020020388", "N5011156", "N6011156"),
    KOSPI200_생활소비재("KRD020020396", "N5011157", "N6011157"),
    KOSPI200_경기소비재("KRD020020404", "N5011158", "N6011158"),
    KOSPI200_산업재("KRD020021386", "N5011159", "N6011159"),
    // FuturesInfo에 입수되는 값은 KRD020021397임. ClosingPriceCenter의 가격이 4 로 업데이트 되는 상황
    KOSPI200_헬스케어("KRD020021394", "N5011160", "N6011160", "KRD020021397"),
    KOSPI200_배당성장50("KRD020021311", "S6011164", "V2011164"),
    KOSPI200_고배당50("KRD020021329", "S4011163", "V0011163"),
    K뉴딜_BBIG("KRD020023085", "AG011411", "AH011411"),
    K뉴딜_2차전지("KRD020023127", "AG011412", "AH011412"),
    K뉴딜_바이오("KRD020023119", "AG011413", "AH011413"),
    K뉴딜_인터넷("KRD020023101", "AG011414", "AH011414"),
    K뉴딜_게임("KRD020023093", "AG011415", "AH011415"),
    KOSPI_변동성("KRD020021139", "J3034   ");
    static final HashMap<String, IndexCode> indexIsinCodeToIndexCodeMap = new HashMap<>();
    static final HashMap<String, IndexCode> normalPriceFeedTRWithIndustryCodeToIndexCodeMap = new HashMap<>();
    static final HashMap<String, IndexCode> expectedPriceFeedTRWithIndustryCodeToIndexCodeMap = new HashMap<>();

    static {
        for (IndexCode indexCode : IndexCode.values()) {
            String indexIsinCode = indexCode.getIndexIsinCode();
            String normalPriceFeedTRWithIndustryCode = indexCode.getNormalPriceFeedTRWithIndustryCode();
            String expectedPriceFeedTRWithIndustryCode = indexCode.getExpectedPriceFeedTRWithIndustryCode();
            String alternativeIndexIsinCode = indexCode.getAlternativeIndexIsinCode();
            if (indexIsinCode != null)
                indexIsinCodeToIndexCodeMap.put(indexIsinCode, indexCode);
            if (expectedPriceFeedTRWithIndustryCode != null)
                expectedPriceFeedTRWithIndustryCodeToIndexCodeMap.put(expectedPriceFeedTRWithIndustryCode, indexCode);
            if (normalPriceFeedTRWithIndustryCode != null)
                normalPriceFeedTRWithIndustryCodeToIndexCodeMap.put(normalPriceFeedTRWithIndustryCode, indexCode);
            if (alternativeIndexIsinCode != null)
                indexIsinCodeToIndexCodeMap.put(alternativeIndexIsinCode, indexCode);
        }
    }

    String indexIsinCode = null, normalPriceFeedTRWithIndustryCode = null, expectedPriceFeedTRWithIndustryCode = null, alternativeIndexIsinCode = null;


    IndexCode(String indexIsinCode, String normalPriceFeedTRWithIndustryCode, String expectedPriceFeedTRWithIndustryCode, String alternativeIndexIsinCode) {
        this(indexIsinCode, normalPriceFeedTRWithIndustryCode, expectedPriceFeedTRWithIndustryCode);
        this.alternativeIndexIsinCode = alternativeIndexIsinCode;
    }

    IndexCode(String indexIsinCode, String normalPriceFeedTRWithIndustryCode, String expectedPriceFeedTRWithIndustryCode) {
        this(indexIsinCode, normalPriceFeedTRWithIndustryCode);
        this.expectedPriceFeedTRWithIndustryCode = expectedPriceFeedTRWithIndustryCode;
    }

    IndexCode(String indexIsinCode, String normalPriceFeedTRWithIndustryCode) {
        this.indexIsinCode = indexIsinCode;
        this.normalPriceFeedTRWithIndustryCode = normalPriceFeedTRWithIndustryCode;
    }

    public String getIndexIsinCode() {
        return indexIsinCode;
    }

    public String getNormalPriceFeedTRWithIndustryCode() {
        return normalPriceFeedTRWithIndustryCode;
    }

    public String getExpectedPriceFeedTRWithIndustryCode() {
        return expectedPriceFeedTRWithIndustryCode;
    }

    public String getAlternativeIndexIsinCode() {
        return alternativeIndexIsinCode;
    }

    public static IndexCode parseIndexIsinCode(String indexIsinCode) {
        return indexIsinCodeToIndexCodeMap.get(indexIsinCode);
    }

    public static IndexCode parseNormalPriceFeedTRWithIndustryCode(String normalPriceFeedTRWithIndustryCode) {
        return normalPriceFeedTRWithIndustryCodeToIndexCodeMap.get(normalPriceFeedTRWithIndustryCode);
    }

    public static IndexCode parseExpectedPriceFeedTRWithIndustryCode(String expectedPriceFeedTRWithIndustryCode) {
        return expectedPriceFeedTRWithIndustryCodeToIndexCodeMap.get(expectedPriceFeedTRWithIndustryCode);
    }
}
