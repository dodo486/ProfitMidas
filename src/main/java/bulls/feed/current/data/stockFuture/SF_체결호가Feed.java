package bulls.feed.current.data.stockFuture;

import bulls.data.PriceInfo;
import bulls.data.bidAsk.BidAskCore;
import bulls.data.bidAsk.BidAskFactory;
import bulls.db.influxdb.InfluxDBData;
import bulls.feed.abstraction.BidAskFillUpdater;
import bulls.feed.abstraction.Feed;
import bulls.feed.abstraction.체결Feed;
import bulls.feed.abstraction.호가Feed;
import bulls.feed.current.enums.FeedTRCode;
import bulls.feed.current.parser.stockFuture.SF_체결호가;
import bulls.order.enums.LongShort;

public class SF_체결호가Feed extends Feed implements 체결Feed, 호가Feed, InfluxDBData {
    static int bidAskLength = SF_체결호가.bidPrice.parser().getLength() + SF_체결호가.bidSign.parser().getLength() + SF_체결호가.bidAmount.parser().getLength();

    public SF_체결호가Feed(FeedTRCode trCode, byte[] packet) {
        super(trCode, packet);
    }

    @Override
    public void updateBidAskFill(BidAskFillUpdater dc) {
        String code = getCode();
        long accVolume = getTotalAmount();
        if (dc.isOutDatedAccVolume(code, accVolume)) {
            // 지각 패킷은 아무것도 하지 않는다.
            return;
        }
        BidAskCore ba = getFullBidAsk(dc.getBidAskFactory());
        ba.feedStamp = arrivalStamp;
        dc.updateDerivBidAskMap(ba.isinCode, ba);

        PriceInfo info = new PriceInfo();
        info.feedStamp = arrivalStamp;
        info.isinCode = ba.isinCode;
        info.amount = getAmount();
        info.price = getPrice();
        info.longShort = getLongShort();

        info.totalPrice = getTotalPrice();
        info.totalAmount = accVolume;
        dc.updatePriceInfo(info);
    }

    public byte[] getCodeByte() {
        return SF_체결호가.isinCode.parser().parseByte(rawPacket);
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
        return SF_체결호가.boardId.parser().parseStr(rawPacket, "");
    }

    @Override
    public Integer getPrice() {
        return SF_체결호가.price.parser().parseIntWithLeadingSign(rawPacket);
    }

    @Override
    public Integer getAmount() {
        return SF_체결호가.amount.parser().parseInt(rawPacket);
    }

    public Integer getAskPrice() {
        return SF_체결호가.askPrice.parser().parseIntWithLeadingSign(rawPacket);
    }

    public Integer getAskAmount() {
        return SF_체결호가.askAmount.parser().parseInt(rawPacket);
    }

    public Integer getBidPrice() {
        return SF_체결호가.bidPrice.parser().parseIntWithLeadingSign(rawPacket);
    }

    public Integer getBidAmount() {
        return SF_체결호가.bidAmount.parser().parseInt(rawPacket);
    }


    public BidAskCore getFullBidAsk(BidAskFactory factory) {
        int size = bidAskLength;
        BidAskCore fullBidAsk = factory.getBidAsk(DERIV_STOCK_MAX_BIDASK_DEPTH);
        fullBidAsk.isinCode = getCode();
        for (int i = 0; i < DERIV_STOCK_MAX_BIDASK_DEPTH; i++) {
            fullBidAsk.askAmount[i] = SF_체결호가.askAmount.plus(i * size).parseInt(rawPacket);
            if (fullBidAsk.askAmount[i] == 0)
                break;
            fullBidAsk.askPrice[i] = SF_체결호가.askPrice.plus(i * size).parseIntWithLeadingSign(rawPacket);
        }
        for (int i = 0; i < DERIV_STOCK_MAX_BIDASK_DEPTH; i++) {
            fullBidAsk.bidAmount[i] = SF_체결호가.bidAmount.plus(i * size).parseInt(rawPacket);
            if (fullBidAsk.bidAmount[i] == 0)
                break;
            fullBidAsk.bidPrice[i] = SF_체결호가.bidPrice.plus(i * size).parseIntWithLeadingSign(rawPacket);
        }
        fullBidAsk.updateBidAskState();
        return fullBidAsk;
    }

    public LongShort getLongShort() {
        return LongShort.getFromByteValue(SF_체결호가.buySellSign.parser().parseSingleByte(rawPacket));
    }

    public Long getTotalPrice() {
        return SF_체결호가.totalPrice.parser().parseLong(rawPacket);
    }

    public Long getTotalAmount() {
        return SF_체결호가.totalAmount.parser().parseLong(rawPacket);
    }

    @Override
    public long getArrivalStamp() {
        return arrivalStamp;
    }

}
