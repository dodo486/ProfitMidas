package bulls.dmaLog.marketshare;

import bulls.designTemplate.observer.Observer;
import bulls.dmaLog.DMALogList;
import bulls.dmaLog.TradeDMALog;
import bulls.order.CodeAndBook;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public enum MarketShareCenter implements Observer<DMALogList> {
    Instance;

    private final ConcurrentHashMap<String, List<TradeDMALog>> isinCodeTradeLogMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, List<TradeDMALog>> bookCodeTradeLogMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, List<TradeDMALog>> accountTradeLogMap = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<String, MarketTradingValueShare> accountMarketTradingValueShareMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, MarketTradingValueShare> bookCodeMarketTradingValueShareMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<CodeAndBook, MarketVolumeShare> marketVolumeShareMap = new ConcurrentHashMap<>();

    @Override
    public void update(DMALogList data) {
        for (var log : data.getLogList()) {
            if (log instanceof TradeDMALog) {
                TradeDMALog tradeDMALog = (TradeDMALog) log;
                isinCodeTradeLogMap.computeIfAbsent(log.getIsinCode(), k -> new ArrayList<>()).add(tradeDMALog);
                bookCodeTradeLogMap.computeIfAbsent(log.getBookCode(), k -> new ArrayList<>()).add(tradeDMALog);
                accountTradeLogMap.computeIfAbsent(log.getAccountNumber(), k -> new ArrayList<>()).add(tradeDMALog);

                accountMarketTradingValueShareMap.computeIfAbsent(log.getAccountNumber(),
                        k -> new MarketTradingValueShare("account", k)).update(tradeDMALog);
                bookCodeMarketTradingValueShareMap.computeIfAbsent(log.getBookCode(),
                        k -> new MarketTradingValueShare("bookCode", k)).update(tradeDMALog);
                marketVolumeShareMap.computeIfAbsent(log.getCodeAndBook(),
                        MarketVolumeShare::new).update(tradeDMALog);
            }
        }
    }

    @NotNull
    public ConcurrentHashMap<String, MarketTradingValueShare> getMarketTradingValueShareMap(String keyName) {
        if (keyName.equals("account"))
            return accountMarketTradingValueShareMap;
        else if (keyName.equals("bookCode"))
            return bookCodeMarketTradingValueShareMap;

        return new ConcurrentHashMap<>();
    }

    public ConcurrentHashMap<String, MarketTradingValueShare> getAccountMarketTradingValueShareMap() {
        return accountMarketTradingValueShareMap;
    }

    public ConcurrentHashMap<String, MarketTradingValueShare> getBookCodeMarketTradingValueShareMap() {
        return bookCodeMarketTradingValueShareMap;
    }

    public ConcurrentHashMap<CodeAndBook, MarketVolumeShare> getMarketVolumeShareMap() {
        return marketVolumeShareMap;
    }
}
