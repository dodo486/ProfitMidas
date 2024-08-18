package bulls.staticData.ProdType;

import bulls.log.DefaultLogger;

public enum DerivativesProdClassType {
    돈육, 금리, 환율, 유로스톡스, 지수, 금, 개별주식, 섹터, 변동성, UNKNOWN, NA;

    public static DerivativesProdClassType getTypeFromCode(DerivativesUnderlyingType dut) {
        String dutStr = dut.toString();
        if (dut == DerivativesUnderlyingType.UNKNOWN) {
            DefaultLogger.logger.error("Not available underlying type {}", dut);
            return DerivativesProdClassType.UNKNOWN;
        }
        if (dut == DerivativesUnderlyingType.NA) {
            DefaultLogger.logger.error("Not available underlying type {}", dut);
            return DerivativesProdClassType.NA;
        }

        if (dut == DerivativesUnderlyingType.K2I || dut == DerivativesUnderlyingType.KQI || dut == DerivativesUnderlyingType.MKI || dut == DerivativesUnderlyingType.WKI || dut == DerivativesUnderlyingType.XI3)
            return DerivativesProdClassType.지수;
        else if (dut == DerivativesUnderlyingType.LHG)
            //돈육
            return DerivativesProdClassType.돈육;
        else if (dut == DerivativesUnderlyingType.BM3 || dut == DerivativesUnderlyingType.BM5 || dut == DerivativesUnderlyingType.BMA || dut == DerivativesUnderlyingType.B3A)
            //국채
            return DerivativesProdClassType.금리;
        else if (dut == DerivativesUnderlyingType.CNH || dut == DerivativesUnderlyingType.EUR || dut == DerivativesUnderlyingType.JPY || dut == DerivativesUnderlyingType.USD)
            //FX
            return DerivativesProdClassType.환율;
        else if (dut == DerivativesUnderlyingType.EST)
            //유로스톡스
            return DerivativesProdClassType.유로스톡스;
        else if (dut == DerivativesUnderlyingType.KGD)
            //금;
            return DerivativesProdClassType.금;
        else if (dut == DerivativesUnderlyingType.VKI)
            return DerivativesProdClassType.변동성;
        else if (dutStr.charAt(0) == 'S') {
            //개별주식
            return DerivativesProdClassType.개별주식;
        } else if (dutStr.charAt(0) == 'X') {
            //섹터
            return DerivativesProdClassType.섹터;
        } else {
            return DerivativesProdClassType.UNKNOWN;
        }

    }
}