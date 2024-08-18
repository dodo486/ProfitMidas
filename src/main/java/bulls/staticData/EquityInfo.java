package bulls.staticData;


import java.util.Date;

// for mongodb deserialize
public class EquityInfo {
    //	"isinCode" : "KRA5211447A9",
//	"type" : "A0011",
//	"productName" : "미래CB83신한지주콜",
//	"KOSPI여부" : "N",
//	"거래정지여부" : "N",
//	"관리종목여부" : "N",
//	"증권그룹ID" : "EW",
//	"기준가격" : NumberLong("90"),
//	"전일종가구분코드" : "3",
//	"KOSPI200섹터업종" : " ",
//	"상장주식수" : NumberLong("10200000"),
//	"전일종가" : NumberLong("90"),
//	"전일시총" : 918000000,
//	"ELW행사종료일" : NumberInt("20180212"),
//	"ELW행사가격" : 47600,
//	"date" : ISODate("2018-01-26T00:00:00.000+09:00"),
//	"lastUpdate" : ISODate("2018-01-26T06:52:24.497+09:00")
//}
    public Date date;
    public String isinCode;
    public String shortCode;
    public String type;
    public String productName;
    public String KOSPI여부;
    public String 거래정지여부;
    public String 관리종목여부;
    public String 증권그룹ID;
    public long 기준가격;
    public long 상한가;
    public long 하한가;
    public String 전일종가구분코드;
    public String KOSPI200섹터업종;
    public long 상장주식수;
    public long 전일종가;
    public long 전일누적체결수량;
    public long 전일누적거래대금;
    public long 전일시총;
    public int ELW행사종료일;
    public double ELW행사가격;


}
