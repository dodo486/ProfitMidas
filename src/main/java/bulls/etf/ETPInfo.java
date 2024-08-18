package bulls.etf;

import bulls.hephaestus.collection.ETFLpMaster;
import bulls.staticData.FuturesInfo;
import bulls.staticData.FuturesInfoCenter;
import bulls.staticData.ProdType.DerivativesUnderlyingType;
import bulls.staticData.ProdType.equity.ETPType;
import bulls.staticData.ProdType.equity.IndexMarketType;
import bulls.staticData.ProdType.equity.IndexMultiplierType;
import bulls.staticData.ProdType.equity.IndexUnderlyingType;

public class ETPInfo {

    public final ETPEquityInfo etpEquityInfo;
    public final boolean isFuturesExist;
    public ETPType etpType; // ETP상품구분코드
    public IndexMarketType indexMarketType; // 지수시장분류ID
    public IndexUnderlyingType indexUnderlyingType; // 지수자산분류ID1
    public IndexMultiplierType indexMultiplierType;
    public DerivativesUnderlyingType uType;
    public String recentF;

    private ETPInfo(ETPEquityInfo etpEquityInfo) {
        this.etpEquityInfo = etpEquityInfo;
        etpType = ETPType.fromCode(etpEquityInfo.ETP상품구분코드);
        indexMarketType = IndexMarketType.fromCode(etpEquityInfo.지수시장분류ID);
        indexUnderlyingType = IndexUnderlyingType.fromCode(etpEquityInfo.지수자산분류ID1);
        indexMultiplierType = IndexMultiplierType.fromCode(etpEquityInfo.참조지수레버리지인버스구분코드);
        this.uType = ETFLpMaster.Instance.getDUTypeOf(etpEquityInfo.isinCode);

        FuturesInfo fInfo = FuturesInfoCenter.Instance.getMostRecentFuturesOf(uType);
        if (fInfo != null) {
            isFuturesExist = true;
            recentF = fInfo.isinCode;
        } else {
            isFuturesExist = false;
        }
    }

    public static ETPInfo create(ETPEquityInfo etpEquityInfo) {
        ETPInfo info = new ETPInfo(etpEquityInfo);
        return info;
    }
}
