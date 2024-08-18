package bulls.feed.current.data.krx300Future;

import bulls.data.bidAsk.BidAskCore;
import bulls.data.bidAsk.BidAskFactory;
import bulls.db.influxdb.InfluxDBData;
import bulls.feed.abstraction.BidAskFillUpdater;
import bulls.feed.abstraction.Feed;
import bulls.feed.abstraction.호가Feed;
import bulls.feed.current.enums.FeedTRCode;
import bulls.feed.current.parser.krx300Future.KRX300_호가;

public class KRX300Futures_호가Feed extends Feed implements 호가Feed, InfluxDBData {

    public KRX300Futures_호가Feed(FeedTRCode trCode, byte[] packet) {
        super(trCode, packet);
    }

    @Override
    public void updateBidAskFill(BidAskFillUpdater dc) {
        String code = getCode();
        BidAskCore ba = getFullBidAsk(dc.getBidAskFactory());

        int expPrice = KRX300_호가.expectedPrice.parser().parseInt(rawPacket);
        if (expPrice != 0) {
            ba = dc.getBidAskFactory().ofAmtOne(ba.isinCode, expPrice, expPrice);
        }
        ba.feedStamp = arrivalStamp;
        dc.updateDerivBidAskMap(code, ba);
    }

    public byte[] getCodeByte() {
        return KRX300_호가.isinCode.parser().parseByte(rawPacket);
    }

    public String getCode() {

        return new String(getCodeByte());
    }

    @Override
    public String getRepresentingCode() {
        return getCode();
    }

    @Override
    public String getBoardId() {
        return KRX300_호가.boardId.parser().parseStr(rawPacket, "");
    }

    public BidAskCore getFullBidAsk(BidAskFactory factory) {
        int size = 13;
        BidAskCore fullBidAsk = factory.getBidAsk(DERIV_MAX_BIDASK_DEPTH);
        fullBidAsk.isinCode = getCode();
        for (int i = 0; i < DERIV_MAX_BIDASK_DEPTH; i++) {
            fullBidAsk.askAmount[i] = KRX300_호가.askAmount.plus(i * size).parseInt(rawPacket);
            if (fullBidAsk.askAmount[i] == 0)
                break;
            fullBidAsk.askPrice[i] = KRX300_호가.askPrice.plus(i * size).parseIntWithLeadingSign(rawPacket);
        }
        for (int i = 0; i < DERIV_MAX_BIDASK_DEPTH; i++) {
            fullBidAsk.bidAmount[i] = KRX300_호가.bidAmount.plus(i * size).parseInt(rawPacket);
            if (fullBidAsk.bidAmount[i] == 0)
                break;
            fullBidAsk.bidPrice[i] = KRX300_호가.bidPrice.plus(i * size).parseIntWithLeadingSign(rawPacket);
        }
        fullBidAsk.updateBidAskState();
        return fullBidAsk;
    }

    @Override
    public long getArrivalStamp() {
        return arrivalStamp;
    }

    public Integer getAskPrice() {
        return KRX300_호가.askPrice.parser().parseIntWithLeadingSign(rawPacket);
    }

    public Integer getAskAmount() {
        return KRX300_호가.askAmount.parser().parseInt(rawPacket);
    }

    public Integer getBidPrice() {
        return KRX300_호가.bidPrice.parser().parseIntWithLeadingSign(rawPacket);
    }

    public Integer getBidAmount() {
        return KRX300_호가.bidAmount.parser().parseInt(rawPacket);
    }

}
