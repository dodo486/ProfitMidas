package bulls.feed.current.data.stockFuture;

import bulls.data.PriceInfo;
import bulls.db.influxdb.InfluxDBData;
import bulls.feed.abstraction.BidAskFillUpdater;
import bulls.feed.abstraction.Feed;
import bulls.feed.abstraction.체결Feed;
import bulls.feed.current.enums.FeedTRCode;
import bulls.feed.current.parser.stockFuture.SF_체결;
import bulls.log.DefaultLogger;
import bulls.order.enums.LongShort;

import java.io.UnsupportedEncodingException;

public class SF_체결Feed extends Feed implements 체결Feed, InfluxDBData {
    public SF_체결Feed(FeedTRCode trCode, byte[] packet) {
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
        info.amount = getAmount();
        info.longShort = getLongShort();
        info.isCreatedWithBidAsk = false;

        info.totalPrice = getTotalPrice();
        info.totalAmount = accVolume;
        dc.handleDerivContract(info);
        dc.updatePriceInfo(info);
    }


    public byte[] getCodeByte() {
        return SF_체결.isinCode.parser().parseByte(rawPacket);
    }

    public String getCode() {
        try {
            return SF_체결.isinCode.parser().parseStr(rawPacket);
        } catch (UnsupportedEncodingException e) {
            DefaultLogger.logger.error("error found", e);
        }

        return new String(getCodeByte());
    }

    @Override
    public String getRepresentingCode() {
        return getCode();
    }

    @Override
    public String getBoardId() {
        return SF_체결.boardId.parser().parseStr(rawPacket, "");
    }

    @Override
    public Integer getPrice() {
        return SF_체결.price.parser().parseIntWithLeadingSign(rawPacket);
    }

    @Override
    public Integer getAmount() {
        return SF_체결.amount.parser().parseInt(rawPacket);
    }

    public LongShort getLongShort() {
        return LongShort.getFromByteValue(SF_체결.buySellSign.parser().parseSingleByte(rawPacket));
    }

    public Long getTotalPrice() {
        return SF_체결.totalPrice.parser().parseLong(rawPacket);
    }

    public Long getTotalAmount() {
        return SF_체결.totalAmount.parser().parseLong(rawPacket);
    }

    @Override
    public long getArrivalStamp() {
        return arrivalStamp;
    }

}
