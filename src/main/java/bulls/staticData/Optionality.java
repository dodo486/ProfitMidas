package bulls.staticData;

import bulls.bs.CallPut;
import bulls.tool.pricing.BSNew;

import java.time.LocalDate;

public interface Optionality {
    String getIsinCode();

    String getUnderlyingCode();

    CallPut getCallPut();

    int getStrikePrice();

    String getExpiryString();

    long getExpiryId();

    int getDateTillExpiry();

    String getMaturity();

    double getMultiplier();

    String getProductName();

    LocalDate get만기();

    int getDistanceFromATM();

    boolean getIsOnDutyToday();

    //아래 공식에 S는 선물 현재가를 현재가치로 할인한 가격을 사용하고 q = 0 대입
    //double S = underlyingFuturesBidAsk.bidPrice[0] * FastMath.Exp(-r * t)
    default double getDelta(double S, double T, double r, double q, double v) {
        return BSNew.gDelta(getCallPut(), S, getStrikePrice(), T, r, q, v);
    }

    default double getGamma(double S, double T, double r, double q, double v) {
        return BSNew.gGamma(S, getStrikePrice(), T, r, q, v);
    }

    default double getTheta(CallPut callPut, double S, double T, double r, double q, double v) {
        return BSNew.gTheta(getCallPut(), S, getStrikePrice(), T, r, q, v);
    }

    default double getVega(double S, double T, double r, double q, double v) {
        return BSNew.gVega(S, getStrikePrice(), T, r, q, v);
    }

    default double getRho(double S, double T, double r, double q, double v) {
        return BSNew.gRho(getCallPut(), S, getStrikePrice(), T, r, q, v);
    }

    default Double getTimeTillExpiry() {
        return TimeTillExpiryCenter.Instance.getTimeTillExpiry(getExpiryId());
    }
}
