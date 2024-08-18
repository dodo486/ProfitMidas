package bulls.staticData.tick;

import bulls.staticData.*;
import bulls.staticData.ProdType.ProdType;
import bulls.staticData.ProdType.ProdTypeCenter;
import bulls.staticData.tick.derivPriceTick.*;
import bulls.staticData.tick.derivStrikeTick.Kosdaq150OptionStrikeTickCalculator;
import bulls.staticData.tick.derivStrikeTick.Kospi200OptionStrikeTickCalculator;
import bulls.staticData.tick.derivStrikeTick.KospiKosdaqStockOptionStrikeTickCalculator;
import bulls.staticData.tick.equityPriceTick.ElwTickCalculator;
import bulls.staticData.tick.equityPriceTick.EtfTickCalculator;
import bulls.staticData.tick.equityPriceTick.KosdaqStockTickCalculator;
import bulls.staticData.tick.equityPriceTick.KospiStockTickCalculator;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <h2>TickCalculatorCenter</h2>
 * <p>* PriceTickFunction : 시세나 상품 가격을 다룰 때 사용하는 TickFunction</p>
 * <p>* QuoteTickFunction : 호가를 제출할 때 사용하는 TickFunction</p>
 * <p>* StrikeTickFunction : 행사가를 다룰 때 사용하는 TickFunction</p>
 */
public enum TickCalculatorCenter {
    Instance;
    final ConcurrentHashMap<String, TickFunction> priceTickCache = new ConcurrentHashMap<>();
    final ConcurrentHashMap<String, TickFunction> quoteTickCache = new ConcurrentHashMap<>();
    final ConcurrentHashMap<String, TickFunction> strikeTickCache = new ConcurrentHashMap<>();

    //실제 시장 tick 계산
    final HashMap<ProdType, TickFunction> derivPriceTickFuncMap = new HashMap<>();
    final HashMap<ProdType, TickFunction> equityPriceTickFuncMap = new HashMap<>();

    //Quote용 tick 계산(quote 줄이기 위해 코스닥 150 옵션 같은 상품에 적용 중)
    final HashMap<ProdType, TickFunction> derivQuoteTickFuncMap = new HashMap<>();
    final HashMap<ProdType, TickFunction> equityQuoteTickFuncMap = new HashMap<>();

    //strike tick 계산
    final HashMap<ProdType, TickFunction> strikeTickFuncMap = new HashMap<>();

    public static class NotSupportedProdTypeException extends Exception {
        public NotSupportedProdTypeException(ProdType pt) {
            super("NotSupportedProdTypeException_" + pt.toString());
        }
    }

