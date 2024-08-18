package bulls.data.bidAsk;

import bulls.db.mongodb.DocumentConvertible;

public class NativeBidAsk extends BidAskCore implements DocumentConvertible {
    NativeBidAsk() {
        bidPrice[0] = 0;
        askPrice[0] = 0;
        bidAmount[0] = 0;
        askAmount[0] = 0;
    }

    NativeBidAsk(String isinCode) {
        this.isinCode = isinCode;
        bidPrice[0] = 0;
        askPrice[0] = 0;
        bidAmount[0] = 0;
        askAmount[0] = 0;
    }

    NativeBidAsk(int maxBidAskDepth) {
        this.maxBidAskDepth = maxBidAskDepth;
        bidPrice[0] = 0;
        askPrice[0] = 0;
        bidAmount[0] = 0;
        askAmount[0] = 0;
    }

    NativeBidAsk(int maxBidAskDepth, String isinCode) {
        this.maxBidAskDepth = maxBidAskDepth;
        this.isinCode = isinCode;
        bidPrice[0] = 0;
        askPrice[0] = 0;
        bidAmount[0] = 0;
        askAmount[0] = 0;
    }


    NativeBidAsk(String isinCode, int bid, int ask) {
        this.isinCode = isinCode;
        bidPrice[0] = bid;
        askPrice[0] = ask;
        bidAmount[0] = 1;
        askAmount[0] = 1;

        updateBidAskState();
    }
}
