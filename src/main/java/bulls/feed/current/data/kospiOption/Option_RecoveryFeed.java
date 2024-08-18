package bulls.feed.current.data.kospiOption;

import bulls.data.bidAsk.BidAskCore;
import bulls.data.bidAsk.BidAskFactory;
import bulls.feed.abstraction.BidAskFillUpdater;
import bulls.feed.abstraction.Feed;
import bulls.feed.current.enums.FeedTRCode;
import bulls.feed.current.parser.kospiOption.Option_Recovery;

public class Option_RecoveryFeed extends Feed {
    static int bidAskLength = Option_Recovery.bidPrice.parser().getLength() + Option_Recovery.bidAmount.parser().getLength() + Option_Recovery.bidOrderCount.parser().getLength()
            + Option_Recovery.askPrice.parser().getLength() + Option_Recovery.askAmount.parser().getLength() + Option_Recovery.askOrderCount.parser().getLength();

    public Option_RecoveryFeed(FeedTRCode trCode, byte[] packet) {
        super(trCode, packet);
    }

    @Override
    public void updateBidAskFill(BidAskFillUpdater dc) {
        BidAskCore ba = getFullBidAsk(dc.getBidAskFactory());
        dc.updateDerivBidAskMap(ba.isinCode, ba);
    }

    public byte[] getCodeByte() {
        return Option_Recovery.isinCode.parser().parseByte(rawPacket);
    }

    public String getCode() {

        return new String(getCodeByte());
    }

    public Integer getAskPrice() {
        return Option_Recovery.askPrice.parser().parseInt(rawPacket);
    }

    public Integer getAskAmount() {
        return Option_Recovery.askAmount.parser().parseInt(rawPacket);
    }

    public Integer getBidPrice() {
        return Option_Recovery.bidPrice.parser().parseInt(rawPacket);
    }

    public Integer getBidAmount() {
        return Option_Recovery.bidAmount.parser().parseInt(rawPacket);
    }

    public BidAskCore getFullBidAsk(BidAskFactory factory) {
//        매수1단계부호	X	1
//        매수1단계우선호가가격	9	5
//        매수1단계우선호가잔량	9	6
//        1 + 5 + 6 = 12
        int size = bidAskLength;
        BidAskCore fullBidAsk = factory.getBidAsk(DERIV_MAX_BIDASK_DEPTH);
        fullBidAsk.isinCode = getCode();
        for (int i = 0; i < DERIV_MAX_BIDASK_DEPTH; i++) {
            fullBidAsk.askAmount[i] = Option_Recovery.askAmount.plus(i * size).parseInt(rawPacket);
            if (fullBidAsk.askAmount[i] == 0)
                break;
            fullBidAsk.askPrice[i] = Option_Recovery.askPrice.plus(i * size).parseInt(rawPacket);
        }
        for (int i = 0; i < DERIV_MAX_BIDASK_DEPTH; i++) {
            fullBidAsk.bidAmount[i] = Option_Recovery.bidAmount.plus(i * size).parseInt(rawPacket);
            if (fullBidAsk.bidAmount[i] == 0)
                break;
            fullBidAsk.bidPrice[i] = Option_Recovery.bidPrice.plus(i * size).parseInt(rawPacket);
        }
        fullBidAsk.updateBidAskState();
        return fullBidAsk;
    }

}