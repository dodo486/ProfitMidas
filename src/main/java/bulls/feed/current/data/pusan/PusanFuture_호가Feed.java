package bulls.feed.current.data.pusan;

import bulls.data.bidAsk.BidAskCore;
import bulls.data.bidAsk.BidAskFactory;
import bulls.feed.abstraction.BidAskFillUpdater;
import bulls.feed.abstraction.Feed;
import bulls.feed.abstraction.FeedPrinter;
import bulls.feed.current.enums.FeedTRCode;
import bulls.feed.current.parser.pusan.PusanFuture_호가;
import bulls.log.DefaultLogger;

public class PusanFuture_호가Feed extends Feed implements FeedPrinter {

    public PusanFuture_호가Feed(FeedTRCode trCode, byte[] packet) {
        super(trCode, packet);
    }

    @Override
    public void updateBidAskFill(BidAskFillUpdater dc) {
        BidAskCore ba = getFullBidAsk(dc.getBidAskFactory());
        ba.feedStamp = arrivalStamp;
        dc.updateDerivBidAskMap(ba.isinCode, ba);
    }

    public byte[] getCodeByte() {
        return PusanFuture_호가.isinCode.parser().parseByte(rawPacket);
    }

    public String getCode() {
        return new String(getCodeByte());
    }

    public BidAskCore getFullBidAsk(BidAskFactory factory) {
        int size = 12;
        BidAskCore fullBidAsk = factory.getBidAsk(DERIV_MAX_BIDASK_DEPTH);
        fullBidAsk.isinCode = getCode();
        for (int i = 0; i < DERIV_MAX_BIDASK_DEPTH; i++) {
            fullBidAsk.askAmount[i] = PusanFuture_호가.askAmount.plus(i * size).parseInt(rawPacket);
            if (fullBidAsk.askAmount[i] == 0)
                break;
            fullBidAsk.askPrice[i] = PusanFuture_호가.askPrice.plus(i * size).parseIntWithLeadingSign(rawPacket);
        }
        for (int i = 0; i < DERIV_MAX_BIDASK_DEPTH; i++) {
            fullBidAsk.bidAmount[i] = PusanFuture_호가.bidAmount.plus(i * size).parseInt(rawPacket);
            if (fullBidAsk.bidAmount[i] == 0)
                break;
            fullBidAsk.bidPrice[i] = PusanFuture_호가.bidPrice.plus(i * size).parseIntWithLeadingSign(rawPacket);
        }
        fullBidAsk.updateBidAskState();
        return fullBidAsk;
    }

    public Integer getAskPrice() {
        return PusanFuture_호가.askPrice.parser().parseIntWithLeadingSign(rawPacket);
    }

    public Integer getAskAmount() {
        return PusanFuture_호가.askAmount.parser().parseInt(rawPacket);
    }

    public Integer getBidPrice() {
        return PusanFuture_호가.bidPrice.parser().parseIntWithLeadingSign(rawPacket);
    }

    public Integer getBidAmount() {
        return PusanFuture_호가.bidAmount.parser().parseInt(rawPacket);
    }

    @Override
    public void print() {
        DefaultLogger.logger.debug("{} : [{}]", new String(rawPacket).length(), new String(rawPacket));
        DefaultLogger.logger.debug("선물호가 - 종목: {}, bid: {}/{} , ask {}/{}", getCode(), getBidPrice(), getBidAmount(), getAskPrice(), getAskAmount());
    }
}
