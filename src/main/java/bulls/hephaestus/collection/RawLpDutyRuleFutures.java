package bulls.hephaestus.collection;

import java.util.Date;

public class RawLpDutyRuleFutures {
    public String underlyingID; // Key
    public int 대상월물번호;//NumberInt(1) Key
    public String codePrefix;//4105",
    public String 종목명;//코스피200 미니 선물 차근월물",
    public boolean isMyDuty;//주식선물 의무종목 여부,
    public double 틱단위;//0.02,
    public int 의무수량;//NumberInt(5),
    public int 의무스프레드;//NumberInt(16),
    public double 일중의무이행률;//0.4,
    public int 일방향호가제시가능틱;//NumberInt(3),
    public Date DutyStartDate;//ISODate("2016-01-09T00:00:00.000+09:00"),
    public Date DutyEndDate;//ISODate("2029-01-04T00:00:00.000+09:00"),
    public double 계약기간의무이행률;//0.8,
    public int 호가의무발생시작시간;//NumberInt(905),
    public int 호가의무발생종료시간;//NumberInt(1535),
    public int priceDisplayMultiplier;//100,
    public String calendar;//옵션의무",
    public double 의무스프레드_비율;//0,
    public double 의무스프레드_비율_버퍼;//0,
    public int 의무스프레드_최대틱;//NumberInt(30),
    public int 의무스프레드_최소틱;//NumberInt(6),
}
