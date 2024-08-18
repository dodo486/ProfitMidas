package bulls.etf;

import bulls.staticData.EquityInfo;

public class ETPEquityInfo extends EquityInfo {

//    public ETPType etpType; // ETP상품구분코드
//    public IndexMarketType indexMarketType; // 지수시장분류ID
//    public IndexUnderlyingType indexUnderlyingType; // 지수자산분류ID1
//    public IndexMultiplierType indexMultiplierType;

    public String ETP상품구분코드;
    public String 참조지수레버리지인버스구분코드; // P1:일반, P2: 2X레버리지, N1: 인버스 , N2: 2X인버스
    public String 지수시장분류ID; // 010100 : 코스피, 010200 : 코스닥
    public String 지수자산분류ID1; // 010100 : 코스피, 010200 : 코스닥

    public int getETFMultiplier() {
        if (참조지수레버리지인버스구분코드.equals("P2"))
            return 2;
        else if (참조지수레버리지인버스구분코드.equals("N1"))
            return -1;
        else if (참조지수레버리지인버스구분코드.equals("N2"))
            return -2;
        else
            return 1;
    }

//    public void init() {
//        etpType = ETPType.fromCode(ETP상품구분코드);
//        indexMarketType = IndexMarketType.fromCode(지수시장분류ID);
//        indexUnderlyingType = IndexUnderlyingType.fromCode(지수자산분류ID1);
//        indexMultiplierType = IndexMultiplierType.fromCode(참조지수레버리지인버스구분코드);
//    }


}
