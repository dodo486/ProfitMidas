package bulls.feed.dc.enums;

import java.util.HashMap;

public enum 세션ID {
    시가단일가(10),
    시가VI단일가(52),
    시간단일가연장(11),
    시가VI단일가연장(53),
    단일가(20),
    단일가연장(21),
    종가단일가(30),
    종가VI단일가(54),
    접속(40),
    장중VI단일가(50),
    장중VI단일가연장(51),
    단위매매(80),
    장종료후종가접수(60);

    public Integer getValue() {
        return this.value;
    }

    public static 세션ID getEnum(Integer value) {
        return 세션IDmap.get(value);
    }

    Integer value;

    세션ID(Integer value) {
        this.value = value;
    }

    static HashMap<Integer, 세션ID> 세션IDmap;

    static {
        for (세션ID id : 세션ID.values()) {
            세션IDmap.put(id.getValue(), id);
        }

    }
}
