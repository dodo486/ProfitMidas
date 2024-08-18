package bulls.order.enums;

import java.util.Arrays;
import java.util.Dictionary;
import java.util.Hashtable;
public enum ShortSellCode {
    해당없음("00"),
    일반매도("01"),
    차입공매도("02"),
    기타매도("06"),
    미니원장결정("  "),
    NA("");

    String stringValue;
    byte[] byteValue;

    ShortSellCode(String str) {
        this.stringValue = str;
        this.byteValue = str.getBytes();
    }

    public String getStringValue() {
        return stringValue;
    }

    public byte[] getByteValue() {
        return byteValue;
    }

    static Dictionary<String, ShortSellCode> dic;

    static {
        dic = new Hashtable<>();
        for (ShortSellCode value : ShortSellCode.values()) {
            dic.put(value.getStringValue(), value);
        }
    }

    public static ShortSellCode getEnumInstance(String str) {
        var t = dic.get(str);
        if (t == null)
            return NA;
        return t;
    }

    public static ShortSellCode getEnumInstance(byte[] arr) {
        for (ShortSellCode value : ShortSellCode.values()) {
            if (Arrays.equals(arr, value.getByteValue()))
                return value;
        }
        return NA;
    }
}
