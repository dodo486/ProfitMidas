package bulls.staticData;

import bulls.staticData.ProdType.DerivativesProdClassType;
import bulls.staticData.ProdType.DerivativesUnderlyingMarketType;
import bulls.staticData.ProdType.DerivativesUnderlyingType;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

// for mongodb deserialize
public class FuturesInfo {

    public String isinCode;
    public String productName;
    public String underlyingIsinCode;
    public Double 기준가;
    public String type;


    public Date date;
    public Date 만기;
    public String matDate;
    public DerivativesUnderlyingType 기초자산ID;
    public DerivativesProdClassType prodClassType;
    //public String 소속상품군;
    public DerivativesUnderlyingMarketType 기초자산시장ID;
    public Double CD금리;
    public Double 거래단위;
    public Double multiplier;
    public Integer ATM구분; // ATM - 1, ITM - 2, OTM - 3

    public Integer priceDivider = 1;

    public String spreadRecentIsin;
    public String spreadNext;
    public Double strikePrice;

    public Double 가격제한1단계상한가;
    public Double 가격제한1단계하한가;

    public String 결제주;

    public Date get만기() {
        return 만기;
    }

    public LocalDateTime get만기CloseTime() {
        LocalDateTime 만기일 = 만기.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        return LocalDateTime.from(만기일).plusHours(15).plusMinutes(20);
    }

    // 21.10.14
    // 개별주식의 이벤트 등으로 인해 상장되어 있는 모든 주식선물의 만기가 같은 날로 조정되는 경우, 근월물/차월물 구분이 불가
    // 이를 위해 만기 순서 계산시 종목코드의 year, month 의 ascii 값으로 계산
    public int getMaturityAscii() {
        char year = isinCode.charAt(6);
        char month = isinCode.charAt(7);

        int recentness = year * 1000 + month;
        return recentness;
    }

    public Double getStrikePrice() {
        return strikePrice;
    }
}
