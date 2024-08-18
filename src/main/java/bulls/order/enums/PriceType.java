package bulls.order.enums;

public enum PriceType {
    /* 호가유형코드	"ORDER_TYPE_CODE"
    1 시장가 (Market)
    2 지정가 (Limit)
    T 가격제한시장가
    W 가격제한최유리지정가
    I 조건부지정가 (Limit To Market)
    V 경쟁대량 (현물만 해당, 값 변경 : 3->V)
    X 최유리지정가
    Y 최우선지정가 (현물만 해당)

    * 취소호가는 SPACE로 입력
    * 코넥스 경매매는 2:지정가만 가능
    * 파생시장 1,X 불가 (T,W 으로 대체)
    */
    시장가((byte) '1'),
    지정가((byte) '2'),
    가격제한시장가((byte) 'T'),
    UNKNOWN((byte) ' ');
    private final byte priceTypeCode;

    PriceType(byte priceTypeCode) {
        this.priceTypeCode = priceTypeCode;
    }

    public byte getValue() {
        return priceTypeCode;
    }

    public static PriceType getFromValue(byte b) {
        for (PriceType value : PriceType.values()) {
            if (value.getValue() == b)
                return value;
        }
        return UNKNOWN;
    }
}
