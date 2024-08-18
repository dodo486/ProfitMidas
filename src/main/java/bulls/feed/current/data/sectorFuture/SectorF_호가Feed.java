package bulls.feed.current.data.sectorFuture;

import bulls.data.bidAsk.BidAskCore;
import bulls.data.bidAsk.BidAskFactory;
import bulls.feed.abstraction.BidAskFillUpdater;
import bulls.feed.abstraction.Feed;
import bulls.feed.abstraction.호가Feed;
import bulls.feed.current.enums.FeedTRCode;
import bulls.feed.current.parser.sectorFuture.SECTOR_호가;

public class SectorF_호가Feed extends Feed implements 호가Feed {

    public SectorF_호가Feed(FeedTRCode trCode, byte[] packet) {
        super(trCode, packet);
    }

    @Override
    public void updateBidAskFill(BidAskFillUpdater dc) {
        BidAskCore ba = getFullBidAsk(dc.getBidAskFactory());
        int expPrice = SECTOR_호가.expectedPrice.parser().parseInt(rawPacket);
        // sector 선물같이 한가한 종목은 장중에 호가가 없는 경우도 있다. 이 경우 예상체결가도 0 이므로 반영되어서는 안된다.
        if (expPrice != 0)
            ba = dc.getBidAskFactory().ofAmtOne(ba.isinCode, expPrice, expPrice);

        ba.feedStamp = arrivalStamp;
        dc.updateDerivBidAskMap(ba.isinCode, ba);
    }

    public byte[] getCodeByte() {
        return SECTOR_호가.isinCode.parser().parseByte(rawPacket);
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
        return SECTOR_호가.boardId.parser().parseStr(rawPacket, "");
    }

    public BidAskCore getFullBidAsk(BidAskFactory factory) {
        int size = 17;
        BidAskCore fullBidAsk = factory.getBidAsk(DERIV_MAX_BIDASK_DEPTH);
        fullBidAsk.isinCode = getCode();
        for (int i = 0; i < DERIV_MAX_BIDASK_DEPTH; i++) {
            fullBidAsk.askAmount[i] = SECTOR_호가.askAmount.plus(i * size).parseInt(rawPacket);
            if (fullBidAsk.askAmount[i] == 0)
                break;
            fullBidAsk.askPrice[i] = SECTOR_호가.askPrice.plus(i * size).parseIntWithLeadingSign(rawPacket);
        }
        for (int i = 0; i < DERIV_MAX_BIDASK_DEPTH; i++) {
            fullBidAsk.bidAmount[i] = SECTOR_호가.bidAmount.plus(i * size).parseInt(rawPacket);
            if (fullBidAsk.bidAmount[i] == 0)
                break;
            fullBidAsk.bidPrice[i] = SECTOR_호가.bidPrice.plus(i * size).parseIntWithLeadingSign(rawPacket);
        }
        fullBidAsk.updateBidAskState();
        return fullBidAsk;
    }

    @Override
    public long getArrivalStamp() {
        return arrivalStamp;
    }

    public Integer getAskPrice() {
        return SECTOR_호가.askPrice.parser().parseIntWithLeadingSign(rawPacket);
    }

    public Integer getAskAmount() {
        return SECTOR_호가.askAmount.parser().parseInt(rawPacket);
    }

    public Integer getBidPrice() {
        return SECTOR_호가.bidPrice.parser().parseIntWithLeadingSign(rawPacket);
    }

    public Integer getBidAmount() {
        return SECTOR_호가.bidAmount.parser().parseInt(rawPacket);
    }
}
