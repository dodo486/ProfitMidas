package bulls.feed.dc.filter;

import bulls.data.PriceInfo;

public enum FreeEquityContractValidator implements EquityContractValidator {
    Instance;

    @Override
    public boolean isLateG1Contract(PriceInfo info, long accVolumeFromBidAsk) {
        return false;
    }
}
