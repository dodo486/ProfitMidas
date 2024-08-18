package bulls.feed.dc.enums;

import java.util.HashMap;

public enum 보드이벤트ID {
    시가단일가개시("AA1"),
    매매거래개시("BB1"),
    단위매매개시("CD1"),
    단위매매체결30분("CD3"),
    종가단일가개시("BC1"),
    단위매매종가개시30분("CC1"),
    종가단일가마감("AC2"),
    시장매매체결최종마감("AQ3"),
    호가접수개시("AD1"),
    매매거래마감("AB2"),
    단일가마감("AD2"),
    예상체결가산출개시("AK1"),
    자기주식호가종료("AM2"),
    시장임시정지("AE8"),
    시장임시정지후재개("AE1"),
    시장임시정지후접매매재개("BE9"),
    종목거래정지("EI8"),
    종목거래정지후재개("EI1"),
    종목거래정지후종가단일가개시("EC1"),
    종목거래정지후매매재개("EI9"),
    시장호가접수정지("AH8"),
    시장호가접수재개("AH9"),
    종목호가접수정지("EH8"),
    종목호가접수재개("EH9");

    public String getValue() {
        return this.value;
    }

    public static 보드이벤트ID getEnum(String value) {
        return 보드이벤트IDmap.get(value);
    }

    String value;

    보드이벤트ID(String value) {
        this.value = value;
    }

    static HashMap<String, 보드이벤트ID> 보드이벤트IDmap;

    static {
        for (보드이벤트ID id : 보드이벤트ID.values()) {
            보드이벤트IDmap.put(id.getValue(), id);
        }

    }
}
