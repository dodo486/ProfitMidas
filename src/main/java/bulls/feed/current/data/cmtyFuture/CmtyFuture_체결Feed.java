package bulls.feed.current.data.cmtyFuture;

import bulls.data.PriceInfo;
import bulls.db.influxdb.InfluxDBData;
import bulls.feed.abstraction.BidAskFillUpdater;
import bulls.feed.abstraction.Feed;
import bulls.feed.abstraction.체결Feed;
import bulls.feed.current.enums.FeedTRCode;
import bulls.feed.current.parser.cmtyFuture.CmtyFuture_체결;
import bulls.order.enums.LongShort;

public class CmtyFuture_체결Feed extends Feed implements 체결Feed, InfluxDBData {
    public CmtyFuture_체결Feed(FeedTRCode trCode, byte[] packet) {
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

        PriceInfo info = new PriceInfo();
        info.feedStamp = arrivalStamp;
        info.isinCode = code;
        info.price = getPrice();
        info.longShort = getLongShort();
        info.amount = getAmount();
        info.totalPrice = getTotalPrice();
        info.totalAmount = accVolume;
        info.isCreatedWithBidAsk = false;
        dc.handleDerivContract(info);
        dc.updatePriceInfo(info);
    }

    public String getCode() {
        return CmtyFuture_체결.isinCode.parser().parseStr(rawPacket, "");
    }

    @Override
    public String getRepresentingCode() {
        return getCode();
    }

    @Override
    public String getBoardId() {
        return CmtyFuture_체결.boardId.parser().parseStr(rawPacket, "");
    }

    @Override
    public Integer getPrice() {
        return CmtyFuture_체결.price.parser().parseIntWithLeadingSign(rawPacket);
    }

    @Override
    public Integer getAmount() {
        return CmtyFuture_체결.amount.parser().parseInt(rawPacket);
    }

    public LongShort getLongShort() {
        return LongShort.getFromByteValue(CmtyFuture_체결.buySellSign.parser().parseSingleByte(rawPacket));
    }

    @Override
    public long getArrivalStamp() {
        return arrivalStamp;
    }

    public Long getTotalPrice() {
        return CmtyFuture_체결.totalPrice.parser().parseLong(rawPacket);
    }

    public Long getTotalAmount() {
        return CmtyFuture_체결.totalAmount.parser().parseLong(rawPacket);
    }

}
