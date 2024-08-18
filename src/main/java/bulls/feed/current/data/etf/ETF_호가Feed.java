package bulls.feed.current.data.etf;

import bulls.data.bidAsk.BidAskCore;
import bulls.data.bidAsk.BidAskFactory;
import bulls.feed.abstraction.BidAskFillUpdater;
import bulls.feed.abstraction.Feed;
import bulls.feed.abstraction.호가Feed;
import bulls.feed.current.enums.FeedTRCode;
import bulls.feed.current.parser.etf.ETF_ELW_호가;

public class ETF_호가Feed extends Feed implements 호가Feed {

    public ETF_호가Feed(FeedTRCode trCode, byte[] packet) {
        super(trCode, packet);
    }

    @Override
    public void updateBidAskFill(BidAskFillUpdater dc) {
        BidAskCore ba = getFullBidAsk(dc.getBidAskFactory());
        ba.feedStamp = arrivalStamp;
        dc.updateEquityBidAskMap(ba.isinCode, ba);
    }

    public byte[] getCodeByte() {
        return ETF_ELW_호가.isinCode.parser().parseByte(rawPacket);
    }

    public String getCode() {
        return new String(getCodeByte());
    }

    public byte[] getIsinByte() {
        return ETF_ELW_호가.isinCode.parser().parseByte(rawPacket);
    }

    public String getIsinCode() {
        return new String(getIsinByte());
    }

    @Override
    public String getRepresentingCode() {
        return getCode();
    }

    @Override
    public String getBoardId() {
        return ETF_ELW_호가.boardId.parser().parseStr(rawPacket, "");
    }

    public BidAskCore getFullBidAsk(BidAskFactory factory) {

        int size = 66;
        BidAskCore fullBidAsk = factory.getBidAsk(EQUITY_MAX_BIDASK_DEPTH);
        fullBidAsk.isinCode = getIsinCode();

        for (int i = 0; i < EQUITY_MAX_BIDASK_DEPTH; i++) {
            fullBidAsk.askPrice[i] = ETF_ELW_호가.askPrice.plus(i * size).parseInt(rawPacket);
            fullBidAsk.askAmount[i] = ETF_ELW_호가.askAmount.plus(i * size).parseInt(rawPacket);
            fullBidAsk.bidPrice[i] = ETF_ELW_호가.bidPrice.plus(i * size).parseInt(rawPacket);
            fullBidAsk.bidAmount[i] = ETF_ELW_호가.bidAmount.plus(i * size).parseInt(rawPacket);
            // LP호가잔량은 일단 무시
        }
        fullBidAsk.accVolume = getVolume();
        fullBidAsk.updateBidAskState();
        return fullBidAsk;
    }

    @Override
    public long getArrivalStamp() {
        return arrivalStamp;
    }

    public Integer getAskPrice() {
        return ETF_ELW_호가.askPrice.parser().parseInt(rawPacket);
    }

    public Integer getAskAmount() {
        return ETF_ELW_호가.askAmount.parser().parseInt(rawPacket);
    }

    public Integer getBidPrice() {
        return ETF_ELW_호가.bidPrice.parser().parseInt(rawPacket);
    }

    public Integer getBidAmount() {
        return ETF_ELW_호가.bidAmount.parser().parseInt(rawPacket);
    }

    public Integer getVolume() {
        return ETF_ELW_호가.volume.parser().parseInt(rawPacket);
    }
}
