package bulls.feed.current.data.kospiFuture;

import bulls.data.bidAsk.BidAskCore;
import bulls.data.bidAsk.BidAskFactory;
import bulls.db.influxdb.InfluxDBData;
import bulls.feed.abstraction.BidAskFillUpdater;
import bulls.feed.abstraction.Feed;
import bulls.feed.abstraction.FeedPrinter;
import bulls.feed.abstraction.호가Feed;
import bulls.feed.current.enums.FeedTRCode;
import bulls.feed.current.parser.kospiFuture.Future_호가;
import bulls.log.DefaultLogger;

public class Future_호가Feed extends Feed implements 호가Feed, FeedPrinter, InfluxDBData {

    public Future_호가Feed(FeedTRCode trCode, byte[] packet) {
        super(trCode, packet);
    }

    @Override
    public void updateBidAskFill(BidAskFillUpdater dc) {
        BidAskCore ba = getFullBidAsk(dc.getBidAskFactory());
        int expPrice = Future_호가.expectedPrice.parser().parseInt(rawPacket);
        if (expPrice != 0)
            ba = dc.getBidAskFactory().ofAmtOne(ba.isinCode, expPrice, expPrice);
        ba.feedStamp = arrivalStamp;
        dc.updateDerivBidAskMap(ba.isinCode, ba);

//        CancelCounterCenter.Instance.updateBidAsk(ba.isinCode, ba);
    }


    public String getCode() {
        return Future_호가.isinCode.parser().parseStr(rawPacket, "");
    }

    @Override
    public String getRepresentingCode() {
        return getCode();
    }

    @Override
    public String getBoardId() {
        return Future_호가.boardId.parser().parseStr(rawPacket, "");
    }

    public BidAskCore getFullBidAsk(BidAskFactory factory) {

        int size = 12;
        BidAskCore fullBidAsk = factory.getBidAsk(DERIV_MAX_BIDASK_DEPTH);
        fullBidAsk.isinCode = getCode();
        for (int i = 0; i < DERIV_MAX_BIDASK_DEPTH; i++) {
            fullBidAsk.askAmount[i] = Future_호가.askAmount.plus(i * size).parseInt(rawPacket);
            if (fullBidAsk.askAmount[i] == 0)
                break;
            fullBidAsk.askPrice[i] = Future_호가.askPrice.plus(i * size).parseIntWithLeadingSign(rawPacket);
        }
        for (int i = 0; i < DERIV_MAX_BIDASK_DEPTH; i++) {
            fullBidAsk.bidAmount[i] = Future_호가.bidAmount.plus(i * size).parseInt(rawPacket);
            if (fullBidAsk.bidAmount[i] == 0)
                break;
            fullBidAsk.bidPrice[i] = Future_호가.bidPrice.plus(i * size).parseIntWithLeadingSign(rawPacket);
        }
        fullBidAsk.updateBidAskState();
        return fullBidAsk;
    }

    @Override
    public long getArrivalStamp() {
        return arrivalStamp;
    }

    public Integer getAskPrice() {
        return Future_호가.askPrice.parser().parseIntWithLeadingSign(rawPacket);
    }

    public Integer getAskAmount() {
        return Future_호가.askAmount.parser().parseInt(rawPacket);
    }

    public Integer getBidPrice() {
        return Future_호가.bidPrice.parser().parseIntWithLeadingSign(rawPacket);
    }

    public Integer getBidAmount() {
        return Future_호가.bidAmount.parser().parseInt(rawPacket);
    }

    @Override
    public void print() {
        DefaultLogger.logger.debug("{} : [{}]", new String(rawPacket).length(), new String(rawPacket));
        DefaultLogger.logger.debug("선물호가 - 종목: {}, bid: {}/{} , ask {}/{}", getCode(), getBidPrice(), getBidAmount(), getAskPrice(), getAskAmount());
    }

}
