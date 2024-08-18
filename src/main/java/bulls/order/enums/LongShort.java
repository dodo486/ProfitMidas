package bulls.order.enums;

import bulls.staticData.UpDown;

public enum LongShort {
    LONG(2, (byte) '2', "매수"),
    SHORT(1, (byte) '1', "매도"),
    UNKNOWN(0, (byte) '0', "알수없음");

    private final int longShortCode;
    private final byte longShortByteCode;

    private final String korean;

    LongShort(int longShortCode, byte longShortByteCode, String korean) {
        this.longShortCode = longShortCode;
        this.longShortByteCode = longShortByteCode;
        this.korean = korean;
    }

    public int getValue() {
        return longShortCode;
    }

    public byte getByteValue() {
        return longShortByteCode;
    }

    public String getKorean() {
        return korean;
    }

    public static LongShort getFromValue(int value) {

        if (value == SHORT.longShortCode)
            return SHORT;
        return LONG;
    }

    public static LongShort getFromByteValue(byte byteValue) {

        if (byteValue == SHORT.longShortByteCode)
            return SHORT;
        return LONG;
    }

    public LongShort opposite() {
        if (this == LONG)
            return LongShort.SHORT;
        else if (this == SHORT)
            return LongShort.LONG;
        return UNKNOWN;
    }

    public UpDown getUpDownForTickNormalize() {
        if (this == LONG)
            return UpDown.DOWN;
        else
            return UpDown.UP;
    }


    public LongShort applyMultiplier(int multiplier) {
        if (multiplier < 0) {
            return this.opposite();
        }
        return this;
    }


//    @JsonCreator
//    public static LongShort forValue(int value) {
//        return  LongShort.forValue(value);
//    }
//
//    @JsonValue
//    public int toValue() {
//        return longShortCode;
//    }
}
