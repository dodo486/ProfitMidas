package bulls.feed.dc;

import bulls.data.PriceInfo;
import bulls.data.bidAsk.BidAskCore;
import bulls.data.bidAsk.BidAskFactory;
import bulls.data.bidAsk.NativeBidAskGenerator;
import bulls.designTemplate.observer.ObserverStation;
import bulls.feed.abstraction.BidAskFillUpdater;
import bulls.feed.abstraction.Feed;
import bulls.feed.current.enums.FeedTRCode;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public enum PrimitiveDC implements BidAskFillUpdater {
    Instance;

    private final CodeEventHashMap<BidAskCore> bidAskMap;
    private final CodeEventHashMap<PriceInfo> priceInfoMap;
    private final CodeEventHashMap<Double> navMap;

    private Set<FeedTRCode> trListToAccept;

    private final String dcName = "PrimitiveDC";


    PrimitiveDC() {
        bidAskMap = new CodeEventHashMap<>();
        priceInfoMap = new CodeEventHashMap<>();
        navMap = new CodeEventHashMap<>();
    }

    public void setFeedObserver(ObserverStation<Feed> obStation, Set<FeedTRCode> feedTRCodeSet) {
        trListToAccept = feedTRCodeSet;
        obStation.addObserver(dcName, this::onUpdateBidAskFill, feed -> true);
    }

    public void onUpdateBidAskFill(Feed data) throws ClassCastException {
        data.updateBidAskFill(this);
    }

    @Override
    public @NotNull BidAskFactory getBidAskFactory() {
        return NativeBidAskGenerator.Instance;
    }

    @Override
    public void updatePriceInfo(PriceInfo info) {
        priceInfoMap.put(info.isinCode, info);
    }

    private void updateBidAskMap(String code, BidAskCore bidAsk) {
        bidAskMap.put(code, bidAsk);
    }

    @Override
    public void updateDerivBidAskMap(String code, BidAskCore newDerivBA) {
        updateBidAskMap(code, newDerivBA);
    }

    @Override
    public void updateEquityBidAskMap(String code, BidAskCore newEquityBA) {
        updateBidAskMap(code, newEquityBA);
    }

    @Override
    public boolean isOutDatedAccVolume(String code, long accVolume) {
        return false;
    }

    @Override
    public void handleEquityContract(PriceInfo info) {
        // do nothing
    }

    @Override
    public void handleDerivContract(PriceInfo info) {
        // do nothing
    }

    @Override
    public void updateNav(String etfCode, Double nav) {
        navMap.put(etfCode, nav);
    }

    public void monitorPriceOf(String code, String obName, CodeObserver<PriceInfo> observer) {
        priceInfoMap.addObserver(code, observer, obName);
    }

    public void stopMonitorPriceOf(String code, String obName) {
        priceInfoMap.deleteObserver(code, obName);
    }

    public void monitorBidAskOf(String code, String obName, CodeObserver<BidAskCore> observer) {
        bidAskMap.addObserver(code, observer, obName);
    }

    public void stopMonitorBidAskOf(String code, String obName) {
        bidAskMap.deleteObserver(code, obName);
    }

    public void monitorNavOf(String code, String obName, CodeObserver<Double> observer) {
        navMap.addObserver(code, observer, obName);
    }

    public void stopMonitorNavOf(String code, String obName) {
        navMap.deleteObserver(code, obName);
    }

    public BidAskCore getBidAsk(String code) {
        return bidAskMap.get(code);
    }

    public PriceInfo getPriceInfo(String code) {
        return priceInfoMap.get(code);
    }

    public Double getNav(String code) {
        return navMap.get(code);
    }
}
