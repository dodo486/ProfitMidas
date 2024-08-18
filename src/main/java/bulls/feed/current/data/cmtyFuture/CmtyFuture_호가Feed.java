package bulls.feed.current.data.cmtyFuture;

import bulls.data.bidAsk.BidAskCore;
import bulls.data.bidAsk.BidAskFactory;
import bulls.db.influxdb.InfluxDBData;
import bulls.feed.abstraction.BidAskFillUpdater;
import bulls.feed.abstraction.Feed;
import bulls.feed.abstraction.FeedPrinter;
import bulls.feed.abstraction.호가Feed;
import bulls.feed.current.enums.FeedTRCode;
import bulls.feed.current.parser.cmtyFuture.CmtyFuture_호가;
import bulls.log.DefaultLogger;

public class CmtyFuture_호가Feed extends Feed implements 호가Feed, FeedPrinter, InfluxDBData {
    public CmtyFuture_호가Feed(FeedTRCode trCode, byte[] packet) {
        super(trCode, packet);
    }

    @Override
    public void updateBidAskFill(BidAskFillUpdater dc) {
        BidAskCore ba = getFullBidAsk(dc.getBidAskFactory());

        int expPrice = CmtyFuture_호가.expectedPrice.parser().parseInt(rawPacket);
        // sector 선물같이 한가한 종목은 장중에 호가가 없는 경우도 있다. 이 경우 예상체결가도 0 이므로 반영되어서는 안된다.
        if (expPrice != 0)
            ba = dc.getBidAskFactory().ofAmtOne(ba.isinCode, expPrice, expPrice);

        ba.feedStamp = arrivalStamp;
        dc.updateDerivBidAskMap(ba.isinCode, ba);
    }

    public String getCode() {
        return CmtyFuture_호가.isinCode.parser().parseStr(rawPacket, "");
    }

    @Override
    public String getRepresentingCode() {
        return getCode();
    }

    @Override
    public String getBoardId() {
        return CmtyFuture_호가.boardId.parser().parseStr(rawPacket, "");
    }

    @Override
    public BidAskCore getFullBidAsk(BidAskFactory factory) {
        int size = 16;
        BidAskCore fullBidAsk = factory.getBidAsk(DERIV_MAX_BIDASK_DEPTH);
        fullBidAsk.isinCode = getCode();
        for (int i = 0; i < DERIV_MAX_BIDASK_DEPTH; i++) {
            fullBidAsk.askAmount[i] = CmtyFuture_호가.askAmount.plus(i * size).parseInt(rawPacket);
            if (fullBidAsk.askAmount[i] == 0)
                break;
            fullBidAsk.askPrice[i] = CmtyFuture_호가.askPrice.plus(i * size).parseIntWithLeadingSign(rawPacket);
        }
        for (int i = 0; i < DERIV_MAX_BIDASK_DEPTH; i++) {
            fullBidAsk.bidAmount[i] = CmtyFuture_호가.bidAmount.plus(i * size).parseInt(rawPacket);
            if (fullBidAsk.bidAmount[i] == 0)
                break;
            fullBidAsk.bidPrice[i] = CmtyFuture_호가.bidPrice.plus(i * size).parseIntWithLeadingSign(rawPacket);
        }
        fullBidAsk.updateBidAskState();
        return fullBidAsk;
    }

    @Override
    public long getArrivalStamp() {
        return arrivalStamp;
    }

    public Integer getAskPrice() {
        return CmtyFuture_호가.askPrice.parser().parseIntWithLeadingSign(rawPacket);
    }

    public Integer getAskAmount() {
        return CmtyFuture_호가.askAmount.parser().parseInt(rawPacket);
    }

    public Integer getBidPrice() {
        return CmtyFuture_호가.bidPrice.parser().parseIntWithLeadingSign(rawPacket);
    }

    public Integer getBidAmount() {
        return CmtyFuture_호가.bidAmount.parser().parseInt(rawPacket);
    }

    @Override
    public void print() {
        DefaultLogger.logger.debug("{} : [{}]", new String(rawPacket).length(), new String(rawPacket));
        DefaultLogger.logger.debug("선물호가 - 종목: {}, bid: {}/{} , ask {}/{}", getCode(), getBidPrice(), getBidAmount(), getAskPrice(), getAskAmount());
    }

}