    TickCalculatorCenter() {
        //price tick 입력
        derivPriceTickFuncMap.put(ProdType.K200Futures, Kospi200BigFuturesTickCalculator.getInstance());
        derivPriceTickFuncMap.put(ProdType.K200FuturesSpread, Kospi200BigFuturesTickCalculator.getInstance());
        derivPriceTickFuncMap.put(ProdType.K200CallOption, Kospi200BigOptionTickCalculator.getInstance());
        derivPriceTickFuncMap.put(ProdType.K200PutOption, Kospi200BigOptionTickCalculator.getInstance());
        derivPriceTickFuncMap.put(ProdType.K200WeeklyCallOption, Kospi200BigOptionTickCalculator.getInstance());
        derivPriceTickFuncMap.put(ProdType.K200WeeklyPutOption, Kospi200BigOptionTickCalculator.getInstance());

        derivPriceTickFuncMap.put(ProdType.K200MiniFutures, Kospi200MiniFuturesTickCalculator.getInstance());
        derivPriceTickFuncMap.put(ProdType.K200MiniFuturesSpread, Kospi200MiniFuturesTickCalculator.getInstance());
        derivPriceTickFuncMap.put(ProdType.K200MiniCallOption, Kospi200MiniOptionTickCalculator.getInstance());
        derivPriceTickFuncMap.put(ProdType.K200MiniPutOption, Kospi200MiniOptionTickCalculator.getInstance());

        derivPriceTickFuncMap.put(ProdType.KQ150Futures, Kosdaq150FuturesTickCalculator.getInstance());
        derivPriceTickFuncMap.put(ProdType.KQ150FuturesSpread, Kosdaq150FuturesTickCalculator.getInstance());
        derivPriceTickFuncMap.put(ProdType.KQ150CallOption, Kosdaq150OptionTickCalculator.getInstance());
        derivPriceTickFuncMap.put(ProdType.KQ150PutOption, Kosdaq150OptionTickCalculator.getInstance());


        derivPriceTickFuncMap.put(ProdType.KRX300Futures, Krx300FuturesTickCalculator.getInstance());
        derivPriceTickFuncMap.put(ProdType.KRX300FuturesSpread, Krx300FuturesTickCalculator.getInstance());

        derivPriceTickFuncMap.put(ProdType.SectorConstructFutures, SectorFuturesTickCalculator.getInstance());
        derivPriceTickFuncMap.put(ProdType.SectorConstructFuturesSpread, SectorFuturesTickCalculator.getInstance());
        derivPriceTickFuncMap.put(ProdType.SectorHEAVYINDFutures, SectorFuturesTickCalculator.getInstance());
        derivPriceTickFuncMap.put(ProdType.SectorHEAVYINDFuturesSpread, SectorFuturesTickCalculator.getInstance());
        derivPriceTickFuncMap.put(ProdType.SectorITFutures, SectorFuturesTickCalculator.getInstance());
        derivPriceTickFuncMap.put(ProdType.SectorITFuturesSpread, SectorFuturesTickCalculator.getInstance());
        derivPriceTickFuncMap.put(ProdType.SectorHEALTHFutures, SectorFuturesTickCalculator.getInstance());
        derivPriceTickFuncMap.put(ProdType.SectorHEALTHFuturesSpread, SectorFuturesTickCalculator.getInstance());
        derivPriceTickFuncMap.put(ProdType.SectorChemistryFutures, SectorFuturesTickCalculator.getInstance());
        derivPriceTickFuncMap.put(ProdType.SectorChemistryFuturesSpread, SectorFuturesTickCalculator.getInstance());
        derivPriceTickFuncMap.put(ProdType.SectorFinanceFutures, SectorFuturesTickCalculator.getInstance());
        derivPriceTickFuncMap.put(ProdType.SectorFinanceFuturesSpread, SectorFuturesTickCalculator.getInstance());
        derivPriceTickFuncMap.put(ProdType.SectorConsuDiscretFutures, SectorFuturesTickCalculator.getInstance());
        derivPriceTickFuncMap.put(ProdType.SectorConsuDiscretFuturesSpread, SectorFuturesTickCalculator.getInstance());
        derivPriceTickFuncMap.put(ProdType.SectorSteelFutures, SectorFuturesTickCalculator.getInstance());
        derivPriceTickFuncMap.put(ProdType.SectorSteelFuturesSpread, SectorFuturesTickCalculator.getInstance());
        derivPriceTickFuncMap.put(ProdType.SectorConsuStapleFutures, SectorFuturesTickCalculator.getInstance());
        derivPriceTickFuncMap.put(ProdType.SectorConsuStapleFuturesSpread, SectorFuturesTickCalculator.getInstance());
        derivPriceTickFuncMap.put(ProdType.SectorIndustrialFutures, SectorFuturesTickCalculator.getInstance());
        derivPriceTickFuncMap.put(ProdType.SectorIndustrialFuturesSpread, SectorFuturesTickCalculator.getInstance());

        derivPriceTickFuncMap.put(ProdType.SectorGrowthFutures, SectorFutures50TickCalculator.getInstance());
        derivPriceTickFuncMap.put(ProdType.SectorGrowthFuturesSpread, SectorFutures50TickCalculator.getInstance());
        derivPriceTickFuncMap.put(ProdType.SectorDividendFutures, SectorFutures50TickCalculator.getInstance());
        derivPriceTickFuncMap.put(ProdType.SectorDividendFuturesSpread, SectorFutures50TickCalculator.getInstance());

        derivPriceTickFuncMap.put(ProdType.SectorKNewDealBBIGFutures, SectorFutures50TickCalculator.getInstance());
        derivPriceTickFuncMap.put(ProdType.SectorKNewDealBBIGFuturesSpread, SectorFutures50TickCalculator.getInstance());
        derivPriceTickFuncMap.put(ProdType.SectorKNewDealBatteryFutures, SectorFutures50TickCalculator.getInstance());
        derivPriceTickFuncMap.put(ProdType.SectorKNewDealBatteryFuturesSpread, SectorFutures50TickCalculator.getInstance());
        derivPriceTickFuncMap.put(ProdType.SectorKNewDealBioFutures, SectorFutures50TickCalculator.getInstance());
        derivPriceTickFuncMap.put(ProdType.SectorKNewDealBioFuturesSpread, SectorFutures50TickCalculator.getInstance());

        derivPriceTickFuncMap.put(ProdType.BondFutures, BondFuturesTickCalculator.getInstance());
        derivPriceTickFuncMap.put(ProdType.BondFuturesSpread, BondFuturesTickCalculator.getInstance());

        derivPriceTickFuncMap.put(ProdType.FXFutures, FXFuturesTickCalculator.getInstance());
        derivPriceTickFuncMap.put(ProdType.FXFuturesSpread, FXFuturesTickCalculator.getInstance());

        derivPriceTickFuncMap.put(ProdType.VkospiFutures, VKospiFuturesTickCalculator.getInstance());
        derivPriceTickFuncMap.put(ProdType.VkospiFuturesSpread, VKospiFuturesTickCalculator.getInstance());

        //ETF 선물 구분해야하므로 미입력
        //derivProdToTickCalculatorMap.put(ProdType.KOSPIStockFutures, KospiStockFuturesTickCalculator.getInstance());
//        derivProdToPriceTickCalculatorMap.put(ProdType.KOSPIStockFuturesSpread, KospiStockFuturesTickCalculator.getInstance());
        derivPriceTickFuncMap.put(ProdType.KOSPIStockCallOptions, KospiKosdaqStockOptionsTickCalculator.getInstance());
        derivPriceTickFuncMap.put(ProdType.KOSPIStockPutOptions, KospiKosdaqStockOptionsTickCalculator.getInstance());

        derivPriceTickFuncMap.put(ProdType.KOSDAQStockFutures, KosdaqStockFuturesTickCalculator.getInstance());
        derivPriceTickFuncMap.put(ProdType.KOSDAQStockFuturesSpread, KosdaqStockFuturesTickCalculator.getInstance());
        derivPriceTickFuncMap.put(ProdType.KOSDAQStockCallOptions, KospiKosdaqStockOptionsTickCalculator.getInstance());
        derivPriceTickFuncMap.put(ProdType.KOSDAQStockPutOptions, KospiKosdaqStockOptionsTickCalculator.getInstance());

        equityPriceTickFuncMap.put(ProdType.EquityKOSPI, KospiStockTickCalculator.getInstance());
        equityPriceTickFuncMap.put(ProdType.EquityMF, KospiStockTickCalculator.getInstance());
        equityPriceTickFuncMap.put(ProdType.EquitySC, KospiStockTickCalculator.getInstance());
        equityPriceTickFuncMap.put(ProdType.EquityReits, KospiStockTickCalculator.getInstance());
        equityPriceTickFuncMap.put(ProdType.EquityKOSDAQ, KosdaqStockTickCalculator.getInstance());
        equityPriceTickFuncMap.put(ProdType.EquityELW, ElwTickCalculator.getInstance());
        equityPriceTickFuncMap.put(ProdType.EquityETF, EtfTickCalculator.getInstance());

        //quote tick 입력
        derivQuoteTickFuncMap.putAll(derivPriceTickFuncMap);
        equityQuoteTickFuncMap.putAll(equityPriceTickFuncMap);
        derivQuoteTickFuncMap.put(ProdType.KQ150CallOption, WideKosdaq150OptionTickCalculator.getInstance());
        derivQuoteTickFuncMap.put(ProdType.KQ150PutOption, WideKosdaq150OptionTickCalculator.getInstance());

        //option strike tick 입력
        strikeTickFuncMap.put(ProdType.K200CallOption, Kospi200OptionStrikeTickCalculator.getInstance());
        strikeTickFuncMap.put(ProdType.K200PutOption, Kospi200OptionStrikeTickCalculator.getInstance());
        strikeTickFuncMap.put(ProdType.K200MiniCallOption, Kospi200OptionStrikeTickCalculator.getInstance());
        strikeTickFuncMap.put(ProdType.K200MiniPutOption, Kospi200OptionStrikeTickCalculator.getInstance());
        strikeTickFuncMap.put(ProdType.KQ150CallOption, Kosdaq150OptionStrikeTickCalculator.getInstance());
        strikeTickFuncMap.put(ProdType.KQ150PutOption, Kosdaq150OptionStrikeTickCalculator.getInstance());
        strikeTickFuncMap.put(ProdType.KOSPIStockCallOptions, KospiKosdaqStockOptionStrikeTickCalculator.getInstance());
        strikeTickFuncMap.put(ProdType.KOSPIStockPutOptions, KospiKosdaqStockOptionStrikeTickCalculator.getInstance());
        strikeTickFuncMap.put(ProdType.KOSDAQStockCallOptions, KospiKosdaqStockOptionStrikeTickCalculator.getInstance());
        strikeTickFuncMap.put(ProdType.KOSDAQStockPutOptions, KospiKosdaqStockOptionStrikeTickCalculator.getInstance());

        //init cache
        for (EquityInfo equityInfo : EquityInfoCenter.Instance.getAllUnexpiredEquityInfo()) {
            getPriceTickFunction(equityInfo.isinCode);
        }
        for (FuturesInfo fi : FuturesInfoCenter.Instance.getAllUnexpiredFuturesInfo()) {
            getPriceTickFunction(fi.isinCode);
        }
    }

