package bulls.staticData;

import bulls.bs.CallPut;
import bulls.dateTime.TimeCenter;
import bulls.exception.InvalidCodeException;
import bulls.exception.NoVolException;
import bulls.staticData.ProdType.DerivativesUnderlyingType;
import bulls.staticData.ProdType.ProdType;
import bulls.staticData.ProdType.ProdTypeCenter;
import bulls.staticData.tick.TickCalculatorCenter;
import bulls.tool.pricing.BS;
import org.apache.commons.math3.util.FastMath;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public final class OptionIdentifier implements Optionality {
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
    final Date 만기date;
    final int distanceFromATM;
    final boolean isOnDutyToday;
    final String optionCode;
    final FuturesInfo info;
    final ProdType prodType;
    public final DerivativesUnderlyingType uType;

    public OptionIdentifier representOptId = null;
    public OptionIdentifier parityOptId = null;
    public OptionIdentifier miniOptId = null;
    public boolean isMini = false;
    public boolean hasMini = false;
//    public final String underlyingKey;

    OptionIdentifier(String underlyingIsinCode, int strikePrice, String optionCode, CallPut callPut, String productName, double multiplier, boolean isOnDutyToday, int distanceFromATM, String matDate, DerivativesUnderlyingType uType) {
        info = FuturesInfoCenter.Instance.getFuturesInfo(optionCode);
        this.underlyingCode = underlyingIsinCode;
        this.optionCode = optionCode;
        this.strikePrice = strikePrice;

        ProdType pType = ProdTypeCenter.Instance.getProdType(optionCode);

        this.callPut = pType.isCallOption() ? CallPut.CALL : CallPut.PUT;
        this.isOnDutyToday = isOnDutyToday;

        if (callPut == CallPut.CALL)
            this.distanceFromATM = distanceFromATM;
        else
            this.distanceFromATM = -distanceFromATM;

        expiryString = ExpiryCenter.Instance.getExpiryString(optionCode, uType == DerivativesUnderlyingType.WKI);
        expiryId = ExpiryCenter.Instance.getExpiryId(optionCode);
        this.uType = uType;
//        underlyingKey = optionCode.substring(4, 6);

        maturity = matDate;
        만기 = LocalDate.parse(maturity, DateTimeFormatter.ofPattern("yyyyMMdd"));
        만기date = TimeCenter.getLocalDateAsDateType(만기);
        dateTillExpiry = ExpiryCenter.Instance.getDayTillMaturity(info.isinCode);


//        DefaultLogger.logger.debug(optionCode+" 만기일:" + maturity);

        this.multiplier = multiplier; // FuturesInfoCenter.Instance.getMultiplierForProfit(optionCode);
        this.productName = productName;
        this.prodType = ProdTypeCenter.Instance.getProdType(optionCode);
    }

    public static OptionIdentifier createStockOption(String underlyingIsinCode, int strikePrice, String optionCode, int atmPrice, String productName, double multiplier, String matDate, DerivativesUnderlyingType uType) {

        boolean isOnDutyToday;
        var strikeTickFunc = TickCalculatorCenter.Instance.getStrikeTickFunction(ProdType.KOSPIStockCallOptions);
        int tickCount = strikeTickFunc.getTickCountBetween(strikePrice, atmPrice);
        isOnDutyToday = tickCount <= 3 && tickCount >= -3;

        int distanceFromATM = tickCount;

        CallPut cp;
        try {
            cp = CallPut.of(optionCode);
        } catch (InvalidCodeException e) {
            e.printStackTrace();
            return null;
        }

        return new OptionIdentifier(underlyingIsinCode, strikePrice, optionCode, cp, productName, multiplier, isOnDutyToday, distanceFromATM, matDate, uType);
    }

    public static OptionIdentifier createK200Option(String underlyingIsinCode, int strikePrice, String optionCode, int atmPrice, String productName, double multiplier, String matDate) {
        boolean isOnDutyToday;
        var strikeTickFunc = TickCalculatorCenter.Instance.getStrikeTickFunction(ProdType.K200CallOption);
        int tickCount = strikeTickFunc.getTickCountBetween(strikePrice, atmPrice);
        String mat1 = null, mat2 = null;
        for (FuturesInfo fi : FuturesInfoCenter.Instance.getAllRecentAndNextFuturesInfo()) {
            if (fi.기초자산ID == DerivativesUnderlyingType.MKI && ProdTypeCenter.Instance.getProdType(fi.isinCode).isK200MiniFut()) {
                if (mat1 == null)
                    mat1 = fi.matDate;
                else if (mat2 == null)
                    mat2 = fi.matDate;
            }
        }

        CallPut cp;
        try {
            cp = CallPut.of(optionCode);
        } catch (InvalidCodeException e) {
            e.printStackTrace();
            return null;
        }

        if (mat1 != null && mat1.equals(matDate) || mat2 != null && mat2.equals(matDate)) {
            ProdType pType = ProdTypeCenter.Instance.getProdType(optionCode);
            if (pType.isCallOption()) {
                isOnDutyToday = tickCount >= -1 && tickCount <= 7;
            } else {
                isOnDutyToday = tickCount <= 1 && tickCount >= -7;
            }
        } else {
            isOnDutyToday = false;
        }

        int distanceFromATM = tickCount;

        return new OptionIdentifier(underlyingIsinCode, strikePrice, optionCode, cp, productName, multiplier, isOnDutyToday, distanceFromATM, matDate, DerivativesUnderlyingType.K2I);
    }

    public static OptionIdentifier createMiniOption(String underlyingIsinCode, int strikePrice, String optionCode,
                                                    int atmPrice, String productName, double multiplier, String matDate) {
        boolean isOnDutyToday;
        // Moneyness 를 위한 틱 카운터, 미니도 원지수와 동일
        var strikeTickFunc = TickCalculatorCenter.Instance.getStrikeTickFunction(ProdType.K200MiniCallOption);
        int tickCount = strikeTickFunc.getTickCountBetween(strikePrice, atmPrice);
        FuturesInfo fi = FuturesInfoCenter.Instance.getMostRecentFuturesOf(DerivativesUnderlyingType.MKI);
        CallPut cp;
        try {
            cp = CallPut.of(optionCode);
        } catch (InvalidCodeException e) {
            e.printStackTrace();
            return null;
        }

        if (fi != null && fi.matDate.equals(matDate)) {
            ProdType pType = ProdTypeCenter.Instance.getProdType(optionCode);
            if (pType.isCallOption()) {
                isOnDutyToday = tickCount >= -1 && tickCount <= 7;
            } else {
                isOnDutyToday = tickCount <= 1 && tickCount >= -7;
            }
        } else {
            isOnDutyToday = false;
        }

        int distanceFromATM = tickCount;

        return new OptionIdentifier(underlyingIsinCode, strikePrice, optionCode, cp, productName, multiplier, isOnDutyToday, distanceFromATM, matDate, DerivativesUnderlyingType.MKI);
    }

    public static OptionIdentifier createWeeklyOption(String underlyingIsinCode, int strikePrice, String
            optionCode, int atmPrice, String productName, double multiplier, String matDate) {
        boolean isOnDutyToday;
        // Moneyness 를 위한 틱 카운터, 위클리도 원지수와 동일
        var strikeTickFunc = TickCalculatorCenter.Instance.getStrikeTickFunction(ProdType.K200CallOption);
        int tickCount = strikeTickFunc.getTickCountBetween(strikePrice, atmPrice);
        isOnDutyToday = false;
        int distanceFromATM = tickCount;

        CallPut cp;
        try {
            cp = CallPut.of(optionCode);
        } catch (InvalidCodeException e) {
            e.printStackTrace();
            return null;
        }

        return new OptionIdentifier(underlyingIsinCode, strikePrice, optionCode, cp, productName, multiplier, isOnDutyToday, distanceFromATM, matDate, DerivativesUnderlyingType.WKI);
    }

    public static OptionIdentifier createKosdaq150Option(String underlyingIsinCode, int strikePrice, String
            optionCode, int atmPrice, String productName, double multiplier, String matDate) {
        boolean isOnDutyToday;
        var strikeTickFunc = TickCalculatorCenter.Instance.getStrikeTickFunction(ProdType.KQ150CallOption);
        int tickCount = strikeTickFunc.getTickCountBetween(strikePrice, atmPrice);
        isOnDutyToday = tickCount <= 5 && tickCount >= -5;

        int distanceFromATM = tickCount;

        CallPut cp;
        try {
            cp = CallPut.of(optionCode);
        } catch (InvalidCodeException e) {
            e.printStackTrace();
            return null;
        }

        return new OptionIdentifier(underlyingIsinCode, strikePrice, optionCode, cp, productName, multiplier, isOnDutyToday, distanceFromATM, matDate, DerivativesUnderlyingType.KQI);
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
        Double vol = VolCenter.Instance.getVol(this);
        if (vol == null)
            return 0d;

        double r = TempConf.조달금리;
        return BS.bs_formula(callPut, underlyingPrice, strikePrice, getTimeTillExpiry(), r, 0, vol) * 0.01d;
    }


    public Double getLogicalPriceOf(int underlyingPrice) {
        Double vol = VolCenter.Instance.getVol(this);
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
        Double vol = VolCenter.Instance.getVol(this);
        if (vol == null) {
            String msg = String.format("%s(%s) 종목의 vol 이 없습니다.", getKey(), AliasManager.Instance.getKoreanFromIsin(underlyingCode));
            throw new NoVolException(msg);
        }

        double r = TempConf.조달금리;
        double theta = BS.gTheta(callPut, underlyingPrice, strikePrice, getTimeTillExpiry(), r, 0, vol) / 365;
        if (Double.isNaN(theta))
            theta = 0;

        return theta;
    }

//    public Double getDeltaOf(double underlyingPrice, double productPrice) {
//        double r = VolCenter.Instance.getRFR();
//        double vol = 0.14; // BS.bs_impliedVol(callPut, underlyingPrice, strikePrice, getTimeTillExpiry(), r, 0 , productPrice);
//        double delta = BS.gDelta(callPut, underlyingPrice, strikePrice, getTimeTillExpiry(), r, 0, vol);
//        return delta;
//    }

    public String getKey() {
        String sb = underlyingCode +
                "_" +
                expiryString +
                "_" +
                callPut +
                "_" +
                strikePrice +
                "_" +
                uType;
        return sb;
    }

//    public String getParityKey() {
//        StringBuilder sb = new StringBuilder();
//        sb.append(underlyingCode);
//        sb.append("_");
//        sb.append(yearCode);
//        sb.append(monthCode);
//        sb.append("_");
//        sb.append(callPut.opposite());
//        sb.append("_");
//        sb.append(strikePrice);
//        sb.append("_");
//        sb.append(uType);
//        return sb.toString();
//    }

    public static String getKey(String underlyingIsinCode, String expiryString, int strike, CallPut
            callPut, DerivativesUnderlyingType duType) {
        String sb = underlyingIsinCode +
                "_" +
                expiryString +
                "_" +
                callPut +
                "_" +
                strike +
                "_" +
                duType;
        return sb;
    }


    @Override
    public boolean equals(Object obj) {
        OptionIdentifier id = (OptionIdentifier) obj;
        return optionCode.equals(id.getIsinCode());
    }

    @Override
    public int hashCode() {
        return optionCode.hashCode();
    }

    @Override
    public String toString() {
        String sb = getKey() + "_" +
                maturity +
                "_" +
                distanceFromATM +
                "_" +
                isOnDutyToday;
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
    public static int compareByStirke(OptionIdentifier id1, OptionIdentifier id2) {
        if (id1.strikePrice > id2.strikePrice)
            return 1;
        else if (id1.strikePrice < id2.strikePrice)
            return -1;
        else
            return 0;
    }

    public String getNormalizedOptionName(String uKorean) {
        StringBuilder shortName = new StringBuilder();
        if (uKorean.equals("코스피200")) {
            uKorean = "KP200";
        } else if (uKorean.equals("코스닥150"))
            uKorean = "KQ150";

        shortName.append(uKorean);
        shortName.append(" ");
        shortName.append(callPut.toString(), 0, 1);
        shortName.append(" ");
//        shortName.append(expiryKey);
        shortName.append(expiryString);
        shortName.append(" ");
        shortName.append(strikePrice);
        if (uType == DerivativesUnderlyingType.MKI)
            shortName.append(" M");
//        else if (uType == DerivativesUnderlyingType.WKI)
//            shortName.append(" W");
        return shortName.toString();
    }

    public FuturesInfo getFuturesInfo() {
        return info;
    }

    @Override
    public String getIsinCode() {
        return optionCode;
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