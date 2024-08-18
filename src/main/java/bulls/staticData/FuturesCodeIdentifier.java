package bulls.staticData;

import bulls.exception.InvalidCodeException;

@Deprecated
public enum FuturesCodeIdentifier {
    K200("01"),
    K200Mini("05"),
    KQ150("06"),
    KRX300("08"),
    KNewDealBBIG("AC"),
    KNewDealBattery("AD"),
    KNewDealBio("AE"),
    VKOSPI("04");

    final String futuresId;

    FuturesCodeIdentifier(String futuresId) {
        this.futuresId = futuresId;
    }

    public String getCodeID() {
        return futuresId;
    }

    public static FuturesCodeIdentifier getFuturesCode(String futuresIsinCode) throws InvalidCodeException {
        if (futuresIsinCode.indexOf("KR41") == 0)
            throw new InvalidCodeException(futuresIsinCode + " 는 선물 코드가 아닙니다.");

        String codeId = futuresIsinCode.substring(4, 6);

        for (var id : FuturesCodeIdentifier.values()) {
            if (id.futuresId.equals(codeId))
                return id;
        }

        throw new InvalidCodeException(futuresIsinCode + " 에 해당하는 선물 구분을 발견하지 못했습니다.");
    }
}
