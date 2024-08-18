package bulls.staticData.ELW;

import bulls.bs.CallPut;
import bulls.exception.CodeNotFoundException;
import bulls.exception.NoVolException;
import bulls.staticData.*;
import bulls.staticData.ProdType.DerivativesUnderlyingType;
import bulls.staticData.ProdType.ProdType;
import bulls.staticData.tick.TickCalculatorCenter;
import bulls.tool.pricing.BS;
import org.apache.commons.math3.util.FastMath;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public final class ELWIdentifier implements Optionality {
    final String underlyingCode;
    final CallPut callPut;
    final int strikePrice;
    final String expiryString;

    final long expiryId;
    final int dateTillExpiry;
    final String maturity;
    final double multiplier;
    final String productName;
    final LocalDate 만기;
    final int distanceFromATM;
    final boolean isOnDutyToday;

    public final ELWIssuer issuer;
    final String elwIsinCode;
    public final String elwShortCode;
    public final double listedOptionToELWMultiplier;
    public OptionIdentifier listedOptionId;

    ELWIdentifier(ELWIssuer issuer, String underlyingIsinCode, int strikePrice, String elwIsinCode, String elwShortCode, String productName, double multiplier, boolean isOnDutyToday, int distanceFromATM, String matDate, CallPut cp) {
        this.issuer = issuer;
        this.underlyingCode = underlyingIsinCode;
        this.elwIsinCode = elwIsinCode;
        this.elwShortCode = elwShortCode;

        this.strikePrice = strikePrice;

        this.callPut = cp;

        this.isOnDutyToday = isOnDutyToday;

        if (callPut == CallPut.CALL)
            this.distanceFromATM = distanceFromATM;
        else
            this.distanceFromATM = -distanceFromATM;

        listedOptionToELWMultiplier = 2500;
//        underlyingKey = elwIsinCode.substring(4, 6);

        maturity = matDate;
        만기 = LocalDate.parse(maturity, DateTimeFormatter.ofPattern("yyyyMMdd"));

        String expiryStringCandidate = ExpiryCenter.Instance.getExpiryString(elwIsinCode, false);
        if (expiryStringCandidate == null)
            expiryStringCandidate = PredefinedString.UNKNOWN_EXPIRY_STRING;

        expiryString = expiryStringCandidate;
        expiryId = ExpiryCenter.Instance.getExpiryId(elwIsinCode);

        dateTillExpiry = ExpiryCenter.Instance.getDayTillMaturity(elwIsinCode);


//        DefaultLogger.logger.debug(elwIsinCode + " 만기일:" + maturity);

        this.multiplier = multiplier; // FuturesInfoCenter.Instance.getMultiplierForProfit(elwIsinCode);
        this.productName = productName;
        try {
            List<DerivativesUnderlyingType> duTypeList = DerivativesUnderlyingType.getTypeListFromIsinCode(underlyingIsinCode);
            if (duTypeList.size() == 1) {
                this.listedOptionId = OptionIdentifierCenter.Instance.getOptionCode(underlyingIsinCode, expiryString, strikePrice, callPut, duTypeList.get(0));
            } else if (duTypeList.size() > 1) {
                DerivativesUnderlyingType duType = duTypeList.get(0);
                if (duType == DerivativesUnderlyingType.MKI)
                    duType = DerivativesUnderlyingType.K2I;
                this.listedOptionId = OptionIdentifierCenter.Instance.getOptionCode(underlyingIsinCode, expiryString, strikePrice, callPut, duType);
            }
        } catch (CodeNotFoundException cnfe) {
            this.listedOptionId = null;
        }
    }

    public static ELWIdentifier createStockELW(ELWIssuer issuer, String underlyingIsinCode, int strikePrice, String elwIsinCode, String elwShortCode, int atmPrice, String productName, double multiplier, String matDate, CallPut cp) {

        boolean isOnDutyToday;
        var strikeTickFunc = TickCalculatorCenter.Instance.getStrikeTickFunction(ProdType.KOSPIStockCallOptions);
        int tickCount = strikeTickFunc.getTickCountBetween(strikePrice, atmPrice);
        isOnDutyToday = tickCount <= 3 && tickCount >= -3;

        int distanceFromATM = tickCount;

        return new ELWIdentifier(issuer, underlyingIsinCode, strikePrice, elwIsinCode, elwShortCode, productName, multiplier, isOnDutyToday, distanceFromATM, matDate, cp);
    }

    public static ELWIdentifier createK200ELW(ELWIssuer issuer, String underlyingIsinCode, int strikePrice, String elwIsinCode, String elwShortCode, int atmPrice, String productName, double multiplier, String matDate, CallPut cp) {
        boolean isOnDutyToday;
        var strikeTickFunc = TickCalculatorCenter.Instance.getStrikeTickFunction(ProdType.K200CallOption);
        int tickCount = strikeTickFunc.getTickCountBetween(strikePrice, atmPrice);
        isOnDutyToday = tickCount <= 5 && tickCount >= -5;

        int distanceFromATM = tickCount;

        return new ELWIdentifier(issuer, underlyingIsinCode, strikePrice, elwIsinCode, elwShortCode, productName, multiplier, isOnDutyToday, distanceFromATM, matDate, cp);
    }

    public static ELWIdentifier createKosdaq150ELW(ELWIssuer issuer, String underlyingIsinCode, int strikePrice, String elwIsinCode, String elwShortCode, int atmPrice, String productName, double multiplier, String matDate, CallPut cp) {
        boolean isOnDutyToday;
        var strikeTickFunc = TickCalculatorCenter.Instance.getStrikeTickFunction(ProdType.KQ150CallOption);
        int tickCount = strikeTickFunc.getTickCountBetween(strikePrice, atmPrice);
        isOnDutyToday = tickCount <= 5 && tickCount >= -5;

        int distanceFromATM = tickCount;

        return new ELWIdentifier(issuer, underlyingIsinCode, strikePrice, elwIsinCode, elwShortCode, productName, multiplier, isOnDutyToday, distanceFromATM, matDate, cp);
    }

    public double getPayOffOn(int underlyingPrice) {
        if (callPut == CallPut.CALL) {
            return FastMath.max((underlyingPrice - strikePrice), 0) * multiplier;
        } else {
            return FastMath.max((strikePrice - underlyingPrice), 0) * multiplier;
        }
    }

    public double getDottedPayOffOn(int underlyingPrice) {
        if (callPut == CallPut.CALL) {
            return FastMath.max((underlyingPrice - strikePrice) / 100.0, 0) * multiplier;
        } else {
            return FastMath.max((strikePrice - underlyingPrice) / 100.0, 0) * multiplier;
        }
    }

    public double getExpiryPrice(int underlyingPrice) {
        if (callPut == CallPut.CALL) {
            return FastMath.max((underlyingPrice - strikePrice), 0);
        } else {
            return FastMath.max((strikePrice - underlyingPrice), 0);
        }
    }

    // *0.01d;
    public Double getIndexLogicalPriceOf(int underlyingPrice) {
        Double vol = VolCenter.Instance.getVol(listedOptionId);
        if (vol == null)
            return 0d;

        double r = TempConf.조달금리;
        return BS.bs_formula(callPut, underlyingPrice, strikePrice, getTimeTillExpiry(), r, 0, vol) * 0.01d;
    }


    public Double getLogicalPriceOf(int underlyingPrice) {
        Double vol = VolCenter.Instance.getVol(listedOptionId);
        if (vol == null)
            return 0d;

        double r = TempConf.조달금리;
        return BS.bs_formula(callPut, underlyingPrice, strikePrice, getTimeTillExpiry(), r, 0, vol);
    }

    public Double getLogicalPriceOf(int underlyingPrice, double vol) {
        double r = TempConf.조달금리;
        return BS.bs_formula(callPut, underlyingPrice, strikePrice, getTimeTillExpiry(), r, 0, vol);
    }

    public Double getThetaOf(double underlyingPrice) throws NoVolException {
        Double vol = VolCenter.Instance.getVol(listedOptionId);
        if (vol == null) {
            String msg = String.format("%s(%s) 종목의 vol 이 없습니다.", getKey(), AliasManager.Instance.getKoreanFromIsin(underlyingCode));
            throw new NoVolException(msg);
        }

        if (vol == 0.0)
            vol = 0.0001;

        double r = TempConf.조달금리;
        return BS.gTheta(callPut, underlyingPrice, strikePrice, getTimeTillExpiry(), r, 0, vol) / 365;
    }

//    public Double getDeltaOf(double underlyingPrice, double productPrice) {
//        double r = VolCenter.Instance.getRFR();
//        double vol = 0.14; // BS.bs_impliedVol(callPut, underlyingPrice, strikePrice, getTimeTillExpiry(), r, 0 , productPrice);
//        double delta = BS.gDelta(callPut, underlyingPrice, strikePrice, getTimeTillExpiry(), r, 0, vol);
//        return delta;
//    }

    public String getKey() {
        String sb = issuer.toString() +
                "_" +
                underlyingCode +
                "_" +
                expiryString +
                "_" +
                callPut +
                "_" +
                strikePrice;
//        if ( underlyingKey.equals("05"))
//            sb.append("_MINI");
        return sb;
    }

    public String getParityKey() {
        String sb = issuer.toString() +
                "_" +
                underlyingCode +
                "_" +
                expiryString +
                "_" +
                callPut.opposite() +
                "_" +
                strikePrice;
//        if ( underlyingKey.equals("05"))
//            sb.append("_MINI");
        return sb;
    }

    public static String getKey(ELWIssuer issuer, String underlyingIsinCode, char yearCode, char monthCode, int strike, CallPut callPut, boolean isMini) {
        StringBuilder sb = new StringBuilder();
        sb.append(issuer.toString());
        sb.append("_");
        sb.append(underlyingIsinCode);
        sb.append("_");
        sb.append(yearCode);
        sb.append(monthCode);
        sb.append("_");
        sb.append(callPut);
        sb.append("_");
        sb.append(strike);
        if (isMini)
            sb.append("_MINI");
        return sb.toString();
    }

    public String getNormalizedOptionName(String uKorean) {
        String shortName = uKorean +
                " " +
                callPut.toString().charAt(0) +
                " " +
                expiryString +
                " " +
                strikePrice +
                " " +
                issuer +
                elwShortCode.substring(3, 7);
        return shortName;
    }


    @Override
    public boolean equals(Object obj) {
        ELWIdentifier id = (ELWIdentifier) obj;
        return elwIsinCode.equals(id.getIsinCode());
    }

    @Override
    public int hashCode() {
        return elwIsinCode.hashCode();
    }

    @Override
    public String toString() {
        String sb = getKey() + "_" +
                maturity +
                "_" +
                distanceFromATM +
                "_" +
                isOnDutyToday +
                "_" +
                elwShortCode +
                "_" +
                elwIsinCode +
                "_" +
                productName;

        return sb;
    }

//    // 월물이 빠른것, 월물이 같다면 moneyness 순
//    public static int compareStockOptionPriority(OptionIdentifier id1, OptionIdentifier id2) {
//        if( id1.yearCode > id2.yearCode)
//            return 1;
//        else if ( id1.yearCode < id2.yearCode)
//            return -1;
//        if( id1.monthCode > id2.monthCode)
//            return 1;
//        else if ( id1.monthCode < id2.monthCode)
//            return -1;
//        int result = SOMoneyness.compare(id1.moneyness, id2.moneyness);
//        return result;
//    }

    // strike 높은 것 -> 낮은 순
    public static int compareByStirke(ELWIdentifier id1, ELWIdentifier id2) {
        if (id1.strikePrice > id2.strikePrice)
            return 1;
        else if (id1.strikePrice < id2.strikePrice)
            return -1;
        else
            return 0;
    }

    @Override
    public String getIsinCode() {
        return elwIsinCode;
    }

    @Override
    public String getUnderlyingCode() {
        return underlyingCode;
    }

    @Override
    public CallPut getCallPut() {
        return callPut;
    }

    @Override
    public int getStrikePrice() {
        return strikePrice;
    }

    @Override
    public String getExpiryString() {
        return expiryString;
    }

    public long getExpiryId() {
        return expiryId;
    }

    @Override
    public int getDateTillExpiry() {
        return dateTillExpiry;
    }

    @Override
    public String getMaturity() {
        return maturity;
    }

    @Override
    public double getMultiplier() {
        return multiplier;
    }

    @Override
    public String getProductName() {
        return productName;
    }

    @Override
    public LocalDate get만기() {
        return 만기;
    }

    @Override
    public int getDistanceFromATM() {
        return distanceFromATM;
    }

    @Override
    public boolean getIsOnDutyToday() {
        return isOnDutyToday;
    }
}