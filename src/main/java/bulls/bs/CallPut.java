package bulls.bs;

import bulls.exception.InvalidCodeException;
import bulls.staticData.ProdType.ProdType;
import bulls.staticData.ProdType.ProdTypeCenter;
public enum CallPut {
    CALL(2),
    PUT(3);

    private final int code;

    CallPut(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public CallPut opposite() {
        if (this == CALL)
            return PUT;
        else
            return CALL;
    }

    public static CallPut of(String isinCode) throws InvalidCodeException {
        ProdType pType = ProdTypeCenter.Instance.getProdType(isinCode);
        if (!pType.isOption())
            throw new InvalidCodeException(isinCode + " 는 옵션이 아닙니다.");

        if (pType.isCallOption())
            return CALL;

        return PUT;
    }
}
