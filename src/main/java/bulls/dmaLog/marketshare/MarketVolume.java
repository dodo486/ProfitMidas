package bulls.dmaLog.marketshare;

import bulls.order.enums.LongShort;

public class MarketVolume {
    private long bidVolume;
    private long askVolume;

    public void add(LongShort longShort, long quantity) {
        if (longShort == LongShort.SHORT)
            askVolume += quantity;
        else
            bidVolume += quantity;
    }

    public void addBidQuantity(long bidQuantity) {
        this.bidVolume += bidQuantity;
    }

    public void addAskQuantity(long askQuantity) {
        this.askVolume += askQuantity;
    }

    public long getBidVolume() {
        return bidVolume;
    }

    public long getAskVolume() {
        return askVolume;
    }

    public long getTotal() {
        return askVolume + bidVolume;
    }
}