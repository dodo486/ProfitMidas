package bulls.staticData.ProdType.equity;

public enum IndexMultiplierType {
    // 221212 0.5X, 3X 인버스, 레버리지 추가
    // 지금까지 ETF 배수가 정수 밖에 없었기 때문에 내부적으로 int 값으로 처리해왔던 상황
    // 일단 0.5배 레버리지/인버스를 제외하고는 정수 값으로 처리하기로 한다.
    // Todo: 0.5배 레버리지/인버스 상품을 매매하려면 multiplier 처리 후 매매해야 함
    PLAIN("P1", 1, 1),
    LEV_2X("P2", 2, 2),
    LEV_3X("P3", 3, 3),
    LEV_0_5X("PA", 0.5, 1),
    INV_1X("N1", -1, -1),
    INV_2X("N2", -2, -2),
    INV_3X("N3", -3, -3),
    INV_0_5X("NA", -0.5, -1);

    private final String code;
    private final double rawMultiplier;
    private final int multiplier;

    IndexMultiplierType(String code, double rawMultiplier, int multiplier) {
        this.code = code;
        this.rawMultiplier = rawMultiplier;
        this.multiplier = multiplier;
    }

    public int getMultiplier() {
        return multiplier;
    }

    public double getRawMultiplier() {
        return rawMultiplier;
    }

    public static IndexMultiplierType fromCode(String code) {
        for (IndexMultiplierType imType : values()) {
            if (imType.code.equals(code))
                return imType;
        }
        return PLAIN;
    }
}
