package bulls.feed.dc.enums;

import java.util.HashMap;

public enum 보드ID {
    정규장("G1"),
    장개시전시간외종가("G2"),
    장종료후시간외종가("G3"),
    장종료후시간외단일가("G4"),
    코넥스장개시전시간외종가("G5"),
    일반BuyIN("G7"),
    당일BuyIN("G8"),
    기타("etc");

    final String value;

    보드ID(String value) {
        this.value = value;
    }

    static final HashMap<String, 보드ID> 보드IDmap = new HashMap<>();

    static {
        for (보드ID id : 보드ID.values()) {
            보드IDmap.put(id.getValue(), id);
        }
    }

    public String getValue() {
        return this.value;
    }

    public static 보드ID getEnum(String value) {
        return 보드IDmap.get(value);
    }
}
