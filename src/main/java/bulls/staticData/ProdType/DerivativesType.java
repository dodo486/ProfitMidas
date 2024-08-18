package bulls.staticData.ProdType;

import bulls.annotation.UseIsinCodeDirectly;

public enum DerivativesType {
    Futures, CallOption, PutOption, Spread, Flex, UNKNOWN, NA;

    @UseIsinCodeDirectly
    public static DerivativesType getTypeFromCode(String isinCode) {
        return
                isinCode.charAt(3) == '1' ? DerivativesType.Futures :
                        isinCode.charAt(3) == '2' ? DerivativesType.CallOption :
                                isinCode.charAt(3) == '3' ? DerivativesType.PutOption :
                                        isinCode.charAt(3) == '4' ? DerivativesType.Spread :
                                                isinCode.charAt(3) == '7' ? DerivativesType.Flex : DerivativesType.UNKNOWN;
    }
}