package bulls.feed.abstraction;

import bulls.data.PriceInfo;
import bulls.data.bidAsk.BidAskCore;
import bulls.data.bidAsk.BidAskFactory;
import org.jetbrains.annotations.NotNull;

public interface BidAskFillUpdater {
    @NotNull
    BidAskFactory getBidAskFactory();

    void updatePriceInfo(PriceInfo info);

    void updateDerivBidAskMap(String code, BidAskCore newDerivBA);

    void updateEquityBidAskMap(String code, BidAskCore newEquityBA);

    boolean isOutDatedAccVolume(String code, long accVolume);

    void handleEquityContract(PriceInfo info);

    void handleDerivContract(PriceInfo info);

    void updateNav(String etfCode, Double nav);
}
