package bulls.feed.current.enums;

import java.util.HashMap;

public enum FeedMarketType {
    /**
     * +--------------+-----------+
     * | 시장구분     | 시장 코드 |
     * +--------------+-----------+
     * | 전체시장공통 | 0         |
     * +--------------+-----------+
     * | 유가증권     | 1         |
     * +--------------+-----------+
     * | 코스닥       | 2         |
     * +--------------+-----------+
     * | 프리보드     | 3         |
     * +--------------+-----------+
     * | 지수파생     | 4         |
     * +--------------+-----------+
     * | 지수파생     | 5         |
     * +--------------+-----------+
     * | 상품파생     | 6         |
     * +--------------+-----------+
     * | 채권         | 7         |
     * +--------------+-----------+
     * | 기타         | 9         |
     * +--------------+-----------+
     * | 기타         | 90        |
     * +--------------+-----------+
     */
    Common("0"),
    KOSPI("1"),
    KOSDAQ("2"),
    FreeBoard("3"),
    IndexDeriv("4"),
    StockDeriv("5"),
    CmdtyDeriv("6"),
    Bond("7"),
    ETC("9");
    final String code;
    static final HashMap<String, FeedMarketType> map;

    static {
        map = new HashMap<>();
        for (FeedMarketType feedMarketType : values()) {
            map.put(feedMarketType.getCode(), feedMarketType);
        }
    }

    public static FeedMarketType parse(String code) {
        return map.get(code);
    }

    public static FeedMarketType parseFromTrCode(String trCode) {
        return map.get(trCode.substring(4, 5));
    }

    FeedMarketType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
