package bulls.feed.abstraction;

import bulls.data.bidAsk.BidAskCore;
import bulls.data.bidAsk.BidAskFactory;

public interface 호가Feed {

    String getRepresentingCode();

    String getBoardId();

    BidAskCore getFullBidAsk(BidAskFactory factory);

    long getArrivalStamp();
}
