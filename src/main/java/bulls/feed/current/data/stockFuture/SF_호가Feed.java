package bulls.feed.current.data.stockFuture;

import bulls.data.bidAsk.BidAskCore;
import bulls.data.bidAsk.BidAskFactory;
import bulls.db.influxdb.InfluxDBData;
import bulls.feed.abstraction.BidAskFillUpdater;
import bulls.feed.abstraction.Feed;
import bulls.feed.abstraction.호가Feed;
import bulls.feed.current.enums.FeedTRCode;
import bulls.feed.current.parser.stockFuture.SF_호가;

public class SF_호가Feed extends Feed implements 호가Feed, InfluxDBData {
    static int bidAskLength = SF_호가.bidPrice.parser().getLength() + SF_호가.bidSign.parser().getLength() + SF_호가.bidAmount.parser().getLength();

    public SF_호가Feed(FeedTRCode trCode, byte[] packet) {
        super(trCode, packet);
    }

    @Override
    public void updateBidAskFill(BidAskFillUpdater dc) {
        BidAskCore ba = getFullBidAsk(dc.getBidAskFactory());
        int expPrice = SF_호가.expectedPrice.parser().parseInt(rawPacket);
        if (expPrice != 0)
            ba = dc.getBidAskFactory().ofAmtOne(ba.isinCode, expPrice, expPrice);
        ba.feedStamp = arrivalStamp;
        dc.updateDerivBidAskMap(ba.isinCode, ba);
    }

    public byte[] getCodeByte() {
        return SF_호가.isinCode.parser().parseByte(rawPacket);
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
        return SF_호가.boardId.parser().parseStr(rawPacket, "");
    }

    public Integer getAskPrice() {
        return SF_호가.askPrice.parser().parseIntWithLeadingSign(rawPacket);
    }

    public Integer getAskAmount() {
        return SF_호가.askAmount.parser().parseInt(rawPacket);
    }

    public Integer getBidPrice() {
        return SF_호가.bidPrice.parser().parseIntWithLeadingSign(rawPacket);
    }

    public Integer getBidAmount() {
        return SF_호가.bidAmount.parser().parseInt(rawPacket);
    }


    public BidAskCore getFullBidAsk(BidAskFactory factory) {
//        9	매수1단계부호	X	1
//        10	매수1단계우선호가가격	9	7
//        11	매수1단계우선호가잔량	9	6
//        1 + 7 + 6 = 14
        int size = bidAskLength;
        BidAskCore fullBidAsk = factory.getBidAsk(DERIV_STOCK_MAX_BIDASK_DEPTH);
        fullBidAsk.isinCode = getCode();
        for (int i = 0; i < DERIV_STOCK_MAX_BIDASK_DEPTH; i++) {
            fullBidAsk.askAmount[i] = SF_호가.askAmount.plus(i * size).parseInt(rawPacket);
            if (fullBidAsk.askAmount[i] == 0)
                break;
            fullBidAsk.askPrice[i] = SF_호가.askPrice.plus(i * size).parseIntWithLeadingSign(rawPacket);
        }
        for (int i = 0; i < DERIV_STOCK_MAX_BIDASK_DEPTH; i++) {
            fullBidAsk.bidAmount[i] = SF_호가.bidAmount.plus(i * size).parseInt(rawPacket);
            if (fullBidAsk.bidAmount[i] == 0)
                break;
            fullBidAsk.bidPrice[i] = SF_호가.bidPrice.plus(i * size).parseIntWithLeadingSign(rawPacket);
        }
        fullBidAsk.updateBidAskState();
        return fullBidAsk;
    }

    @Override
    public long getArrivalStamp() {
        return arrivalStamp;
    }

}
