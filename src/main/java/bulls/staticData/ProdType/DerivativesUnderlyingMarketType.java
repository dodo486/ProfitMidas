package bulls.staticData.ProdType;

public enum DerivativesUnderlyingMarketType {
    UNDEF, STK, KSQ, CMD, GBL, UNKNOWN, NA;

    public static DerivativesUnderlyingMarketType getTypeFromCode(String underlyingMarketCode) {
        String code = underlyingMarketCode.trim();
        if (code.isEmpty())
            return DerivativesUnderlyingMarketType.UNDEF;
        else if (code.equals("STK"))
            return DerivativesUnderlyingMarketType.STK;
        else if (code.equals("KSQ"))
            return DerivativesUnderlyingMarketType.KSQ;
        else if (code.equals("CMD"))
            return DerivativesUnderlyingMarketType.CMD;
        else if (code.equals("GBL"))
            return DerivativesUnderlyingMarketType.GBL;
        else
            return DerivativesUnderlyingMarketType.UNKNOWN;
    }
}