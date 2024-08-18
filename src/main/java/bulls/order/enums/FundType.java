package bulls.order.enums;

public enum FundType {
    // 0:일반잔고 1:대차잔고 2:권리잔고 2021.07.08 미니원장 소스 추가
    일반잔고((byte) '0'),
    대차잔고((byte) '1');
    final byte askInvType;

    FundType(byte askInvType) {
        this.askInvType = askInvType;
    }

    public byte getAskInvTypeByte() {
        return askInvType;
    }

    public static FundType getFundType(byte askInvType) {
        if (askInvType == 일반잔고.askInvType)
            return 일반잔고;
        else
            return 대차잔고;
    }
}
