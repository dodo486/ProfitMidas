package bulls.staticData.ProdType;

public enum EquitySecurityGroupType {
    //    ST:주권 MF:증권투자회사 RT:부동산투자회사
    //    SC:선박투자회사
    //    IF:사회간접자본투융자회사 DR:주식예탁증서 EW:ELW EF:ETF
    //    SW:신주인수권증권 SR:신주인수권증서 BC:수익증권
    //    FE:해외ETF FS:외국주권
    //    EN:ETN 2014.11.17
    BC, DR, EF, EN, EW, FS, IF, MF, RT, SC, SR, ST, SW, FE, UNKNOWN, NA;

    public static EquitySecurityGroupType getTypeFromCode(String id) {
        for (EquitySecurityGroupType t : values()) {
            if (t.toString().equals(id)) {
                return t;
            }
        }
        return EquitySecurityGroupType.UNKNOWN;
    }
}