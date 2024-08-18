package bulls.feed.current.data.stockOption;

import bulls.data.PriceInfo;
import bulls.db.influxdb.InfluxDBData;
import bulls.feed.abstraction.BidAskFillUpdater;
import bulls.feed.abstraction.Feed;
import bulls.feed.abstraction.체결Feed;
import bulls.feed.current.enums.FeedTRCode;
import bulls.feed.current.parser.stockOption.SO_체결;
import bulls.order.enums.LongShort;

public class SO_체결Feed extends Feed implements 체결Feed, InfluxDBData {

    public SO_체결Feed(FeedTRCode trCode, byte[] packet) {
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
        info.isinCode = getCode();
        info.price = getPrice();
        info.amount = getAmount();
        info.longShort = getLongShort();
        info.isCreatedWithBidAsk = false;

        info.totalPrice = getTotalPrice();
        info.totalAmount = getTotalAmount();
        dc.handleDerivContract(info);
        dc.updatePriceInfo(info);
    }

    public String getCode() {
        return SO_체결.isinCode.parser().parseStr(rawPacket, "");
    }

    @Override
    public String getRepresentingCode() {
        return getCode();
    }

    @Override
    public String getBoardId() {
        return SO_체결.boardId.parser().parseStr(rawPacket, "");
    }

    @Override
    public Integer getPrice() {
        return SO_체결.price.parser().parseInt(rawPacket);
    }

    @Override
    public Integer getAmount() {
        return SO_체결.amount.parser().parseInt(rawPacket);
    }

    public LongShort getLongShort() {
        return LongShort.getFromByteValue(SO_체결.buySellSign.parser().parseSingleByte(rawPacket));
    }

    public Long getTotalPrice() {
        return SO_체결.totalPrice.parser().parseLong(rawPacket);
    }

    public Long getTotalAmount() {
        return SO_체결.totalAmount.parser().parseLong(rawPacket);
    }

    @Override
    public long getArrivalStamp() {
        return arrivalStamp;
    }

}

