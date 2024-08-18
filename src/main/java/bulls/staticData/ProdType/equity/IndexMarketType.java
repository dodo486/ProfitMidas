package bulls.staticData.ProdType.equity;


public enum IndexMarketType {
    국내코스피("010100"),
    국내코스닥("010200"),
    국내코스피앤닥("010300"),
    국내주식외("010400"),


    해외글로벌("020100"),
    해외선진국("020200"),
    해외신흥국("020300"),
    해외프런티어("020400"),
    해외아시아중국("020501"),
    해외아시아홍콩("020502"),
    해외아시아일본("020503"),
    해외아시아인도("020504"),
    해외아시아베트남("020505"),
    해외아시아호주("020506"),
    해외아시아대만("020507"),
    해외아시아인도네시아("020508"),
    해외아시아필리핀("020509"),

    해외북미("020600"),
    해외북미미국("020601"),

    해외남미("020700"),
    해외남미브라질("020701"),

    해외유럽("020800"),
    해외유럽영국("020801"),
    해외유럽독일("020802"),
    해외유럽러시아("020803"),

    국내앤해외("030000"),
    ETP아님("000000");


    private final String aCode;
    private final String bCode;
    private final String cCode;

    private final String fullCode;

    IndexMarketType(String code) {
        this.fullCode = code;
        this.aCode = code.substring(0, 2);
        this.bCode = code.substring(2, 4);
        this.cCode = code.substring(4, 6);
    }

    public static IndexMarketType fromCode(String code) {
        for (IndexMarketType imType : values()) {
            if (imType.fullCode.equals(code))
                return imType;
        }
        return null;
    }


}
