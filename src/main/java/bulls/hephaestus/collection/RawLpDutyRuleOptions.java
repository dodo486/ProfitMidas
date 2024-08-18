package bulls.hephaestus.collection;

import org.bson.codecs.pojo.annotations.BsonIgnore;

import java.util.Date;

public class RawLpDutyRuleOptions {
    public String underlyingID;//MKI <= key
    public int 대상월물번호;//NumberInt(1) <= key
    public String underlyingIsinCode;//KR499999999P",
    public String 종목명;//미니코스피 옵션",
    public boolean isMyDuty;//주식옵션 의무종목 여부,
    public String 의무수량;//5,5,10,10,30,30,30,30,30",
    @BsonIgnore
    public int[] 의무수량Array = null;
    public int 의무스프레드_최소틱;//NumberInt(10),
    public int 의무스프레드_최대틱;//NumberInt(26),
    public double 의무스프레드_비율;//0.16,
    public int 공격스프레드_최소틱;//NumberInt(10),
    public int 공격스프레드_최대틱;//NumberInt(26),
    public double 공격스프레드_비율;//0.16,
    public double 의무스프레드_비율_버퍼;//0.16,
    public int 일방향호가제시가능틱;//NumberInt(3),
    public Date DutyStartDate;//ISODate("2016-01-09T00:00:00.000+09:00"),
    public Date DutyEndDate;//ISODate("2099-07-12T00:00:00.000+09:00"),
    public double 일중의무이행률;//0.35,
    public double 계약기간의무이행률;//0.7,
    public int 호가의무발생시작시간;//NumberInt(905),
    public int 호가의무발생종료시간;//NumberInt(1535),
    public String calendar;//옵션의무",
    public String prodType;//KOSPI200Options",
    public String 의무수량Moneyness;//-1,0,1,2,3,4,5,6,7"
    @BsonIgnore
    public int[] 의무수량MoneynessArray = null;

}