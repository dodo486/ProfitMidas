package bulls.feed.current.enums;

import java.util.HashMap;

public enum FeedDataType {
    Price("A3"),
    BidAsk("B6"),
    PriceBidAsk("G7");
    final String code;
    static final HashMap<String, FeedDataType> map;

    static {
        map = new HashMap<>();
        for (FeedDataType f : values()) {
            map.put(f.getCode(), f);
        }
    }

    public static FeedDataType parse(String code) {
        return map.get(code);
    }

    public static FeedDataType parseFromTrCode(String trCode) {
        return map.get(trCode.substring(0, 2));
    }

    public String getCode() {
        return code;
    }

    FeedDataType(String code) {
        this.code = code;
    }
}
