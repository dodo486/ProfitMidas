package bulls.feed.current.data.EuroStoxx50;

import bulls.data.PriceInfo;
import bulls.feed.abstraction.BidAskFillUpdater;
import bulls.feed.abstraction.Feed;
import bulls.feed.abstraction.체결Feed;
import bulls.feed.current.enums.FeedTRCode;
import bulls.feed.current.parser.euroStoxx50.EuroStoxx50_체결;
import bulls.log.DefaultLogger;
import bulls.order.enums.LongShort;

import java.io.UnsupportedEncodingException;

public class EuroStoxx50_체결Feed extends Feed implements 체결Feed {

    public EuroStoxx50_체결Feed(FeedTRCode trCode, byte[] packet) {
        super(trCode, packet);
    }

    @Override
    public void updateBidAskFill(BidAskFillUpdater dc) {
        PriceInfo info = new PriceInfo();
        info.feedStamp = arrivalStamp;
        info.isinCode = getCode();
        info.price = getPrice();
        info.longShort = getLongShort();
        info.amount = getAmount();

        info.totalPrice = getTotalPrice();
        info.totalAmount = getTotalAmount();
        dc.updatePriceInfo(info);
    }

    public byte[] getCodeByte() {
        return EuroStoxx50_체결.isinCode.parser().parseByte(rawPacket);
    }

    public String getCode() {
        try {
            return EuroStoxx50_체결.isinCode.parser().parseStr(rawPacket);
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
        return EuroStoxx50_체결.boardId.parser().parseStr(rawPacket, "");
    }

    @Override
    public Integer getPrice() {
        return EuroStoxx50_체결.price.parser().parseInt(rawPacket);
    }

    @Override
    public Integer getAmount() {
        return EuroStoxx50_체결.amount.parser().parseInt(rawPacket);
    }

    public LongShort getLongShort() {
        return LongShort.getFromByteValue(EuroStoxx50_체결.buySellSign.parser().parseSingleByte(rawPacket));
    }

    public Long getTotalPrice() {
        return EuroStoxx50_체결.totalPrice.parser().parseLong(rawPacket) * 1000;
    }

    public Long getTotalAmount() {
        return EuroStoxx50_체결.totalAmount.parser().parseLong(rawPacket);
    }

    @Override
    public long getArrivalStamp() {
        return arrivalStamp;
    }
}
