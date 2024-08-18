package bulls.data.bidAsk;

public enum NativeBidAskGenerator implements BidAskFactory {
    Instance;

    @Override
    public Class<? extends BidAskCore> getBidAskClass() {
        return NativeBidAsk.class;
    }

    public BidAskCore getBidAsk() {
        return new NativeBidAsk();
    }

    @Override
    public BidAskCore getBidAsk(String isinCode) {
        return new NativeBidAsk(isinCode);
    }


    @Override
    public BidAskCore getBidAsk(int maxBidAskDepth) {
        return new NativeBidAsk(maxBidAskDepth);
    }

    @Override
    public BidAskCore getBidAsk(int maxBidAskDepth, String isinCode) {
        return new NativeBidAsk(maxBidAskDepth, isinCode);
    }

    @Override
    public BidAskCore ofAmtOne(String isinCode, int bid, int ask) {
        return new NativeBidAsk(isinCode, bid, ask);
    }
}
