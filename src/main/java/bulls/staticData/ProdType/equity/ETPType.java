package bulls.staticData.ProdType.equity;

public enum ETPType {
    ETF_투자회사형("1"),
    ETF_수익증권형("2"),
    ETN("3"),
    손실제한ETN("4"),
    변동성ETN("5");


    private final String code;

    ETPType(String code) {
        this.code = code;
    }

    public static ETPType fromCode(String code) {
        for (ETPType etpType : values()) {
            if (etpType.code.equals(code))
                return etpType;
        }
        return null;
    }
}