    /**
     * ProdType에 해당되는 TickFunction 을 리턴한다. 사전에 정의된 ProdType에 맞는 TickFunction이 없을 경우 null을 리턴한다.
     *
     * @param pt ProdType. 틱 계산 방식이 주식선물과 ETF선물 모두 ProdType이 StockFutures로 분류되므로 아예 null을 리턴한다.
     * @return ProdType에 해당되는 TickFunction.
     */
    public TickFunction getPriceTickFunction(ProdType pt) throws NotSupportedProdTypeException {
        TickFunction tf;
        if (pt == ProdType.KOSPIStockFutures || pt == ProdType.KOSPIStockFuturesSpread)
            throw new NotSupportedProdTypeException(pt);
        if (pt.isEquity()) {
            return equityPriceTickFuncMap.get(pt);
        }
        return derivPriceTickFuncMap.get(pt);
    }

    public TickFunction getPriceTickFunction(String isinCode) {
        TickFunction tf = priceTickCache.get(isinCode);
        if (tf != null)
            return tf;
        ProdType pt = ProdTypeCenter.Instance.getProdType(isinCode);
        if (pt.isDerivative()) {
            TickFunction tf2 = derivPriceTickFuncMap.get(pt);
            if (tf2 != null) {
                priceTickCache.put(isinCode, tf2);
                return tf2;
            }
            //check sector fut
            if (pt.isSectorFut() || pt.isSectorFutSP()) {
                tf2 = SectorFuturesTickCalculator.getInstance();
                priceTickCache.put(isinCode, tf2);
                return tf2;
            }
            //check etf fut
            if (pt.isStockFutKOSPI()) {
                FuturesInfo fi = FuturesInfoCenter.Instance.getFuturesInfo(isinCode);
                if (fi != null) {
                    EquityInfo ei = EquityInfoCenter.Instance.getEquityInfo(fi.underlyingIsinCode);
                    if (ei != null) {
                        if (ProdTypeCenter.Instance.getProdType(ei.isinCode) == ProdType.EquityETF) {
                            tf2 = EtfFuturesTickCalculator.getInstance();
                            priceTickCache.put(isinCode, tf2);
                            return tf2;
                        }
                    }
                }
                tf2 = KospiStockFuturesTickCalculator.getInstance();
                priceTickCache.put(isinCode, tf2);
                return tf2;
            } else if (pt.isStockFutSPKOSPI()) {
                FuturesInfo fi = FuturesInfoCenter.Instance.getFuturesInfo(isinCode);
                String recent = fi.spreadRecentIsin;
                String next = fi.spreadNext;
                if (recent == null || next == null)
                    return UnknownFixedTickCalculator.getInstance();
                FuturesInfo recentFi = FuturesInfoCenter.Instance.getFuturesInfo(recent);
                FuturesInfo nextFi = FuturesInfoCenter.Instance.getFuturesInfo(next);
                if (recentFi == null || nextFi == null)
                    return UnknownFixedTickCalculator.getInstance();
                int recentTick = KospiStockFuturesTickCalculator.getInstance().getTickSize(UpDown.UP, (int) (recentFi.기준가 * recentFi.priceDivider));
                int nextTick = KospiStockFuturesTickCalculator.getInstance().getTickSize(UpDown.UP, (int) (recentFi.기준가 * nextFi.priceDivider));
                int tick = Math.min(recentTick, nextTick);
                tf2 = new KospiStockFuturesSpreadTickCalculator(tick, true);
                priceTickCache.put(isinCode, tf2);
                return tf2;
            }
            tf2 = UnknownFixedTickCalculator.getInstance();
            priceTickCache.put(isinCode, tf2);
            return tf2;
        }
        //equity
        TickFunction tf2 = equityPriceTickFuncMap.get(pt);
        if (tf2 != null) {
            priceTickCache.put(isinCode, tf2);
            return tf2;
        }
        tf2 = UnknownFixedTickCalculator.getInstance();
        priceTickCache.put(isinCode, tf2);
        return tf2;
    }

