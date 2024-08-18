package bulls.feed.current.data.EuroStoxx50;

import bulls.data.bidAsk.BidAskCore;
import bulls.data.bidAsk.BidAskFactory;
import bulls.feed.abstraction.BidAskFillUpdater;
import bulls.feed.abstraction.Feed;
import bulls.feed.abstraction.호가Feed;
import bulls.feed.current.enums.FeedTRCode;
import bulls.feed.current.parser.euroStoxx50.EuroStoxx50_호가;

public class EuroStoxx50_호가Feed extends Feed implements 호가Feed {

    public EuroStoxx50_호가Feed(FeedTRCode trCode, byte[] packet) {
        super(trCode, packet);
    }

    @Override
    public void updateBidAskFill(BidAskFillUpdater dc) {
        BidAskCore ba = getFullBidAsk(dc.getBidAskFactory());
        dc.updateDerivBidAskMap(ba.isinCode, ba);
    }

    public byte[] getCodeByte() {
        return EuroStoxx50_호가.isinCode.parser().parseByte(rawPacket);
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
        return EuroStoxx50_호가.boardId.parser().parseStr(rawPacket, "");
    }

    public BidAskCore getFullBidAsk(BidAskFactory factory) {
        int size = 13;
        BidAskCore fullBidAsk = factory.getBidAsk(DERIV_MAX_BIDASK_DEPTH);
        fullBidAsk.isinCode = getCode();
        for (int i = 0; i < 5; i++) {
            fullBidAsk.askAmount[i] = EuroStoxx50_호가.askAmount.plus(i * size).parseInt(rawPacket);
            if (fullBidAsk.askAmount[i] == 0)
                break;
            fullBidAsk.askPrice[i] = EuroStoxx50_호가.askPrice.plus(i * size).parseInt(rawPacket);
        }
        for (int i = 0; i < 5; i++) {
            fullBidAsk.bidAmount[i] = EuroStoxx50_호가.bidAmount.plus(i * size).parseInt(rawPacket);
            if (fullBidAsk.bidAmount[i] == 0)
                break;
            fullBidAsk.bidPrice[i] = EuroStoxx50_호가.bidPrice.plus(i * size).parseInt(rawPacket);
        }
        fullBidAsk.updateBidAskState();
        return fullBidAsk;
    }

    @Override
    public long getArrivalStamp() {
        return arrivalStamp;
    }

    public Integer getAskPrice() {
        return EuroStoxx50_호가.askPrice.parser().parseInt(rawPacket);
    }

    public Integer getAskAmount() {
        return EuroStoxx50_호가.askAmount.parser().parseInt(rawPacket);
    }

    public Integer getBidPrice() {
        return EuroStoxx50_호가.bidPrice.parser().parseInt(rawPacket);
    }

    public Integer getBidAmount() {
        return EuroStoxx50_호가.bidAmount.parser().parseInt(rawPacket);
    }
}
