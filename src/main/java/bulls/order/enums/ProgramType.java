package bulls.order.enums;

/* Exture+ 시장접속 프로토콜 20210609 기준
 * 현물 Sidecar 대상 : 11,13,17,31,41
 * 현물 공매도가격제한 예외 적용 대상 : (증권그룹 ST,SC,RT,MF,IF,FS,DR에 대해) 11,12,15,16,19,21,23,25
 * 현물 공매도가격제한 예외 적용시 파생시장조성자 점검대상: 22
 * 공매도금지시장조치시 호가입력제한 예외 적용 대상 : 21, 22, 23, 25
 */
public enum ProgramType {
    일반("00"),
    차익거래("10"), // 주식 및 주가지수 파생상품만 사용
    지수차익거래("11"),
    주식차익거래("12"),
    ETF차익거래_지수비차익거래("13"),
    ETF차익거래_지수비차익거래아닌경우("14"),
    DR차익거래("15"),
    KDR차익거래("16"),
    ETN차익거래_지수비차익거래("17"),
    ETN차익거래_지수비차익거래아닌경우("18"),
    섹터지수차익거래("19"),
    헤지거래("20"), // 주식 및 주가지수 파생상품만 사용
    ELW_LP_헤지거래("21"),
    파생MM_헤지거래("22"),
    ETF_LP_헤지거래("23"),
    장외파생상품헤지거래("24"),
    ETN_LP_헤지거래("25"),
    비차익거래("30"),
    지수비차익거래("31"),
    설정거래("40"),
    ETF설정거래_지수비차익거래("41"),
    ETF설정거래_지수비차익거래아닌경우("42");


    private final String typeCode;
    private final byte[] byteValue;

    ProgramType(String typeCode) {
        this.typeCode = typeCode;
        this.byteValue = typeCode.getBytes();
    }

    public String getValue() {
        return typeCode;
    }

    public byte[] getByteValue() {
        return byteValue;
    }

    public static ProgramType getProgramType(String typeStr) {
        for (ProgramType type : values()) {
            if (type.typeCode.equals(typeStr))
                return type;
        }
        return 일반;
    }

}
