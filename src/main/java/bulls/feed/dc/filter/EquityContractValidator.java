package bulls.feed.dc.filter;

import bulls.data.PriceInfo;

public interface EquityContractValidator {
    boolean isLateG1Contract(PriceInfo info, long accVolumeFromBidAsk);
}