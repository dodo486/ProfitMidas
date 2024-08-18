package bulls.data.bidAsk;

public interface BidAskFactory {

    Class<? extends BidAskCore> getBidAskClass();

    BidAskCore getBidAsk();

    BidAskCore getBidAsk(String isinCode);


    BidAskCore getBidAsk(int maxBidAskDepth);

    BidAskCore getBidAsk(int maxBidAskDepth, String isinCode);

//    BidAskCore getBidAsk(String isinCode, int price);
//    BidAskCore getBidAsk(int maxBidAskDepth, String isinCode, int price);

    BidAskCore ofAmtOne(String isinCode, int bid, int ask);
}
