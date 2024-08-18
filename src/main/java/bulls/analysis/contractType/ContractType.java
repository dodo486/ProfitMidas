package bulls.analysis.contractType;

public enum ContractType {
    MAKE_BAD("badMake"),   // 도망가려고 했지만 실패
    MAKE_GOOD("goodMake"),  // 내가 낸 가격에 체결
    TAKE("take"),       // 시장 호가에 때림
    MARKET("market"),
    UNKNOWN("unknown");

    private final String str;

    ContractType(String str) {
        this.str = str;
    }

    @Override
    public String toString() {
        return str;
    }
}