    public TickFunction getQuoteTickFunction(ProdType pt) throws NotSupportedProdTypeException {
        if (pt.isEquity()) {
            return equityQuoteTickFuncMap.get(pt);
        }
        if (pt == ProdType.KOSPIStockFutures || pt == ProdType.KOSPIStockFuturesSpread)
            throw new NotSupportedProdTypeException(pt);
        return derivQuoteTickFuncMap.get(pt);
    }

    public TickFunction getQuoteTickFunction(String isinCode) {
        TickFunction tf = quoteTickCache.get(isinCode);
        if (tf != null)
            return tf;
        ProdType pt = ProdTypeCenter.Instance.getProdType(isinCode);
        if (pt.isDerivative()) {
            TickFunction tf2 = derivQuoteTickFuncMap.get(pt);
            if (tf2 != null) {
                quoteTickCache.put(isinCode, tf2);
                return tf2;
            }
            //check sector fut
            if (pt.isSectorFut() || pt.isSectorFutSP()) {
                tf2 = SectorFuturesTickCalculator.getInstance();
                quoteTickCache.put(isinCode, tf2);
                return tf2;
            }
            //check etf fut
            if (pt.isStockFutKOSPI()) {
                FuturesInfo fi = FuturesInfoCenter.Instance.getFuturesInfo(isinCode);
                if (fi != null) {
                    EquityInfo ei = EquityInfoCenter.Instance.getEquityInfo(fi.underlyingIsinCode);
                    if (ei != null) {
                        if (ProdTypeCenter.Instance.getProdType(ei.isinCode) == ProdType.EquityETF) {
                            tf2 = EtfFuturesTickCalculator.getInstance();
                            quoteTickCache.put(isinCode, tf2);
                            return tf2;
                        }
                    }
                }
                tf2 = KospiStockFuturesTickCalculator.getInstance();
                quoteTickCache.put(isinCode, tf2);
                return tf2;
            }
            tf2 = UnknownFixedTickCalculator.getInstance();
            quoteTickCache.put(isinCode, tf2);
            return tf2;
        }
        //equity
        TickFunction tf2 = equityQuoteTickFuncMap.get(pt);
        if (tf2 != null) {
            quoteTickCache.put(isinCode, tf2);
            return tf2;
        }
        tf2 = UnknownFixedTickCalculator.getInstance();
        quoteTickCache.put(isinCode, tf2);
        return tf2;
    }

    public TickFunction getStrikeTickFunction(ProdType pt) {
        return strikeTickFuncMap.get(pt);
    }

    public TickFunction getStrikeTickFunction(String isinCode) {
        TickFunction tf = strikeTickCache.get(isinCode);
        if (tf != null)
            return tf;
        ProdType pt = ProdTypeCenter.Instance.getProdType(isinCode);
        TickFunction tf2 = strikeTickFuncMap.get(pt);
        if (tf2 != null) {
            strikeTickCache.put(isinCode, tf2);
            return tf2;
        }
        tf2 = UnknownFixedTickCalculator.getInstance();
        strikeTickCache.put(isinCode, tf2);
        return tf2;
    }
}
