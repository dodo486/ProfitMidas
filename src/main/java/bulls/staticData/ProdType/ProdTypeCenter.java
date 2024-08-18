package bulls.staticData.ProdType;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import bulls.designTemplate.EarlyInitialize;
import bulls.staticData.*;

import java.util.Collection;
import java.util.HashMap;

public enum ProdTypeCenter implements EarlyInitialize {
    Instance;
    public final HashMap<String, ProdType> prodTypeMap;

    // 해당 dut에 대한 모든 파생상품
    public final Multimap<ProdType, String> typeToCodeMap;

    ProdTypeCenter() {
        prodTypeMap = new HashMap<>();
        typeToCodeMap = HashMultimap.create();

        FuturesInfoCenter.Instance.futuresInfoMap.forEach((isinCode, value) -> {
            ProdType pType = getProdType(isinCode);
            if (pType != null)
                typeToCodeMap.put(pType, isinCode);
        });

        EquityInfoCenter.Instance.getAllEquityInfo()
                .forEach(info -> {
                    String isinCode = info.isinCode;
                    ProdType pType = getProdType(isinCode);
                    if (pType != null)
                        typeToCodeMap.put(pType, isinCode);
                });
    }

    public Collection<String> getAllCodeOf(ProdType pType) {
        return typeToCodeMap.get(pType);
    }


    public ProdType getProdType(String isinCode) {
        ProdType type = prodTypeMap.get(isinCode);
        if (type != null)
            return type;
        EquityInfo ei = EquityInfoCenter.Instance.getEquityInfo(isinCode);
        if (ei != null) {
            type = ProdType.fromEquityValues(EquitySecurityGroupType.getTypeFromCode(ei.증권그룹ID), ei.KOSPI여부.equals("Y"));
            prodTypeMap.put(isinCode, type);
            return type;
        }

        FuturesInfo fi = FuturesInfoCenter.Instance.futuresInfoMap.get(isinCode);
        if (fi != null) {
            type = ProdType.fromDerivativesValues(DerivativesType.getTypeFromCode(isinCode),
                    fi.기초자산시장ID != null ? fi.기초자산시장ID : DerivativesUnderlyingMarketType.UNDEF, fi.prodClassType, fi.기초자산ID);
//                        DerivativesUnderlyingMarketType.getTypeFromCode(fi.기초자산시장ID),
//                        DerivativesUnderlyingType.getTypeFromCode(fi.기초자산ID));
            prodTypeMap.put(isinCode, type);
            return type;
        }
        IndexCode indexCode = IndexCode.parseIndexIsinCode(isinCode);
        if (indexCode != null) {
            type = ProdType.Index;
            prodTypeMap.put(isinCode, type);
            return type;
        }
        if (isinCode.startsWith("KR7") || isinCode.startsWith("KRA"))
            type = ProdType.EquityUnknown;
        else if (isinCode.startsWith("KR4")) {
            type = ProdType.DerivativesUnknown;
        } else {
            type = ProdType.Unknown;
        }
        prodTypeMap.put(isinCode, type);
        return type;
    }

    public static void main(String[] args) {
//        ProdType pt = ProdTypeCenter.Instance.getProdType(TempConf.K200_REPRESENTING_ETFISINCODE);
//        System.out.println(pt);

        int i = 0;
        for (FuturesInfo fi : FuturesInfoCenter.Instance.futuresInfoMap.values()) {
            ProdType pt = ProdTypeCenter.Instance.getProdType(fi.isinCode);
            System.out.println(i + "\t" + fi.isinCode + "\t" + pt.toString() + "\t" + fi.productName + " isStockFutures:" + pt.isStockFut());
            i++;
        }
        i = 0;
        for (EquityInfo ei : EquityInfoCenter.Instance.getAllEquityInfo()) {
            ProdType pt = ProdTypeCenter.Instance.getProdType(ei.isinCode);
            System.out.println(i + "\t" + ei.isinCode + "\t" + pt.toString() + "\t" + ei.productName + " isStockFutures:" + pt.isEquityStock());

            i++;
        }
//        // MarketTypeCenter랑 결과 비교 테스트
//        int i = 0 ;
//        for(FuturesInfo fi : FuturesInfoCenter.Instance.futuresInfoMap.values()){
//            MarketType mt1 = MarketTypeCenter.Instance.getType(fi.isinCode);
//            MarketType mt2 = ProdTypeCenter.Instance.getType(fi.isinCode);
//            if(mt1 == mt2){
//                System.out.println(i + " isinCode : " + fi.isinCode + " passed!");
//            }
//            else{
//                System.out.println(i + " isinCode : " + fi.isinCode + " failed!!!!!!!!!!!!!!!!!!!!");
//                mt1 = MarketTypeCenter.Instance.getType(fi.isinCode);
//                mt2 = ProdTypeCenter.Instance.getType(fi.isinCode);
//            }
//            i++;
//        }
    }
}
//enum OptionsType { Call, Put, Unknown }


